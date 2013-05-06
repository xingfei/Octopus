package com.github.xingfei.octopus;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.github.xingfei.octopus.dht.ConsistentHash;
import com.github.xingfei.octopus.pool.ConnectionPool;
import com.github.xingfei.octopus.redis.RedisReply;
import com.github.xingfei.octopus.redis.RedisRequest;

/**
 * this is where to handle the redis request. 
 * for those command that don't need to
 * send to backend servers, reply them immediately.
 * @author xingfei
 *
 */
public class RedisRequestHandler extends SimpleChannelHandler {
	private ConcurrentHashMap<String, ConnectionPool> pools;
	private ConsistentHash dht;
	private ClientBootstrap bootstrap;

	public RedisRequestHandler(ConcurrentHashMap<String, ConnectionPool> pools,
			ConsistentHash dht, ClientBootstrap bootstrap) {
		super();
		this.pools = pools;
		this.dht = dht;
		this.bootstrap = bootstrap;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final RedisRequest request = (RedisRequest) e.getMessage();
		if (request.canceled()) {
			return;
		}
		request.setClientChannel(e.getChannel());
		String command = request.getCommand().toUpperCase();
		switch (command) {
		case "PING":
			request.sendToClient(RedisReply.status("PONG"));
			break;
		case "QUIT":
			ChannelFuture future = request
					.sendToClient(RedisReply.status("OK"));
			if (future != null) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
			break;
		case "VERBOSE": // a special command to enable/disable debugger
			Debugger.noisy = Boolean.valueOf(request.getKey());
		case "SELECT": // "select db" will have no effect
			request.sendToClient(RedisReply.status("OK"));
			break;
		default:
			String key = request.getKey();
			BackendServer server = dht.find(key);
			String name = server.getKey();

			pools.putIfAbsent(name, new ConnectionPool((BackendServer)server, bootstrap));
			ConnectionPool _pool = pools.get(name);
			_pool.handleRequest(request);
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		Channel channel = ctx.getChannel();
		Object o = channel.getAttachment();
		if(o != null) {
			((RedisRequest)o).cancel();
		}
	}
	
	
}

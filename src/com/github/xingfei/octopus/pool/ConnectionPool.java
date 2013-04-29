package com.github.xingfei.octopus.pool;

import java.net.InetSocketAddress;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.github.xingfei.octopus.BackendServer;
import com.github.xingfei.octopus.Debugger;
import com.github.xingfei.octopus.defer.Callback;
import com.github.xingfei.octopus.defer.Deferred;
import com.github.xingfei.octopus.defer.Errback;
import com.github.xingfei.octopus.redis.RedisRequest;

public class ConnectionPool {
	public static final int MAX_CONN = 3;
	private int maxConnection;
	private Semaphore semaphore;
	private Stack<RedisConnection> connections;
	private InetSocketAddress backendAddr;
	private ClientBootstrap bootstrap;
	private ConcurrentLinkedQueue<RedisRequest> queue;
	
	public ConnectionPool(BackendServer server, ClientBootstrap bootstrap, int maxConn) {
		super();
		this.maxConnection = maxConn;
		this.semaphore = new Semaphore(this.maxConnection);
		this.backendAddr = server.getAddress();
		this.bootstrap = bootstrap;
		this.connections = new Stack<>();
		this.queue = new ConcurrentLinkedQueue<>();
	}
	
	public ConnectionPool(BackendServer server, ClientBootstrap bootstrap) {
		this(server, bootstrap, MAX_CONN);
	}
	
	public Deferred<RedisConnection> getConnection() {
		if(semaphore.tryAcquire()) {
			if(connections.empty()) {
				final Deferred<RedisConnection> defer = new Deferred<>();
				ChannelFuture future = bootstrap.connect(backendAddr);
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture cf) throws Exception {
						Channel channel = cf.getChannel();
						RedisConnection conn = new RedisConnection(ConnectionPool.this, channel);
						channel.setAttachment(conn);
						defer.callback(conn);
					}
				});
				return defer;
			} else {
				RedisConnection conn = connections.pop();
				return Deferred.succeed(conn);
			}
		} else {
			Debugger.debug("failed to acquire semaphore");
			return Deferred.fail(new RuntimeException("failed to acqure semaphore"));
		}
	}

	public void queueRequest(RedisRequest request) {
		this.queue.offer(request);
	}

	public void releaseConnection(RedisConnection redisConnection) {
		Debugger.debug("releasing connection");
		redisConnection.setRedisRequest(null);
		connections.push(redisConnection);
		semaphore.release();
		
		if(!this.queue.isEmpty()) {
			RedisRequest request = this.queue.poll();
			if(request != null) {
				handleRequest(request);
			}
		}
	}
	
	public void handleRequest(final RedisRequest request) {
		if(request.canceled()) {
			return;
		}
		Deferred<RedisConnection> defer = getConnection();
		
		Callback callback = new Callback() {
			@Override
			public Object callback(Object t, Object... args) {
				request.sendToBackend((RedisConnection)t);
				return null;
			}
			
		};
		
		Errback errback = new Errback() {
			@Override
			public Object error(Object t, Object... args) {
				queueRequest(request);
				return null;
			}
			
		};
		defer.addCallback(callback);
		defer.addErrback(errback);
	}

	public void releaseBrokenConnection(RedisConnection redisConnection) {
		this.semaphore.release();
	}

}

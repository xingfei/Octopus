package com.github.xingfei.octopus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;

import com.github.xingfei.octopus.dht.ConsistentHash;
import com.github.xingfei.octopus.pool.ConnectionPool;
import com.github.xingfei.octopus.redis.RedisRequestDecoder;

/**
 * The serverside ChannelPipelineFactory implementation
 * @author xingfei
 *
 */
public class ServerPipelineFactory implements ChannelPipelineFactory {

	private ConcurrentHashMap<String, ConnectionPool> pools;
	private ConsistentHash dht;
	private ClientBootstrap bootstrap;

	public ServerPipelineFactory(ServerConfig config,
			ChannelFactory clientChannelFactory) {
		this.pools = new ConcurrentHashMap<>();
		List<BackendServer> backends = new ArrayList<>();
		for(String serverName: config.servers.keySet()) {
			ServerConfig.Server server = config.servers.get(serverName);
			String[] t = server.address.split(":");
			BackendServer backend = new BackendServer(t[0], Integer.parseInt(t[1]), server.weight);
			backends.add(backend);
		}
		this.dht = new ConsistentHash(backends, config.virtualNode);
		this.bootstrap = new ClientBootstrap(clientChannelFactory);
		this.bootstrap.setPipelineFactory(new ClientPipelineFactory());
	}

	private RedisRequestHandler createRequestHandler() {
		return new RedisRequestHandler(pools, dht, bootstrap);
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, true,
				Delimiters.lineDelimiter()));
		pipeline.addLast("linedecoder", new StringDecoder());
		pipeline.addLast("requestdecoder", new RedisRequestDecoder());
		pipeline.addLast("handler", createRequestHandler());
		return pipeline;
	}

}

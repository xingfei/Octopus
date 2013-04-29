package com.soooner.rss;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;

import com.soooner.rss.dht.DistributeHashTable;
import com.soooner.rss.pool.ConnectionPool;
import com.soooner.rss.redis.RedisRequestDecoder;

public class ServerPipelineFactory implements ChannelPipelineFactory {

	private ConcurrentHashMap<String, ConnectionPool> pools;
	private DistributeHashTable<BackendServer> dht;
	private ClientBootstrap bootstrap;

	public ServerPipelineFactory(ServerConfig config,
			ChannelFactory clientChannelFactory) {
		this.pools = new ConcurrentHashMap<>();
		this.dht = new DistributeHashTable<>(config.getServers(), config.getVirtualNodes());
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

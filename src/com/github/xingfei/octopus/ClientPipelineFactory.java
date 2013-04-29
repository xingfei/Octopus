package com.github.xingfei.octopus;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;

import com.github.xingfei.octopus.redis.BackendToClientTransfer;
import com.github.xingfei.octopus.redis.RedisReplyDecoder;

public class ClientPipelineFactory implements ChannelPipelineFactory {
	
	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("transfer", new BackendToClientTransfer());
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, true, Delimiters.lineDelimiter()));
		pipeline.addLast("linedecoder", new StringDecoder());
		pipeline.addLast("replydecoder", new RedisReplyDecoder());
		pipeline.addLast("handler", new RedisResponseHandler());
		return pipeline;
	}

}

package com.soooner.rss.redis;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

import com.soooner.rss.pool.RedisConnection;

public class BackendToClientTransfer implements ChannelUpstreamHandler {

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent ce)
			throws Exception {
		if(!(ce instanceof MessageEvent)) {
			ctx.sendUpstream(ce);
			return;
		}
		MessageEvent e = (MessageEvent)ce;
		RedisConnection conn = (RedisConnection)e.getChannel().getAttachment();
		ChannelBuffer cb = (ChannelBuffer)e.getMessage();
		conn.replyFromBackend(cb.copy());
		
		ctx.sendUpstream(ce);
	}

}

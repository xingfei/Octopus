package com.soooner.rss;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.soooner.rss.pool.RedisConnection;

public class RedisResponseHandler extends SimpleChannelHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		RedisConnection conn = (RedisConnection) e.getChannel().getAttachment();
		conn.release();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace(System.err);
		RedisConnection conn = (RedisConnection) ctx.getChannel()
				.getAttachment();
		conn.releaseBroken();
		/*
		 * ChannelFuture future = ctx.getChannel().disconnect();
		 * future.addListener(new ChannelFutureListener() {
		 * 
		 * @Override public void operationComplete(ChannelFuture cf) throws
		 * Exception { RedisConnection conn =
		 * (RedisConnection)cf.getChannel().getAttachment();
		 * conn.releaseBroken(); } });
		 */
	}

}

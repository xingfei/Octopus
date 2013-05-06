package com.github.xingfei.octopus.pool;

import org.jboss.netty.channel.Channel;

import com.github.xingfei.octopus.Debugger;
import com.github.xingfei.octopus.redis.RedisRequest;

public class RedisConnection {
	private ConnectionPool pool;
	private Channel backendChannel;
	private RedisRequest request;
	
	public RedisConnection(ConnectionPool pool, Channel backendChannel) {
		super();
		this.pool = pool;
		this.backendChannel = backendChannel;
	}
	
	public void release() {
		pool.releaseConnection(this);
	}
	
	public void setRedisRequest(RedisRequest request) {
		this.request = request;
	}

	public void requestFromClient(Object msg) {
		Debugger.debug(msg, "requestFromClient");
		backendChannel.write(msg);
	}

	public void replyFromBackend(Object msg) {
		request.sendToClient(msg);
	}

	public void releaseBroken() {
		pool.releaseBrokenConnection(this);
	}
}

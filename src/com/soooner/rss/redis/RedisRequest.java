/**
 * 
 */
package com.soooner.rss.redis;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.soooner.rss.Debugger;
import com.soooner.rss.pool.RedisConnection;

/**
 * @author xingfei
 * 
 */
public class RedisRequest {
	private ChannelBuffer buffer = ChannelBuffers.buffer(1024);
	private List<String> args;
	private String key;
	private Channel clientChannel;
	private String command;
	private boolean canceled = false;

	public RedisRequest(ChannelBuffer buffer,
			List<String> args) {
		super();
		this.buffer = buffer;
		this.args = args;
		this.command = this.args.get(0);
		if (this.args.size() > 1) {
			this.key = this.args.get(1);
		}
	}

	public void cancel() {
		canceled = true;
	}
	
	public boolean canceled() {
		return canceled;
	}

	public String getCommand() {
		return command;
	}

	public String getKey() {
		return key;
	}
	
	public void setClientChannel(Channel channel) {
		this.clientChannel = channel;
	}
	
	public ChannelFuture sendToClient(Object reply) {
		if(!canceled) {
			return this.clientChannel.write(reply);
		}
		return null;
	}

	public void sendToBackend(RedisConnection conn) {
		if (!canceled) {
			Debugger.debug(buffer, "sending to backend");
			conn.setRedisRequest(this);
			conn.requestFromClient(buffer);
		}
	}

}

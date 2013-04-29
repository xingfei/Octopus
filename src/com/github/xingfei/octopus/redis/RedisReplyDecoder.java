package com.github.xingfei.octopus.redis;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.github.xingfei.octopus.Debugger;


public class RedisReplyDecoder extends OneToOneDecoder {
	private int argCount = 1;
	private int receivedArgCount = 0;
	private int argLength = 0;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		String line = (String)msg;
		
		char c = line.charAt(0);
		switch(c) {
		case '+': // status
		case '-': // error
		case ':': // integer
			receivedArgCount++;
			break;
		case '$':
			int n = Integer.parseInt(line.substring(1));
			if(n == -1) { // nil
				argCount = 1;
				receivedArgCount++;
			} else { // bulk
				argLength = n;
			}
			break;
		case '*': // multi bulk reply
			argCount = Integer.parseInt(line.substring(1));
			break;
		default:
			if(line.length() != argLength) {
				throw new RuntimeException("line.lenght != argLength");
			}
			receivedArgCount++;
		}
		Debugger.debug("receivedArgCount " + receivedArgCount + " ArgCount " + argCount);
		if(receivedArgCount == argCount) {
			argCount = 1;
			receivedArgCount = 0;
			return Boolean.TRUE;
		}
		return null;
	}


}

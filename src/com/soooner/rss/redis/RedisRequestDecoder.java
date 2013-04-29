/**
 * 
 */
package com.soooner.rss.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * @author xingfei
 * 
 */
public class RedisRequestDecoder extends OneToOneDecoder {
	enum DecoderState {
		ARGC, ARG_LEN, ARG
	}
	
	public static final byte[] CRLF = {(byte)'\r', (byte)'\n'};

	private ChannelBuffer decodeBuffer;
	private List<String> args = new LinkedList<String>();
	private DecoderState state;
	private int argCount = 1;

	public RedisRequestDecoder() {
		super();
		this.decodeBuffer = ChannelBuffers.dynamicBuffer();
		this.state = DecoderState.ARGC;
	}

	@Override
	protected Object decode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		String line = (String)msg;
		decodeBuffer.writeBytes(line.getBytes());
		decodeBuffer.writeBytes(CRLF);

		RedisRequest request = null;
		if(this.state == DecoderState.ARGC && line.length() == 0) { // request is only a blank line, ignore it
			this.decodeBuffer.clear();
			return request;
		}
		char c = line.charAt(0);
		switch (this.state) {
		case ARGC:
			if (c == '*') {
				argCount = Integer.parseInt(line.substring(1));
				this.state = DecoderState.ARG_LEN;
			} else { // inline request
				String[] t = line.split("\\s");
				request = new RedisRequest(decodeBuffer.copy(),
						Arrays.asList(t));
			}
			break;
		case ARG_LEN:
			this.state = DecoderState.ARG;
			break;
		case ARG:
			args.add(line);
			if (args.size() == argCount) {
				request = new RedisRequest(decodeBuffer.copy(),
						new ArrayList<>(args));
			} else {
				this.state = DecoderState.ARG_LEN;
			}

			break;
		}
		if (request != null) {
			this.state = DecoderState.ARGC;
			this.decodeBuffer.clear();
			this.argCount = 1;
			this.args.clear();
		}
		return request;
	}

}

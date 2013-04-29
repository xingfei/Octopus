package com.soooner.rss.redis;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class RedisReply {
	public static final byte[] CRLF = "\r\n".getBytes();
	public static final int STATUS = '+';
	public static final int ERROR = '-';
	public static final int INTEGER = ':';
	public static final int BULK = '$';
	public static final int MULTI = '*';
	public static final int BLANK = ' ';

	public static ChannelBuffer error(String kind, String message) {
		byte[] b1 = kind.getBytes();
		byte[] b2 = message.getBytes();
		ChannelBuffer cb = ChannelBuffers.buffer(1 + b1.length + 1 + b2.length + CRLF.length);
		cb.writeByte(ERROR);
		cb.writeBytes(b1);
		cb.writeByte(BLANK);
		cb.writeBytes(b2);
		cb.writeBytes(CRLF);
		return cb;
	}

	public static ChannelBuffer status(String message) {
		byte[] b = message.getBytes();
		ChannelBuffer cb = ChannelBuffers.buffer(1 + b.length + CRLF.length);
		cb.writeByte(STATUS);
		cb.writeBytes(b);
		cb.writeBytes(CRLF);
		return cb;
	}
}

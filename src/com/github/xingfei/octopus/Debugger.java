package com.github.xingfei.octopus;

import org.jboss.netty.buffer.ChannelBuffer;

public class Debugger {
	
	public static boolean noisy = false;
	
	public static void debug(Object...strings) {
		if(!noisy) {
			return;
		}
		for(Object s: strings) {
			if(s instanceof ChannelBuffer) {
				dumpBuffer((ChannelBuffer)s);
			} else {
				System.out.println(s);
			}
		}
	}
	
	public static void dumpBuffer(ChannelBuffer cb) {
		if(!noisy) {
			return;
		}
		ChannelBuffer buffer = cb.copy();
		int n = buffer.readableBytes();
		byte[] b = new byte[n];
		buffer.readBytes(b);
		
		for(byte _b : b) {
			char c = (char)_b;
			if(c == '\r') {
				System.out.print("\\r");
			} else if(c == '\n') {
				System.out.println("\\n");
			} else {
				System.out.print(c);
			}
		}
	}

}

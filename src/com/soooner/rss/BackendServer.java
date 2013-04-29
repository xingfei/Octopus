package com.soooner.rss;

import java.net.InetSocketAddress;

import com.soooner.rss.dht.KeyedObject;

public class BackendServer implements KeyedObject {
	public static final int DEFAULT_WEIGHT = 3;
	private String host;
	private int port;
	private int weight;
	private String name;
	public BackendServer(String host, int port, int weight, String name) {
		super();
		this.host = host;
		this.port = port;
		this.weight = weight;
		this.name = name;
	}
	public BackendServer(String host, int port, int weight) {
		this(host, port, weight, host + ":" + port);
	}
	public BackendServer(String host, int port, String name) {
		this(host, port, DEFAULT_WEIGHT, name);
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public int getWeight() {
		return this.weight;
	}
	public InetSocketAddress getAddress() {
		return new InetSocketAddress(host, port);
	}
	public String getKey() {
		return name;
	}
}

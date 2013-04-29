package com.soooner.rss;

import java.io.IOException;
import java.util.List;

public class ServerConfig {
	private int port = 6400;
	private int virtualNodes = 200;
	
	public List<BackendServer> getServers() {
		return null;
	}
	
	public int getPort() {
		return port;
	}

	public int getVirtualNodes() {
		return virtualNodes;
	}

	public void read(String filepath) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

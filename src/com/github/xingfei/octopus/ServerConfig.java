package com.github.xingfei.octopus;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * @author xingfei
 * 
 */
public class ServerConfig {
	public static class Server {
		public String address;
		public int check;
		public int weight;
		public int dbs;
	}
	public String bind;
	public int maxClients = 5000;
	public int virtualNode = 200;
	public Map<String, Server> servers;

	public static ServerConfig load(String filepath) throws IOException {
		try (FileInputStream fin = new FileInputStream(filepath)) {
			Yaml yaml = new Yaml();
			ServerConfig sc = yaml.loadAs(fin, ServerConfig.class);
			return sc;
		}
	}

	public static void main(String[] args) {
		try {
			ServerConfig sc = load("exec/redis-sharding.conf");
			for(String serverName: sc.servers.keySet()) {
				System.out.println(serverName);
				Server s = sc.servers.get(serverName);
				System.out.println("  address: " + s.address);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

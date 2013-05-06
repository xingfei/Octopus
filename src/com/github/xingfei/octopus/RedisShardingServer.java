package com.github.xingfei.octopus;

import java.io.File;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class RedisShardingServer {
	static final String version = "RedisShardingServer v0.1";
	private static final RedisShardingServer server = new RedisShardingServer();

	public static void main(String[] args) {
		int n = args.length;
		String s = null;
		String filepath = "/etc/redis-sharding.conf";
		for (int i = 0; i < n;) {
			s = args[i];
			switch (s) {
			case "-f":
				if (i + 1 < args.length) {
					filepath = args[i + 1];
				} else {
					System.err.println("no valid port supported");
					return;
				}
				i += 2;
				break;
			case "-verbose":
				Debugger.noisy = true;
				i++;
				break;
			case "-V":
				System.out.println(version);
				return;
			default:
				System.err.println("invalid argument:" + s);
				return;
			}
		}

		File f = new File(filepath);
		if (!f.exists()) {
			System.err.println("config file " + filepath + " does not exist");
			return;
		}

		try {
			ServerConfig config = ServerConfig.load(filepath);
			server.start(config);
		} catch (Exception e) {

		}
	}

	private void start(ServerConfig config) {
		ChannelFactory serverChannelFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ChannelFactory clientChannelFactory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		// 配置服务器-使用java线程池作为解释线程
		ServerBootstrap bootstrap = new ServerBootstrap(serverChannelFactory);
		// 设置 pipeline factory.
		bootstrap.setPipelineFactory(new ServerPipelineFactory(config, clientChannelFactory));
		// 绑定端口
		//bootstrap.bind(new InetSocketAddress(config.getPort()));
		//Debugger.debug("redis-sharding start on " + config.getPort());
	}

}

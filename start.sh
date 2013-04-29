#!/bin/sh

java -cp bin:lib/netty-3.5.7.Final.jar com.soooner.rss.RedisShardingServer $@

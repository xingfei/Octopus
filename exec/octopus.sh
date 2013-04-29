#!/bin/sh

MAIN_CLASS=com.github.xingfei.octopus.RedisShardingServer
SELF=`readlink -f $0`
BIN_DIR=`dirname $SELF`
OCTOPUS_HOME=`dirname $BIN_DIR`
cd $OCTOPUS_HOME

CP=.
for jarfile in lib/*.jar;
do
    CP=$CP:$jarfile
done

java -cp $CP $MAIN_CLASS -f bin/redis-sharding.conf


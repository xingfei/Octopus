Octopus
=======

Octopus is a redis sharding server, written in java and jboss-netty.

Octopus does sharding according to the key's hash, using a consistent-hash algorithm.

How to build
============
use ant to build octopus
$ ant dist

Usage:
======
after dist, execute a command to start octopus
./dist/bin/octopus.sh

you can copy dist directory to any path you want.
then execute the bin/octopus.sh to start it.


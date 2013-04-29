package com.github.xingfei.octopus.defer;

public interface Errback {
	public Object error(Object t, Object... args);
}

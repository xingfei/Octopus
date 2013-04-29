package com.soooner.rss.defer;

public interface Errback {
	public Object error(Object t, Object... args);
}

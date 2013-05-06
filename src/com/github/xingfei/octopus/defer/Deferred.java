package com.github.xingfei.octopus.defer;

import java.util.ArrayList;
import java.util.List;

/**
 * a simplified defer implementation borrowed from python's twisted framework
 * refer to http://twistedmatrix.com/documents/12.3.0/core/howto/defer.html
 * for detail
 * @author xingfei
 *
 * @param <E>
 */
public class Deferred<E> {
	static class ListenerArgs<T> {
		T listener;
		Object[] args;
		public ListenerArgs(T listener, Object[] args) {
			super();
			this.listener = listener;
			this.args = args;
		}
	}
	private Object obj;
	private Object err;
	private List<ListenerArgs<Callback>> callbacks;
	private List<ListenerArgs<Errback>> errbacks;
	
	public static <E> Deferred<E> succeed(E e) {
		Deferred<E> d = new Deferred<E>();
		d.obj = e;
		return d;
	}
	
	public static <E> Deferred<E> fail(Object e) {
		Deferred<E> d = new Deferred<E>();
		d.err = e;
		return d;
	}
	
	public Deferred() {
		this.callbacks = new ArrayList<>();
		this.errbacks = new ArrayList<>();
	}
	
	private void doCallbacks() {
		while(!callbacks.isEmpty()) {
			ListenerArgs<Callback> listener = callbacks.remove(0);
			obj = listener.listener.callback(obj, listener.args);
		}
	}
	
	private void doErrbacks() {
		while(!errbacks.isEmpty()) {
			ListenerArgs<Errback> listener = errbacks.remove(0);
			err = listener.listener.error(err, listener.args);
		}
	}
	
	public void addCallback(Callback callback, Object... args) {
		this.callbacks.add(new ListenerArgs<Callback>(callback, args));
		if(this.obj != null) {
			this.doCallbacks();
		}
	}

    public void addErrback(Errback errback, Object... args) {
    	this.errbacks.add(new ListenerArgs<Errback>(errback, args));
		if(this.err != null) {
			this.doErrbacks();
		}
	}
    
    public void callback(E obj) {
    	this.obj = obj;
    	this.doCallbacks();
    }
    
    public void errback(Object err) {
    	this.err = err;
    	this.doErrbacks();
    }
}

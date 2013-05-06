/**
 * 
 */
package com.github.xingfei.octopus.dht;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.github.xingfei.octopus.BackendServer;

/**
 * ConsistentHash is a consistent hashing implementation
 * @author xingfei
 *
 */
public class ConsistentHash {
	public static final int VIRTUAL_NODES = 200;
	private int virtualNodes;
	private TreeMap<Integer, BackendServer> map;
	private Hash hash;
	
	public ConsistentHash(List<BackendServer> servers, int virtualNodes) {
		super();
		this.virtualNodes = virtualNodes;
		this.hash = new Hash();
		map = new TreeMap<>();
		
		String key = null, virtualKey = null;
		int virtualNodeNum = 0;
		for(BackendServer bs : servers) {
			key = bs.getKey();
			virtualNodeNum = this.virtualNodes * bs.getWeight();
			for(int i = 0 ; i < virtualNodeNum ; i++) {
				virtualKey = String.format("%s:vn:%06d", key, i);
				map.put(hash.hash(virtualKey), bs);
			}
		}
	}
	
	public ConsistentHash(List<BackendServer> objects) {
		this(objects, VIRTUAL_NODES);
	}

	/**
	 * consistent hash algorithm
	 * @param key
	 * @return
	 */
	public BackendServer find(String key) {
		Integer h = hash.hash(key);
		Entry<Integer, BackendServer> entry = map.ceilingEntry(h);
		if(entry == null) {
			entry = map.firstEntry();
		}
		return entry.getValue();
	}

}

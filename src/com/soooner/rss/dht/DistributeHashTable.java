/**
 * 
 */
package com.soooner.rss.dht;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author xingfei
 *
 */
public class DistributeHashTable<T extends KeyedObject> {
	public static final int VIRTUAL_NODES = 200;
	private int virtualNodes;
	private List<T> objects;
	private TreeMap<Integer, KeyedObject> map;
	private Hash hash;
	
	public DistributeHashTable(List<T> objects, int virtualNodes) {
		super();
		this.objects = objects;
		this.virtualNodes = virtualNodes;
		this.hash = new Hash();
		map = new TreeMap<>();
		
		String key = null, virtualKey = null;
		for(KeyedObject ko: this.objects) {
			key = ko.getKey();
			for(int i = 0 ; i < this.virtualNodes ; i++) {
				virtualKey = String.format("%s:vn:%06d", key, i);
				map.put(hash.hash(virtualKey), ko);
			}
		}
	}
	
	public DistributeHashTable(List<T> objects) {
		this(objects, VIRTUAL_NODES);
	}

	/**
	 * consistent hash algorithem
	 * @param key
	 * @return
	 */
	public KeyedObject find(String key) {
		Integer h = hash.hash(key);
		Entry<Integer, KeyedObject> entry = map.ceilingEntry(h);
		if(entry == null) {
			entry = map.firstEntry();
		}
		return entry.getValue();
	}

}

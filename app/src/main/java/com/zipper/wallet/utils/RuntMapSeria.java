package com.zipper.wallet.utils;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RuntMapSeria<k,v> implements Serializable, Map<k, v> {
	Map<k,v> map;
	public RuntMapSeria(Map<k,v> map) {
		this.map = map;
		// TODO Auto-generated constructor stub
	}

	public Map<k,v> getMap() {
		return map;
	}


	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return map.containsKey(o) ;
	}

	@Override
	public boolean containsValue(Object o) {
		return map.containsValue(o);
	}

	@Override
	public v get(Object o) {
		return map.get(o);
	}

	@Override
	public v put(k k, v v) {
		return map.put((k)k,(v)v);
	}

	@Override
	public v remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(@NonNull Map<? extends k, ? extends v> map) {
		this.map.putAll(map);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@NonNull
	@Override
	public Set<k> keySet() {
		return map.keySet();
	}

	@NonNull
	@Override
	public Collection<v> values() {
		return map.values();
	}

	@NonNull
	@Override
	public Set<Entry<k, v>> entrySet() {
		return map.entrySet();
	}
}

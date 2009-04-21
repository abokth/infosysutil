package se.kth.sys.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MultivalueMap<K extends Comparable<K>,V>
{
	private Map<K,List<V>> map;
	
	public class NaturalAndNullComparator implements Comparator<K> {

		public int compare(K o1, K o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return 1;
			}
			if (o2 == null) {
				return -1;
			}
			return o1.compareTo(o2);
		}
		
	}
	
	public MultivalueMap ()
	{
		map=new TreeMap<K, List<V>>(new NaturalAndNullComparator());
	}
	
	public void clear()
	{
		map.clear();
	}

	public boolean containsKey(K key)
	{
		return map.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		for (List<V> element : map.values()) {
			if (element.contains(value)) {
				return true;
			}
		}

		return false;
	}

	public List<V> get(K key)
	{
		if(map.containsKey(key))
		{
			return map.get(key);
		}
		
		return new LinkedList<V>();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set<K> keySet()
	{
		return map.keySet();
	}

	public void put(K key, V value)
	{
		if(!map.containsKey(key))
		{
			map.put(key, new LinkedList<V>());
		}

		map.get(key).add(value);
	}

	public void putAll(Map<? extends K, ? extends V> m)
	{
		throw new UnsupportedOperationException();
	}

	public void putAll(K key, Collection<V> value)
	{
		if(!map.containsKey(key))
		{
			map.put(key, new LinkedList<V>());
		}

		map.get(key).addAll(value);
	}
	
	public List<V> remove(K key)
	{
		return map.remove(key);
	}

	public void removeFirst(K key)
	{
		if(containsKey(key)){
			get(key).remove(0);
		}
	}

	public int size()
	{
		throw new UnsupportedOperationException();
	}

	public Collection<V> values()
	{
		throw new UnsupportedOperationException();
	}
	
	public String toString()
	{
		return map.toString();
	}

	public Map<K, List<V>> asMap() {
		return map;
	}
	
}

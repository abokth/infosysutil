package se.kth.sys.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/**
 * A Map-like class that can hold several items per key.
 * @param <K> Type of keys
 * @param <V> Type of values
 */
public class MultivalueMap<K extends Comparable<K>, V> {
    private Map<K, List<V>> map;

    /**
     * Orders according to the natural ordering of the items but also handles null.
     */
    public class NaturalAndNullComparator implements Comparator<K> {

        /** {@inheritDoc} */
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

    /** Create a new (empty) MultivalueMap. */
    public MultivalueMap() {
        map = new TreeMap<K, List<V>>(new NaturalAndNullComparator());
    }

    /** Make this map empty. */
    public void clear() {
        map.clear();
    }

    /**
     * @param key a key to check for
     * @return true if the key exists in this map
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (List<V> element : map.values()) {
            if (element.contains(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the set of values for a key.
     * @param key the key to get values for
     * @return A list with the values, or an empty list.
     */
    public List<V> get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        return new LinkedList<V>();
    }

    /**
     * @return true if this MultivalueMap is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Get the set of keys that is in this map.
     * @return the set of keys
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Put a value in the map.  It is possible to put multiple values
     * with the same key in the map.
     * @param key the key to put
     * @param value the value add for key
     */
    public void put(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new LinkedList<V>());
        }

        map.get(key).add(value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    public void putAll(K key, Collection<V> value) {
        if (!map.containsKey(key)) {
            map.put(key, new LinkedList<V>());
        }

        map.get(key).addAll(value);
    }

    /**
     * Remove all values associated with a specific key from the map.
     * @param key the key to clear.
     * @return the previous values associated with key, or null if there was no mapping for key.
     */
    public List<V> remove(K key) {
        return map.remove(key);
    }

    public void removeFirst(K key) {
        if (containsKey(key)) {
            get(key).remove(0);
        }
    }

    /**
     * Count the total number of values for all keys.
     * @return the number of values
     */
    public int size() {
        int result = 0;
        for (List<V> vlist : map.values()) {
            result += vlist.size();
        }
        return result;
    }

    /**
     * Get a single Collection containing all the values, regardless of key, in this MultivalueMap.
     * @return the new summary collection
     */
    public Collection<V> values() {
        Collection<V> result = new ArrayList<V>();
        for (List<V> vlist : map.values()) {
            for (V value : vlist) {
                result.add(value);
            }
        }
        return result;
    }

    public String toString() {
        return map.toString();
    }

    public Map<K, List<V>> asMap() {
        return map;
    }

}

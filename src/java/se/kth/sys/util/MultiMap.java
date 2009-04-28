/*
 * Created on 2005-maj-30
 */
package se.kth.sys.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Map of lists with a reasonably sane interface.
 * @param <K> the key type
 * @param <V> the value type
 * @author Rasmus Kaj &lt;kaj@kth.se&gt;
 * @deprecated Use se.kth.sys.util.MultivalueMap instead
 */
public class MultiMap<K, V> {

    private Map<K, List<V>> map;

    /** Create a new (empty) MultiMap. */
    public MultiMap() {
        map = new HashMap<K, List<V>>();
    }

    /** Make this map empty. */
    public void clear() {
        map.clear();
    }

    /**
     * Put a value in the map.  It is possible to put multiple values
     * with the same key in the map.
     * @param key the key to put
     * @param value the value add for key
     */
    public void put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }
        list.add(value);
    }

    /**
     * Remove all values associated with a specific key from the map.
     * @param key the key to clear.
     */
    public void remove(K key) {
        map.remove(key);
    }

    /**
     * Get the set of keys that is in this map.
     * @return the set of keys
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Get the set of values for a key.
     * @param key the key to get values for
     * @return An iterable with the values, or an empty iterable.
     */
    public Iterable<V> get(K key) {
        List<V> value = map.get(key);
        if (value != null) {
            return value;
        } else {
            return new ArrayList<V>();
        }
    }

    /**
     * Get a single Collection containing all the values, regardless of key, in this MultiMap.
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

    /**
     * Check if this MultiMap is empty or contains any values.
     * @return true iff this MultiMap is empty.
     */
    public boolean isEmpty() {
        if (map.isEmpty()) {
            return true;            // nothing
        }
        for (List<V> vlist : map.values()) {
            if (!vlist.isEmpty()) {
                return false;       // there is values
            }
        }
        return true;                // no values for any of the keys
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
     * @param key a key to check for
     * @return true if the key exists in this map
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}

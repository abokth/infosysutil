package se.kth.sys.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.kth.sys.util.TestUtil.assertEqualsSet;
import static java.util.Arrays.asList;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for {@link MultiMap}.
 * @author Rasmus Kaj &lt;kaj@kth.se&gt;
 */
@Deprecated
public class MultiMapTest {

    @Test
    public void testDistinctValues() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();
        map.put(1, "one");
        map.put(2, "two");

        assertFalse(map.isEmpty());
        assertEquals(2, map.size());
        assertEqualsSet("one", map.get(1));
        assertEqualsSet("two", map.get(2));
        assertEqualsSet(asList(1, 2), map.keySet());
        assertEqualsSet(asList("one", "two"), map.values());
    }

    @Test
    public void testNonDistinctValues() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();
        map.put(1, "one");
        map.put(1, "yxi");
        map.put(2, "two");
        map.put(2, "kaxi");

        assertFalse(map.isEmpty());
        assertEquals(4, map.size());
        assertEqualsSet(asList("one", "yxi"), map.get(1));
        assertEqualsSet(asList("two", "kaxi"), map.get(2));
        assertEqualsSet(asList(1, 2), map.keySet());
        assertEqualsSet(asList("one", "two", "yxi", "kaxi"), map.values());
    }

    @Test
    public void testEmpty() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(Collections.<Integer>emptyList(), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }

    @Test
    public void testClear() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();
        map.put(1, "one");
        map.put(1, "yxi");
        map.put(2, "two");
        map.put(2, "kaxi");

        map.clear();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(Collections.<Integer>emptyList(), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }


    @Test
    public void testRemoval() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();
        map.put(1, "one");
        map.remove(1);

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(Collections.<Integer>emptyList(), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }

    @Test
    public void getNonexistant() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();

        assertEqualsSet(Collections.<String>emptyList(), map.get(3));
    }

    @Test
    public void testContainsKey() {
        MultiMap<Integer, String> map = new MultiMap<Integer, String>();
        map.put(1, "one");

        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
    }
}

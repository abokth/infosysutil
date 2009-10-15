package se.kth.sys.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static se.kth.sys.util.TestUtil.assertEqualsSet;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for {@link MultivalueMap}.
 */
public class MultivalueMapTest {

    @Test
    public void testCreate() {
        MultivalueMap<String, String> map = new MultivalueMap<String, String>();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testThings() {
        MultivalueMap<String, String> map = new MultivalueMap<String, String>();
        map.put("nyckel", "gurka");
        map.put("nyckel", "squash");
        map.put("nyckel2", "hej");
        assertEqualsSet(new String[]{"nyckel", "nyckel2"}, map.keySet());
        assertTrue(map.containsKey("nyckel"));
        assertFalse(map.containsKey("jordgubbe"));
        assertTrue(map.containsValue("gurka"));
        assertTrue(map.containsValue("squash"));
        assertTrue(map.containsValue("hej"));
        assertFalse(map.containsValue("jordgubbe"));
        assertEquals(Arrays.asList(new String[]{"gurka", "squash"}), map.get("nyckel"));
        assertEquals(Arrays.asList(new String[]{"gurka", "squash"}), map.asMap().get("nyckel"));
    }

    @Test public void testNullAsKey() {
        MultivalueMap<String, String> map = new MultivalueMap<String, String>();
        map.put("mat", "gurka");
        map.put("mat", "squash");
        map.put(null, "null");
        map.put(null, "nil");
        map.put("form", "klot");
        map.put("form", "kub");

        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue("null"));
        assertEquals(6, map.size());
    }

    @Test
    public void testPutAll() {
        MultivalueMap<String, String> map = new MultivalueMap<String, String>();
        map.put("nyckel", "gurka");
        map.put("nyckel", "squash");
        map.put("nyckel2", "hej");
        map.putAll("nyckel2", Arrays.asList("t1", "t2"));
        map.putAll("nyckel3", Arrays.asList("d1", "d2"));
        assertEqualsSet(new String[]{"nyckel", "nyckel2", "nyckel3"}, map.keySet());
        assertTrue(map.containsKey("nyckel"));
        assertFalse(map.containsKey("jordgubbe"));
        assertTrue(map.containsValue("gurka"));
        assertTrue(map.containsValue("squash"));
        assertTrue(map.containsValue("hej"));
        assertFalse(map.containsValue("jordgubbe"));
        assertEquals(Arrays.asList(new String[]{"gurka", "squash"}), map.get("nyckel"));
        assertEquals(Arrays.asList(new String[]{"hej", "t1", "t2"}), map.get("nyckel2"));
        assertEquals(Arrays.asList(new String[]{"d1", "d2"}), map.get("nyckel3"));
    }

    @Test
    public void testPutAllFromOtherMultivalueMap() {
        MultivalueMap<String, String> map = new MultivalueMap<String, String>();
        map.put("nyckel", "gurka");
        map.put("nyckel", "squash");
        map.put("nyckel2", "hej");
        MultivalueMap<String, String> map2 = new MultivalueMap<String, String>();
        map2.put("nyckel2", "t1");
        map2.put("nyckel3", "d1");
        map2.put("nyckel3", "d2");
        map.putAll(map2);
        assertEqualsSet(new String[]{"nyckel", "nyckel2", "nyckel3"}, map.keySet());
        assertTrue(map.containsKey("nyckel"));
        assertFalse(map.containsKey("jordgubbe"));
        assertTrue(map.containsValue("gurka"));
        assertTrue(map.containsValue("squash"));
        assertTrue(map.containsValue("hej"));
        assertFalse(map.containsValue("jordgubbe"));
        assertEquals(Arrays.asList("gurka", "squash"), map.get("nyckel"));
        assertEquals(Arrays.asList("hej", "t1"), map.get("nyckel2"));
        assertEquals(Arrays.asList("d1", "d2"), map.get("nyckel3"));
    }

    @Test
    public void testDistinctValues() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
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
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
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
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(Collections.<Integer>emptyList(), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }

    @Test
    public void testClear() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
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
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
        map.put(1, "one");
        map.remove(1);

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(Collections.<Integer>emptyList(), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }

    @Test
    public void testRemoveFirst() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
        map.put(1, "one");
        map.put(1, "two");

        assertEquals("one", map.removeFirst(1));

        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEqualsSet(asList("two"), map.get(1));
    }

    @Test
    public void testRemoveFirstAndOnly() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
        map.put(1, "one");

        assertEquals("one", map.removeFirst(1));

        // TODO This leaves the map in a state where it has the key but no values.  Is that really good? /kaj 2009-08-05
        //assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEqualsSet(asList(1), map.keySet());
        assertEqualsSet(Collections.<String>emptyList(), map.values());
    }

    @Test
    public void testRemoveFirstNoneExisting() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
        map.put(1, "one");

        assertNull(map.removeFirst(2));

        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEqualsSet(asList("one"), map.get(1));
    }

    @Test
    public void getNonexistant() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();

        assertEqualsSet(Collections.<String>emptyList(), map.get(3));
    }

    @Test
    public void testContainsKey() {
        MultivalueMap<Integer, String> map = new MultivalueMap<Integer, String>();
        map.put(1, "one");

        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
    }
}

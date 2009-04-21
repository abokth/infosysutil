package se.kth.sys.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.kth.sys.util.TestUtil.assertEqualsSet;

import java.util.Arrays;

public class MultivalueMapTest
{
	public void testCreate()
	{
		MultivalueMap<String, String> map=new MultivalueMap<String, String>();
		assertTrue(map.isEmpty());
	}

	public void testThings()
	{
		MultivalueMap<String, String> map=new MultivalueMap<String, String>();
		map.put("nyckel", "gurka");
		map.put("nyckel", "squash");
		map.put("nyckel2", "hej");
		assertEqualsSet(new String[]{"nyckel","nyckel2"}, map.keySet());
		assertTrue(map.containsKey("nyckel"));
		assertFalse(map.containsKey("jordgubbe"));
		assertTrue(map.containsValue("gurka"));
		assertTrue(map.containsValue("squash"));
		assertTrue(map.containsValue("hej"));
		assertFalse(map.containsValue("jordgubbe"));
		assertEquals(Arrays.asList(new String[]{"gurka","squash"}), map.get("nyckel"));
		assertEquals(Arrays.asList(new String[]{"gurka","squash"}), map.asMap().get("nyckel"));
	}

	public void testPutAll()
	{
		MultivalueMap<String, String> map=new MultivalueMap<String, String>();
		map.put("nyckel", "gurka");
		map.put("nyckel", "squash");
		map.put("nyckel2", "hej");
		map.putAll("nyckel2", Arrays.asList("t1", "t2"));
		map.putAll("nyckel3", Arrays.asList("d1", "d2"));
		assertEqualsSet(new String[]{"nyckel","nyckel2","nyckel3"}, map.keySet());
		assertTrue(map.containsKey("nyckel"));
		assertFalse(map.containsKey("jordgubbe"));
		assertTrue(map.containsValue("gurka"));
		assertTrue(map.containsValue("squash"));
		assertTrue(map.containsValue("hej"));
		assertFalse(map.containsValue("jordgubbe"));
		assertEquals(Arrays.asList(new String[]{"gurka","squash"}), map.get("nyckel"));
		assertEquals(Arrays.asList(new String[]{"hej","t1","t2"}), map.get("nyckel2"));
		assertEquals(Arrays.asList(new String[]{"d1","d2"}), map.get("nyckel3"));
	}

}

package se.kth.infosys.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Test {@link StringUtil}.
 *
 * @author Rasmus Kaj &lt;kaj@kth.se&gt;
 */
public class StringUtilTest {

    /** Test for {@link StringUtil.join}. */
    @Test
    public void testJoin0() {
        assertEquals("", StringUtil.join(", ", Arrays.asList()));
    }

    /** Test for {@link StringUtil.join}. */
    @Test
    public void testJoin1() {
        assertEquals("foo", StringUtil.join(", ", Arrays.asList("foo")));
    }

    /** Test for {@link StringUtil.join}. */
    @Test
    public void testJoin2() {
        assertEquals("foo, bar", StringUtil.join(", ", Arrays.asList("foo", "bar")));
    }

    /**
     * Test for {@link StringUtil.join}.
     * It should be possible to join anything with a toString(), int should propagate to Integer.
     */
    @Test
    public void testJoinNum() {
        assertEquals("1, 2, 3", StringUtil.join(", ", Arrays.asList(1, 2, 3)));
    }

    /**
     * Test for {@link StringUtil.euqals}.
     */
    @Test
    public void testEquals() {
        assertTrue(StringUtil.equals("foo", "foo"));
        assertTrue(StringUtil.equals("foo", new String("foo")));
        assertFalse(StringUtil.equals("foo", "bar"));
        assertTrue(StringUtil.equals(null, null));
        assertFalse(StringUtil.equals(null, ""));
        assertFalse(StringUtil.equals("", null));
        assertTrue(StringUtil.equals("", ""));
    }
}

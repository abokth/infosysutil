package se.kth.sys.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * JUnit tests for {@TestUtil}.
 *
 * @author Rasmus Kaj &lt;kaj@kth.se&gt;
 */
public class TestUtilTest {

    @Test
    public void testContains() {
        TestUtil.assertContains(1, asList(1, 2, 3));
    }

    @Test
    public void testNotContains() {
        try {
            TestUtil.assertContains(4, asList(1, 2, 3));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("The item <4> is missing from collection <1, 2, 3>", err.getMessage());
        }
    }

    @Test
    public void testContainsNot() {
        TestUtil.assertContainsNot(4, asList(1, 2, 3));
    }

    @Test
    public void testNotContainsNot() {
        try {
            TestUtil.assertContainsNot(1, asList(1, 2, 3));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("Unexpected item <1> present in collection <1, 2, 3>", err.getMessage());
        }
    }
}
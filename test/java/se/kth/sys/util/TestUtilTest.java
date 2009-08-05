package se.kth.sys.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;

/**
 * JUnit tests for {@TestUtil}.
 *
 * @author Rasmus Kaj &lt;kaj@kth.se&gt;
 */
public class TestUtilTest {

    @Test
    public void testEqualsSet() {
        TestUtil.assertEqualsSet(asList(1, 2, 3, 5), asList(1, 2, 3, 5));
    }

    @Test
    public void testEqualsSetFailure() {
        try {
            TestUtil.assertEqualsSet(asList(1, 2, 3, 5), asList(1, 2, 3, 6));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("Expected [1, 2, 3, 5] but had extra elements [6] and missed elements [5]", err.getMessage());
        }
    }

    @Test
    public void testEqualsSetFailureExtraOnly() {
        try {
            TestUtil.assertEqualsSet(asList(1, 2, 3), asList(1, 2, 3, 6));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("Expected [1, 2, 3] but had extra elements [6]", err.getMessage());
        }
    }

    @Test
    public void testEqualsSetFailureMissingOnly() {
        try {
            TestUtil.assertEqualsSet(asList(1, 2, 3, 5), asList(1, 2, 3));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("Expected [1, 2, 3, 5] but missed elements [5]", err.getMessage());
        }
    }

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

    @Test
    public void testEmpty() {
        TestUtil.assertEmpty(Collections.emptyList());
    }

    @Test
    public void testEmptyNot() {
        try {
            TestUtil.assertEmpty(asList(1, 2, 3));
            fail("The above should throw an execption");
        } catch (AssertionError err) {
            assertEquals("Collection should be empty but contained <1, 2, 3>", err.getMessage());
        }
    }
}

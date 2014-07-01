package se.kth.sys.util;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods for tests.
 */
public final class TestUtil {

    /**
     * Hide default constructor.
     */
    private TestUtil() { }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T[] expected, T[] actual) {
        assertEqualsSet(Arrays.asList(expected), Arrays.asList(actual));
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(Collection<T> expected, Collection<T> actual) {
        if (!new HashSet<T>(expected).equals(new HashSet<T>(actual))) {
            Set<T> extra = new HashSet<T>();
            Set<T> missing = new HashSet<T>(expected);
            for (T item : actual) {
                if (!expected.contains(item)) {
                    extra.add(item);
                } else {
                    missing.remove(item);
                }
            }
            fail("Expected " + expected.toString() + " but "
                 + (extra.isEmpty() ? "" : "had extra elements " + extra.toString())
                 + (!extra.isEmpty() && !missing.isEmpty() ? " and " : "")
                 + (missing.isEmpty() ? "" : "missed elements " + missing.toString()));
        }
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T[] expected, Collection<T> actual) {
        assertEqualsSet(Arrays.asList(expected), actual);
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(Collection<T> expected, Iterable<T> actual) {
        HashSet<T> actualSet = new HashSet<T>();
        for (T t : actual) {
            actualSet.add(t);
        }
        assertEqualsSet(expected, actualSet);
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T expected, T[] actual) {
        assertEqualsSet(expected, Arrays.asList(actual));
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T expected, Collection<T> actual) {
        assertEqualsSet(Arrays.asList(expected), actual);
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T expected, Iterable<T> actual) {
        assertEqualsSet(Arrays.asList(expected), actual);
    }

    /**
     * Test that a given collection contains a specific item.
     * @param <T> type of items
     * @param item the expected item
     * @param actual the actual collection
     */
    public static <T> void assertContains(T item, Collection<T> actual) {
        if (!actual.contains(item)) {
            fail("The item <" + item + "> is missing from collection <" + StringUtil.join(", ", actual) + ">");
        }
    }

    /**
     * Test that a given collection does not contain a specific item.
     * @param <T> type of items
     * @param item the unexpected item
     * @param actual the actual collection
     */
    public static <T> void assertContainsNot(T item, Collection<T> actual) {
        if (actual.contains(item)) {
            fail("Unexpected item <" + item + "> present in collection <" + StringUtil.join(", ", actual) + ">");
        }
    }

    public static <T> void assertEmpty(Collection<T> actual) {
        if (!actual.isEmpty()) {
            fail("Collection should be empty but contained <" + StringUtil.join(", ", actual) + ">");
        }
    }
}

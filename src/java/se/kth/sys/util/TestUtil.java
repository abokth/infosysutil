package se.kth.sys.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
        assertEquals(new HashSet<T>(expected), new HashSet<T>(actual));
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    public static <T> void assertEqualsSet(T[] expected, Collection<T> actual) {
        assertEquals(new HashSet<T>(Arrays.asList(expected)),
                new HashSet<T>(actual));
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
        assertEquals(new HashSet<T>(expected), actualSet);
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
    @SuppressWarnings("unchecked")
    public static <T> void assertEqualsSet(T expected, Collection<T> actual) {
        assertEqualsSet(Arrays.asList(expected), actual);
    }

    /**
     * Tests that a set of data has the expected members.  The assert will fail if the members differ.
     * @param <T> type of member data
     * @param expected the expected elements
     * @param actual the actual elements
     */
    @SuppressWarnings("unchecked")
    public static <T> void assertEqualsSet(T expected, Iterable<T> actual) {
        assertEquals(Arrays.asList(expected), actual);
    }

}

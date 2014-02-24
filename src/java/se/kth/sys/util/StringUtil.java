package se.kth.sys.util;

import static java.util.Arrays.asList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Some utility classes for handling strings.
 */
public final class StringUtil {

    private StringUtil() { }

    /**
     * Join the string representations of a set of objects, separated by any separator.
     * @param separator the separator to use
     * @param objects the objects to join.
     * @return the joined string.
     * @param <T> type of objects to join.
     */
    public static String join(String separator, Iterable<?> objects) {
        StringBuilder retval = new StringBuilder();

        Iterator<?> i = objects.iterator();
        if (i.hasNext()) {
            retval.append(i.next());
            while (i.hasNext()) {
                retval.append(separator);
                retval.append(i.next());
            }
        }

        return retval.toString();
    }

    /**
     * Join the string representations of a set of objects, separated by any separator.
     * @param separator the separator to use
     * @param array the objects to join.
     * @return the joined string.
     * @param <T> type of objects to join.
     */
    public static <T> String join(String separator, T[] array) {
        return join(separator, asList(array));
    }

    /**
     * Get n copies of a string, to iterate over.
     * @param s the original value.
     * @param n the number of copies.
     * @return an iterable of n times s.
     * @param <T> the iterable will be of the same type as the original
     */
    public static <T> Iterable<T> fill(T s, int n) {
        List<T> result = new LinkedList<T>();
        for (int i = 0; i < n; i++) {
            result.add(s);
        }
        return result;
    }

    /**
     * Check if two strings are equal.  If both strings are null, they are considered equal.
     * If one of the strings are null, they are not equal.  Otherwise {@link String#equals(Object)} decides.
     * @param a one string.
     * @param b another string.
     * @return true if the strings are equal.
     */
    public static boolean equals(String a, String b) {
        if ((a == null) && (b == null)) {
            return true;
        }

        if ((a == null) || (b == null)) {
            return false;
        }

        return (a.equals(b));
    }

    public static boolean isValue(String object) {
        return (object != null) && (!"".equals(object));
    }
}

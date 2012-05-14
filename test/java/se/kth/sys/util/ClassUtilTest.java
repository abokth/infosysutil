package se.kth.sys.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassUtilTest {

    private static String nameGotStatically = ClassUtil.getClassName();

    @Test
    public void testGetClassNameInMethod() {
        assertEquals("se.kth.sys.util.ClassUtilTest", ClassUtil.getClassName());
    }

    @Test
    public void testGetClassNameStatically() {
        assertEquals("se.kth.sys.util.ClassUtilTest", nameGotStatically);
    }
}

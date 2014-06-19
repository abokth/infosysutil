package se.kth.sys.util;

public final class ClassUtil {

    private ClassUtil() { }

    public static String getClassName() {
        try {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            return stack[1].getClassName();
        } catch (Throwable t) {
            return "classnamenotfound";
        }
    }
}

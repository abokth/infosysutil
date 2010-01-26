package se.kth.sys.util;

public class ClassUtil {
	
	static public String getClassName () {
		try {
			StackTraceElement stack[] = new Throwable().getStackTrace();
			return stack[1].getClassName();
		} catch (Throwable t) {
			return "classnamenotfound";
		}
	}
}

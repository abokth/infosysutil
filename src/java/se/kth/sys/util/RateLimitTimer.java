package se.kth.sys.util;

/**
 * A timer for limiting the frequency of an operation.
 * 
 * register() and waitFor() can be called separately if other work is done in between.
 * 
 * delay() simply sleeps the required amount of time.
 * 
 * @author abo
 *
 */
public class RateLimitTimer {
	private SimpleTimer lastRegister = null;
	private long limit;

	public RateLimitTimer(long millis) {
		limit = millis;
	}

	public long getLimit() {
		return limit;
	}

	/**
	 * Sets a timestamp used by waitFor().
	 */
	public void register() {
		lastRegister = new SimpleTimer();
	}

	/**
	 * Sleep until at least one second has passed since register() was called.
	 */
	public void waitFor() {
		if (lastRegister != null) {
			do {
				long sleeptime = limit - lastRegister.elapsed();
				if (sleeptime > 0)
					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {}
				else
					break;
			} while (true);
		}
	}

	/**
	 * Sleep for limit() amount of time.
	 * 
	 * A subsequent call to waitFor() will return immediately.
	 */
	public void delay() {
		// Register that we're sending a notification.
		register();
	
		// Ensure that at least one second has passed since the last notification.
		waitFor();
	}

}

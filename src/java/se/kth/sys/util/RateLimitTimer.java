package se.kth.sys.util;

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

}

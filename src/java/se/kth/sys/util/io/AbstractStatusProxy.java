package se.kth.sys.util.io;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Possibly generic logic for consolidating status updates in a StatusProxy implementation.
 * 
 * @author abo
 *
 */
public abstract class AbstractStatusProxy extends StatusProxy {

	protected abstract void notifyStatus(String string);
	protected abstract void notifyReady(String string);
	protected abstract void notifyReady();
	protected abstract void notifyStopping(String string);
	protected abstract void notifyWatchdog();

	private boolean ready = false;
	private boolean stopping = false;
	private String previousStatus = null;

	private boolean need_watchdog_update = false;
	private Timer watchdogExpirerTimer = null;

	/* Calls notifyStatus(string) if state has changed or if a watchdog update is needed.
	 * 
	 * (non-Javadoc)
	 * @see se.kth.sys.util.io.StatusProxy#setStarting(java.lang.String)
	 */
	@Override
	public void setStarting(String string) {
		if (need_watchdog_update || previousStatus == null || previousStatus.equals(string)) {
			notifyStatus(string);
			previousStatus = string;
			need_watchdog_update = false;
		}
	}

	/* Calls notifyReady() if this hasn't already been done, else updates the watchdog if needed.
	 * 
	 * (non-Javadoc)
	 * @see se.kth.sys.util.io.StatusProxy#setRunning()
	 */
	@Override
	public void setRunning() {
		if (ready) {
			if (need_watchdog_update) {
				notifyWatchdog();
				need_watchdog_update = false;
			}
		} else {
			ready = true;
			notifyReady();
			need_watchdog_update = false;
		}
	}

	/* Calls notifyReady() if this hasn't already been done, else calls
	 * notifyStatus(string) if state has changed or if a watchdog update is needed.
	 * 
	 * (non-Javadoc)
	 * @see se.kth.sys.util.io.StatusProxy#setRunning(java.lang.String)
	 */
	@Override
	public void setRunning(String string) {
		if (ready) {
			if (need_watchdog_update || previousStatus == null || !previousStatus.equals(string)) {
				notifyStatus(string);
				need_watchdog_update = false;
			}
		} else {
			ready = true;
			notifyReady(string);
			need_watchdog_update = false;
		}
		if (previousStatus != string)
			previousStatus = string;
	}

	/* Calls notifyStopping() if this hasn't already been done, else updates the status as required.
	 * 
	 * (non-Javadoc)
	 * @see se.kth.sys.util.io.StatusProxy#setStopping(java.lang.String)
	 */
	@Override
	public void setStopping(String string) {
		if (stopping) {
			if (need_watchdog_update || previousStatus == null || !previousStatus.equals(string)) {
				notifyStatus(string);
				need_watchdog_update = false;
			}
		} else {
			stopping = true;
			notifyStopping(string);
			need_watchdog_update = false;
		}
		if (previousStatus != string)
			previousStatus = string;
	}

	/* Calls notifyStopped() and disables the watchdog.
	 * 
	 * (non-Javadoc)
	 * @see se.kth.sys.util.io.StatusProxy#setStopped(java.lang.String)
	 */
	@Override
	public void setStopped(String string) {
		notifyStopping(string);
		need_watchdog_update = false;
		previousStatus = string;
		watchdogExpirerTimer.cancel();
	}

	/**
	 * Enable watchdog updates on status updates.
	 * 
	 * The service must provide status updates at least every watchdog_msec milliseconds
	 * by calling any of the setStarting(), setRunning() or setStopping() methods every so often.
	 * 
	 * Not every status update will result in a watchdog update.
	 * Only when state, status text or a third of the time specified by
	 * the parameter has passed will a message be sent to the watchdog.
	 * 
	 * @param watchdog_msec Maximum amount of time between each watchdog update.
	 */
	protected void startWatchdogUpdateTimer(final long watchdog_msec) {
		need_watchdog_update = true;
		watchdogExpirerTimer = new Timer();
		watchdogExpirerTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				need_watchdog_update = true;
			}
		}, 0, watchdog_msec / 3);
	}
}

package se.kth.sys.util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import se.kth.sys.util.lang.SystemCommandHandler;

/**
 * The sd_notify compatible implementation of StatusProxy.
 * 
 * @author abo
 *
 */
public class SystemdNotify extends StatusProxy {
	private SystemCommandHandler socketCommand = null;
	String watchdogargs = null;
	private OutputStreamWriter socketWriter = null;
	boolean initialized = false;

	boolean sendReady = false, sentReady = false;
	boolean sendStopping = false, sentStopping = false;
	String statusText = null, sentStatusText = null;

	private boolean need_watchdog_update = false;
	private Timer watchdogExpirerTimer = null;

	/**
	 * Initializes and returns an instance of SystemdNotify if the matching
	 * environment variables are found, else returns null.
	 * 
	 * @return an instance of SystemdNotify or null
	 */
	protected static SystemdNotify createInstance() {
		if (System.getenv().containsKey("NOTIFY_SOCKET")) {
			String socket = System.getenv("NOTIFY_SOCKET");
			final SystemdNotify systemdNotify = new SystemdNotify();

			if (System.getenv().containsKey("WATCHDOG_USEC")) {
				String usec = System.getenv("WATCHDOG_USEC");
				systemdNotify.watchdogargs = "WATCHDOG=1\n";
				if (System.getenv().containsKey("WATCHDOG_PID")) {
					String pid = System.getenv("WATCHDOG_PID");
					String mypid = System.getProperty("watchdog.pid");
					if (mypid != null && mypid.equals(pid))
						systemdNotify.watchdogargs = "MAINPID=" + mypid + "\n" + systemdNotify.watchdogargs;
				}
				systemdNotify.startWatchdogUpdateTimer(new Long(usec) / 1000);
			}

			try {
				systemdNotify.startNotify(socket);
			} catch (IOException e) {
				// TODO Automatically generated catch block
				e.printStackTrace();
			}
			return systemdNotify;
		}
		return null;
	}

	/**
	 * @return true if there are updates needed to be sent, otherwise false
	 */
	private boolean needNewNotification() {
		return sentReady != sendReady || sentStopping != sendStopping || sentStatusText == null || !sentStatusText.equals(statusText);
	}

	/**
	 * Generates a notification string if the state (ready or stopping) has changed.
	 * 
	 * @return a String in sd_notify format
	 */
	private String getChangedState() {
		if (sendStopping != sentStopping) {
			sendReady = true; sentReady = true; // or it's too late anyway
			sentStopping = true;
			return "STOPPING=1\n";
		}
		if (sendReady != sentReady) {
			sentReady = true;
			return "READY=1\n";
		}
		return null;
	}

	/**
	 * Generates a notification string if the status text has changed.
	 * 
	 * @return a String in sd_notify format
	 */
	private String getChangedStatusText() {
		if (statusText != null && (sentStatusText == null || !statusText.equals(sentStatusText))) {
			sentStatusText = statusText;
			return "STATUS=" + sentStatusText + "\n";
		}
		return null;
	}

	/**
	 * Open socket and initialize variables.
	 * 
	 * @param socket a path to a named socket
	 * @throws IOException
	 */
	private void startNotify(String socket) throws IOException {
		// How to talk to a unix socket in Java.
		socketCommand = new SystemCommandHandler(new String[] { "socat", "-u", "-", "GOPEN:" + socket });
		socketCommand.execute();
		socketWriter = new OutputStreamWriter(socketCommand.getOutputStream());
		initialized = true;
	}

	/**
	 * Drain queues and disconnect from the socket.
	 */
	@SuppressWarnings("unused")
	private void stopNotify() {
		notifyAndFlush();
		initialized = false;
		try {
			if (socketWriter != null)
				socketWriter.close();
			if (socketCommand != null) {
				socketCommand.getOutputStream().close();
				socketCommand.joinProcess();
			}
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
		socketWriter = null;
		socketCommand = null;
	}

	/**
	 * Write the latest queued state and status to the socket, then inhibit
	 * sending for one second. 
	 * 
	 * When this returns, sendReady == sentReady and sendStopping == sentStopping
	 * and statusText equals sentStatusText.
	 */
	private void notifyAndFlush() {
		synchronized (socketWriter) {
			String newStatus = getChangedStatusText();
			String newState = getChangedState();

			// Send a single message with the given information and a watchdog notification (if enabled).
			if (newStatus != null || newState != null || watchdogargs != null) {
				String string = (newStatus != null ? newStatus : "") + (newState != null ? newState : "") + (watchdogargs != null ? watchdogargs : "");
				try {
					socketWriter.write(string);
					socketWriter.flush();
					socketCommand.getOutputStream().flush();
				} catch (IOException e) {}

				// Stay inside this (synchronized) block for one second, if we sent something.
				// (There's rate limiting at the receiving end of these messages.)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
	}

	/**
	 * Generate a notification if there are updates needed to be sent.
	 * 
	 * Due to rate limiting writing to the socket may take a while.
	 * This runs it in a separate thread to enable notify*() to return quickly.
	 */
	private void startQueueFlushIfRequired() {
		if (!initialized)
			return;
		if (need_watchdog_update || needNewNotification()) {
			need_watchdog_update = false;
			new Thread() {
				public void run() {
					notifyAndFlush();
				}
			}.start();
		}
	}

	@Override
	public void setStarting(String string) {
		statusText = string;
		startQueueFlushIfRequired();
	}

	@Override
	public void setRunning() {
		sendReady = true;
		startQueueFlushIfRequired();
	}

	@Override
	public void setRunning(String string) {
		sendReady = true;
		statusText = string;
		startQueueFlushIfRequired();
	}

	@Override
	public void setStopping(String string) {
		sendStopping = true;
		statusText = string;
		startQueueFlushIfRequired();
	}

	@Override
	public void setStopped(String string) {
		setStopping(string);
		watchdogExpirerTimer.cancel();
		stopNotify();
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

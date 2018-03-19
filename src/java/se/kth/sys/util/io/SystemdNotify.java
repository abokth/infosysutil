package se.kth.sys.util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;

import se.kth.sys.util.lang.SystemCommandHandler;

/**
 * The sd_notify compatible implementation of StatusProxy.
 * 
 * @author abo
 *
 */
public class SystemdNotify extends WatchdogStatusProxy {
	private SystemCommandHandler socketCommand = null;
	String watchdogargs = null;
	private OutputStreamWriter socketWriter = null;

	boolean sendReady = false, sentReady = false;
	boolean sendStopping = false, sentStopping = false;
	String statusText = null, sentStatusText = null;

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
				return null;
			}
			return systemdNotify;
		}
		return null;
	}

	/**
	 * @return true if there are updates needed to be sent, otherwise false
	 */
	private boolean hasQueuedNotifications() {
		return sentReady != sendReady || sentStopping != sendStopping || (statusText != null && (sentStatusText == null || !statusText.equals(sentStatusText)));
	}

	/**
	 * @return
	 */
	private boolean dequeueRunningStateNotification() {
		if (sentReady != sendReady) {
			sentReady = true;
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	private boolean dequeueStoppingStateNotification() {
		if (sentStopping != sendStopping) {
			sendReady = true; sentReady = true; // or it's too late anyway
			sentStopping = true;
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	private boolean dequeueStatusNotification() {
		if (statusText != null && (sentStatusText == null || !statusText.equals(sentStatusText))) {
			sentStatusText = statusText;
			return true;
		}
		return false;
	}

	/**
	 * Generates a notification string if the state (ready or stopping) has changed.
	 * 
	 * @return a String in sd_notify format or null
	 */
	private String dequeueState() {
		if (dequeueStoppingStateNotification())
			return "STOPPING=1\n";
		if (dequeueRunningStateNotification())
			return "READY=1\n";
		return null;
	}

	/**
	 * Generates a notification string if the status text has changed.
	 * 
	 * @return a String in sd_notify format or null
	 */
	private String dequeueStatus() {
		if (dequeueStatusNotification())
			return "STATUS=" + sentStatusText + "\n";
		return null;
	}

	/**
	 * Generates a notification string if any state has changed or if watchdogargs is set.
	 * 
	 * @return a String in sd_notify format or null
	 */
	private String dequeueNotification() {
		String newStatus = dequeueStatus();
		String newState = dequeueState();

		// Send a single message with the given information and a watchdog notification (if enabled).
		if (newStatus != null || newState != null || watchdogargs != null)
			return (newStatus != null ? newStatus : "") + (newState != null ? newState : "") + (watchdogargs != null ? watchdogargs : "");

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
	}

	/**
	 * Drain queues and disconnect from the socket.
	 */
	private void stopNotify() {
		dequeueAndSendNotification();
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
	private void dequeueAndSendNotification() {
		synchronized (socketWriter) {
			String toSend = dequeueNotification();

			if (toSend != null) {
				try {
					socketWriter.write(toSend);
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
	 * This method must return quickly.
	 */
	private void queueNotification() {
		if (dequeueWatchdogUpdate() || hasQueuedNotifications()) {
			new Thread() {
				public void run() {
					dequeueAndSendNotification();
				}
			}.start();
		}
	}

	@Override
	public void setStarting(String string) {
		statusText = string;
		queueNotification();
	}

	@Override
	public void setRunning() {
		sendReady = true;
		queueNotification();
	}

	@Override
	public void setRunning(String string) {
		sendReady = true;
		statusText = string;
		queueNotification();
	}

	@Override
	public void setStopping(String string) {
		sendStopping = true;
		statusText = string;
		queueNotification();
	}

	@Override
	public void setStopped(String string) {
		setStopping(string);
		stopWatchdogUpdateTimer();
		stopNotify();
	}

}

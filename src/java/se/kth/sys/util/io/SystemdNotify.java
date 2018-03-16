package se.kth.sys.util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.kth.sys.util.lang.SystemCommandHandler;

public class SystemdNotify extends AbstractStatusProxy {
	private SystemCommandHandler socketCommand = null;
	String watchdogargs = null;
	private OutputStreamWriter socketWriter = null;
	private ConcurrentLinkedQueue<String> statusQueue = null, stateQueue = null;
	boolean started = false;

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

	private void startNotify(String socket) throws IOException {
		// How to talk to a unix socket in Java.
		socketCommand = new SystemCommandHandler(new String[] { "socat", "-u", "-", "GOPEN:" + socket });
		socketCommand.execute();
		socketWriter = new OutputStreamWriter(socketCommand.getOutputStream());
		statusQueue = new ConcurrentLinkedQueue<String>();
		stateQueue = new ConcurrentLinkedQueue<String>();
		started = true;
	}

	@SuppressWarnings("unused")
	private void stopNotify() {
		writeAndFlush();
		started = false;
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
		stateQueue = null;
		statusQueue = null;
		socketWriter = null;
		socketCommand = null;
	}

	@Override
	protected void notifyStatus(String string) {
		if (started) {
			statusQueue.add("STATUS=" + string + "\n");
			startQueueFlush();
		}
	}

	@Override
	protected void notifyReady(String string) {
		if (started) {
			statusQueue.add("STATUS=" + string + "\n");
			stateQueue.add("READY=1\n");
			startQueueFlush();
		}
	}

	@Override
	protected void notifyReady() {
		if (started) {
			stateQueue.add("READY=1\n");
			startQueueFlush();
		}
	}

	@Override
	protected void notifyStopping(String string) {
		if (started) {
			statusQueue.add("STATUS=" + string + "\n");
			stateQueue.add("STOPPING=1\n");
			startQueueFlush();
		}
	}

	@Override
	protected void notifyWatchdog() {
		if (started && watchdogargs != null)
			startQueueFlush();
	}

	private void startQueueFlush() {
		// Run in a separate thread since it will block for at least a second.
		new Thread() {
			public void run() {
				writeAndFlush();
			}
		}.start();
	}

	synchronized private void writeAndFlush() {
		// Find the last status text and the last state code given.
		String lastStatus = null, lastState = null;
		while (!statusQueue.isEmpty()) {
			lastStatus = statusQueue.remove();
		}
		while (!stateQueue.isEmpty()) {
			lastState = stateQueue.remove();
		}

		// Send a single message with the given information and a watchdog notification (if enabled).
		if (lastStatus != null || lastState != null || watchdogargs != null) {
			String string = (lastStatus != null ? lastStatus : "") + (lastState != null ? lastState : "") + (watchdogargs != null ? watchdogargs : "");
			try {
				socketWriter.write(string);
				socketWriter.flush();
				socketCommand.getOutputStream().flush();
			} catch (IOException e) {}

			// Stay inside this (synchronized) method for one second, if we sent something.
			// (There's rate limiting at the receiving end of these messages.)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}

}

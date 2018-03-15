package se.kth.sys.util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.kth.sys.util.lang.SystemCommandHandler;

public class SystemdNotify extends AbstractStatusProxy {
	private SystemCommandHandler socketCommand = null;
	String watchdogargs = null;
	private OutputStreamWriter socketWriter = null;
	private ConcurrentLinkedQueue<String> queue = null;

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
				// TODO Automatiskt genererat catch-block
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
		queue = new ConcurrentLinkedQueue<String>();
	}

	@SuppressWarnings("unused")
	private void stopNotify() {
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
	}

	@Override
	protected void notifyStatus(String string) {
		sendNotify("STATUS=" + string + "\n");
	}

	@Override
	protected void notifyReady(String string) {
		sendNotify("STATUS=" + string + "\nREADY=1\n");
	}

	@Override
	protected void notifyReady() {
		sendNotify("READY=1\n");
	}

	@Override
	protected void notifyStopping(String string) {
		sendNotify("STATUS=" + string + "\nSTOPPING=1\n");
	}

	@Override
	protected void notifyWatchdog() {
		if (watchdogargs != null)
			writeIfStarted(watchdogargs);
	}

	private void sendNotify(String notification) {
		if (watchdogargs != null)
			writeIfStarted(notification + watchdogargs);
		else
			writeIfStarted(notification);
	}

	private void writeIfStarted(String string) {
		if (socketCommand == null || socketWriter == null)
			return;
		queue.add(string);
		
		// Run in a separate thread since it will block for at least a second.
		new Thread() {
			public void run() {
				writeAndFlush();
			}
		}.start();
	}

	synchronized private void writeAndFlush() {
		while (!queue.isEmpty()) {
			String string = queue.remove();
			try {
				socketWriter.write(string);
				socketWriter.flush();
				socketCommand.getOutputStream().flush();
			} catch (IOException e) {}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

}

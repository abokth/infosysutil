package se.kth.sys.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple implementation of StatusProxy which writes the current status to
 * the file provided by the STATUS_FILE environment variable, in the form
 * 
 * (STARTING|READY|STOPPING) <counter> <status text>
 * 
 * The reader of the file should consider a change in the content of the file as
 * an update of the watchdog timestamp.
 * 
 * @author abo
 *
 */
public class StatusFile extends StatusProxy {
	private static final String STARTING = "STARTING", READY = "READY", STOPPING = "STOPPING";
	private String serviceState = null, statusText = null;
	private int counter = 0;

	private File statusFile = null;

	private boolean need_watchdog_update = false;
	private Timer watchdogExpirerTimer = null;

	@Override
	public void setStarting(String text) {
		serviceState = STARTING;
		writeOldStatus(text);
	}

	@Override
	public void setRunning() {
		if (serviceState == READY) {
			writeOldStatus();
		} else {
			serviceState = READY;
			writeStatus();
		}
	}

	@Override
	public void setRunning(String text) {
		if (serviceState == READY) {
			writeOldStatus(text);
		} else {
			serviceState = READY;
			writeStatus(text);
		}
	}

	@Override
	public void setStopping(String text) {
		if (serviceState == STOPPING) {
			writeOldStatus(text);
		} else {
			serviceState = STOPPING;
			writeStatus(text);
		}
	}

	@Override
	public void setStopped(String text) {
		serviceState = STOPPING;
		writeStatus(text);
		watchdogExpirerTimer.cancel();
	}

	/**
	 * Enable watchdog updates on status updates.
	 * 
	 * The service must provide status updates at least every watchdog_msec milliseconds
	 * by calling any of the setStarting(), setRunning() or setStopping() methods every so often.
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

	/**
	 * Initializes and returns an instance of StatusFile if the matching
	 * environment variables are found, else returns null.
	 * 
	 * @return an instance of StatusFile or null
	 */
	protected static StatusFile createInstance() {
		if (System.getenv().containsKey("STATUS_FILE")) {
			String statusfilename = System.getenv("STATUS_FILE");
			final StatusFile instance = new StatusFile();

			if (System.getenv().containsKey("WATCHDOG_SEC")) {
				String sec = System.getenv("WATCHDOG_SEC");
				instance.startWatchdogUpdateTimer(new Long(sec) * 1000);
			}

			instance.statusFile = new File(statusfilename);
			try {
				instance.writeStatusString("");
			} catch (IOException e) {
				// TODO Automatiskt genererat catch-block
				e.printStackTrace();
			}
			return instance;
		}
		return null;
	}

	/**
	 * Write the given string to the status file.
	 * 
	 * @param string
	 * @throws IOException
	 */
	private void writeStatusString(String string) throws IOException {
		FileWriter writer = new FileWriter(statusFile, false);
		try {
			writer.write(string);
		} finally {
			writer.close();
		}
	}

	/**
	 * Generate the content of the status file and write it.
	 * Accordingly resets the watchdog update flag.
	 */
	private void writeStatus() {
		String statusstring = serviceState + " " + Integer.toString(counter++) + " " + statusText;
		if (counter >= 1048576)
			counter = 0;
		try {
			writeStatusString(statusstring);
		} catch (IOException e) {}
		need_watchdog_update = false;
	}

	private void writeStatus(String s) {
		statusText = s;
		writeStatus();
	}

	/**
	 * Call writeStatus() if a watchdog update is needed.
	 */
	private void writeOldStatus() {
		if (need_watchdog_update)
			writeStatus();
	}

	/**
	 * Call writeStatus() if a watchdog update is needed or the status
	 * text has changed.
	 * 
	 * @param s Describes the current status
	 */
	private void writeOldStatus(String s) {
		if (need_watchdog_update || statusText == null || !statusText.equals(s))
			writeStatus(s);
	}

}

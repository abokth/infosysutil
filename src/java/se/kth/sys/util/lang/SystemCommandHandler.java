package se.kth.sys.util.lang;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.kth.sys.util.io.LineReceiver;
import se.kth.sys.util.io.ReadLineThread;

public class SystemCommandHandler implements LineReceiver {

	protected LinkedList<String> commandline;
	private int passwordindex = 0;
	private String passwordval;
	private List<String> stdOutStore = null;
	private List<String> stdErrStore = null;
	protected static Object STDOUT = new Object();
	protected static Object STDERR = new Object();
	private int exitCode = -1;
	protected ReadLineThread outlog;
	protected ReadLineThread errlog;

	public SystemCommandHandler(LinkedList<String> commandline) {
		this.commandline = commandline;
	}

	public SystemCommandHandler(String[] commandline) {
		this(new LinkedList<String>(Arrays.asList(commandline)));
	}

	public void prepend(String string) {
		passwordindex++;
		commandline.addFirst(string);
	}

	public void prepend(String[] strings) {
		for (int i=strings.length-1; i>=0; i--)
			prepend(strings[i]);
	}

	public void append(String string) {
		commandline.add(string);
	}

	public void setPassword(int index, String value) {
		passwordindex = index;
		passwordval = value;
	}

	/**
	 * Executes the command and waits for it to complete.
	 * Afterwards you need to call getExitCode(), getStdOutIOException() and getStdErrIOException().
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeAndWait() throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
	
		String[] argv = commandline.toArray(new String[] {});
		if (passwordval != null) {
			argv[passwordindex] = passwordval;
		}
		Process process = runtime.exec(argv);
	
		process.getOutputStream().close();

		outlog = new ReadLineThread(process.getInputStream(), STDOUT, this);
		errlog = new ReadLineThread(process.getErrorStream(), STDERR, this);
	
		outlog.start();
		errlog.start();
	
		exitCode = process.waitFor();
	
		// Wait for all output to be logged.
		outlog.join();
		errlog.join();
	
		process.getInputStream().close();
		process.getErrorStream().close();
	}

	public int getExitCode() {
		return exitCode;
	}

	/**
	 * @return
	 */
	public IOException getStdOutIOException() {
		return outlog.getIOException();
	}

	/**
	 * @return
	 */
	protected IOException getStdErrIOException() {
		return errlog.getIOException();
	}

	public void receiveLine(Object bufferid, String line) {
		storeStdioLine(bufferid, line);
	}

	public void enableStdOutStore() {
		if (stdOutStore == null) {
			stdOutStore = new LinkedList<String>();
		}
	}

	public void enableStdErrStore() {
		if (stdErrStore == null) {
			stdErrStore = new LinkedList<String>();
		}
	}

	public List<String> getStdOutStore() {
		return stdOutStore;
	}

	public List<String> getStdErrStore() {
		return stdErrStore;
	}

	/**
	 * @param streamid
	 * @param s
	 */
	protected void storeStdioLine(Object streamid, String s) {
		if (STDOUT == streamid) {
			if (stdOutStore != null) {
				stdOutStore.add(s);
			}
		}
		if (STDERR == streamid) {
			if (stdErrStore != null) {
				stdErrStore.add(s);
			}
		}
	}

	public List<String> testingGetCmdLineStringList() {
		return commandline;
	}

	@Override
	public String toString() {
		return "Command line: " + asEscapedString();
	}

	public String asEscapedString() {
		return arrayToEscapedCommandLineString(commandline);
	}

	/**
	 * Returns an escaped string representing the given array of string or null
	 * if the array is empty.
	 */
	private static String arrayToEscapedCommandLineString(Iterable<String> array) {
		// this is lazy and probably wrong
		String ls = null;
		for (String s : array) {
			s = java.util.regex.Matcher.quoteReplacement(s);
			s = s.replaceAll(";", java.util.regex.Matcher.quoteReplacement("\\;"));
			if (s.indexOf(" ") >= 0)
				s = "\"" + s + "\"";
			if (ls == null)
				ls = s;
			else
				ls += " " + s;
		}
		return ls;
	}

}
package se.kth.sys.util.io;

import java.io.IOException;
import java.io.OutputStreamWriter;

import se.kth.sys.util.lang.SystemCommandHandler;

public class UnixSocket {
	private SystemCommandHandler socketCommand = null;
	private OutputStreamWriter socketWriter = null;

	/**
	 * @param socket
	 * @throws IOException
	 */
	void connect(String socket) throws IOException {
		// How to talk to a unix socket in Java.
		socketCommand = new SystemCommandHandler(new String[] { "socat", "-u", "-", "GOPEN:" + socket });
		socketCommand.execute();
		socketWriter = new OutputStreamWriter(socketCommand.getOutputStream());
	}

	/**
	 * 
	 */
	void close() {
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
	 * @param s
	 */
	void send(String s) {
		try {
			socketWriter.write(s);
			socketWriter.flush();
			socketCommand.getOutputStream().flush();
		} catch (IOException e) {}
	}

}

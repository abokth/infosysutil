package se.kth.sys.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A type of Thread for feeding lines of data from a buffer into a class.
 * 
 * After join()ing the thread, call getIOException() to check if any IOException()s were caught.
 */
public class ReadLineThread extends Thread {
	private BufferedReader buffer;
	private Object bufferid;
	private LineReceiver receiver;
	private IOException ioexception = null;

	public ReadLineThread(BufferedReader buf, Object bufferid,
			LineReceiver receiver) {
		this.buffer = buf;
		this.bufferid = bufferid;
		this.receiver = receiver;
	}

	public ReadLineThread(InputStreamReader buf, Object bufferid,
			LineReceiver receiver) {
		this(new BufferedReader(buf), bufferid, receiver);
	}
	public ReadLineThread(InputStream stream, Object bufferid,
			LineReceiver receiver) {
		this(new InputStreamReader(stream), bufferid, receiver);
	}

	@Override
	public void run() {
		do {
			String l;
			try {
				l = buffer.readLine();
			} catch (IOException e) {
				this.ioexception  = e;
				return;
			}
			if (l == null) {
				return;
			}
			receiver.receiveLine(bufferid, l);
		} while(true);
	}

	public IOException getIOException() {
		return ioexception;
	}
}

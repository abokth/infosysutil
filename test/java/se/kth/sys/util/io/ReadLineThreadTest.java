package se.kth.sys.util.io;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.CharBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReadLineThreadTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRun() {
		final boolean[] ok = new boolean[]{false};
		final boolean[] fail = new boolean[]{false};
		Reader s = new StringReader("hej");
		LineReceiver foo = new LineReceiver() {
			@Override
			public void receiveLine(Object bufferid, String line) {
				if (bufferid.equals("fooid") && line.equals("hej") && ok[0] == false)
					ok[0] = true;
				else
					fail[0] = true;
			}
		};
		final ReadLineThread r = new ReadLineThread(new BufferedReader(s), "fooid", foo);
		r.run();
		if (ok[0] == false)
			fail("receiveLine() was never called correctly");
		if (fail[0] == true)
			fail("receiveLine() was called incorrectly");
	}

	@Test
	public void testGetIOException() {
		final IOException e = new IOException("This exception should be thrown.");
		Reader s = new StringReader("hej") {
			@Override
			public int read() throws IOException { throw e; }
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException { throw e; }
			@Override
			public int read(char[] cbuf) throws IOException { throw e; }
			@Override
			public int read(CharBuffer target) throws IOException { throw e; }
		};
		LineReceiver foo = new LineReceiver() {
			@Override
			public void receiveLine(Object bufferid, String line) {
			}
		};
		final ReadLineThread r = new ReadLineThread(new BufferedReader(s), "fooid", foo);
		r.run();
		if (e != r.getIOException())
			fail("The IOException was not caught and saved.");
	}

}

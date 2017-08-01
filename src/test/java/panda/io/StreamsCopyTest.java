package panda.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import panda.io.stream.ByteArrayOutputStream;
import panda.io.stream.NullInputStream;
import panda.io.stream.NullOutputStream;
import panda.io.stream.NullReader;
import panda.io.stream.NullWriter;

/**
 * JUnit tests for Streams. copy methods.
 */
public class StreamsCopyTest extends FileBasedTestCase {

	/*
	 * NOTE this is not particularly beautiful code. A better way to check for flush and close
	 * status would be to implement "trojan horse" wrapper implementations of the various stream
	 * classes, which set a flag when relevant methods are called. (JT)
	 */

	private static final int FILE_SIZE = 1024 * 4 + 1;

	private final byte[] inData = generateTestData(FILE_SIZE);

	public StreamsCopyTest(final String testName) {
		super(testName);
	}

	// ----------------------------------------------------------------
	// Setup
	// ----------------------------------------------------------------

	@Override
	public void setUp() throws Exception {
	}

	@Override
	public void tearDown() throws Exception {
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_inputStreamToOutputStream() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, false, true);

		final int count = Streams.copy(in, out);

		assertEquals("Not all bytes were read", 0, in.available());
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
		assertEquals(inData.length, count);
	}

	public void testCopy_inputStreamToOutputStreamWithBufferSize() throws Exception {
		testCopy_inputStreamToOutputStreamWithBufferSize(1);
		testCopy_inputStreamToOutputStreamWithBufferSize(2);
		testCopy_inputStreamToOutputStreamWithBufferSize(4);
		testCopy_inputStreamToOutputStreamWithBufferSize(8);
		testCopy_inputStreamToOutputStreamWithBufferSize(16);
		testCopy_inputStreamToOutputStreamWithBufferSize(32);
		testCopy_inputStreamToOutputStreamWithBufferSize(64);
		testCopy_inputStreamToOutputStreamWithBufferSize(128);
		testCopy_inputStreamToOutputStreamWithBufferSize(256);
		testCopy_inputStreamToOutputStreamWithBufferSize(512);
		testCopy_inputStreamToOutputStreamWithBufferSize(1024);
		testCopy_inputStreamToOutputStreamWithBufferSize(2048);
		testCopy_inputStreamToOutputStreamWithBufferSize(4096);
		testCopy_inputStreamToOutputStreamWithBufferSize(8192);
		testCopy_inputStreamToOutputStreamWithBufferSize(16384);
	}

	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	private void testCopy_inputStreamToOutputStreamWithBufferSize(final int bufferSize) throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, false, true);

		final long count = Streams.copy(in, out, bufferSize);

		assertEquals("Not all bytes were read", 0, in.available());
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
		assertEquals(inData.length, count);
	}

	public void testCopy_inputStreamToOutputStream_nullIn() throws Exception {
		final OutputStream out = new ByteArrayOutputStream();
		try {
			Streams.copy((InputStream)null, out);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	public void testCopy_inputStreamToOutputStream_nullOut() throws Exception {
		final InputStream in = new ByteArrayInputStream(inData);
		try {
			Streams.copy(in, (OutputStream)null);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	/**
	 * Test Copying file > 2GB - see issue# IO-84
	 */
	public void testCopy_inputStreamToOutputStream_IO84() throws Exception {
		final long size = (long)Integer.MAX_VALUE + (long)1;
		final InputStream in = new NullInputStream(size);
		final OutputStream out = new NullOutputStream();

		// Test copy() method
		assertEquals(-1, Streams.copy(in, out));

		// reset the input
		in.close();

		// Test copyLarge() method
		assertEquals("copyLarge()", size, Streams.copyLarge(in, out));
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_inputStreamToWriter() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final YellOnFlushAndCloseOutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(baout, "US-ASCII");

		Streams.copy(in, writer); // deliberately testing deprecated method
		out.off();
		writer.flush();

		assertEquals("Not all bytes were read", 0, in.available());
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
	}

	public void testCopy_inputStreamToWriter_nullIn() throws Exception {
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(out, "US-ASCII");
		try {
			Streams.copy((InputStream)null, writer);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	public void testCopy_inputStreamToWriter_nullOut() throws Exception {
		final InputStream in = new ByteArrayInputStream(inData);
		try {
			Streams.copy(in, (Writer)null); // deliberately testing deprecated method
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_inputStreamToWriter_Encoding() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final YellOnFlushAndCloseOutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(baout, "US-ASCII");

		Streams.copy(in, writer, "UTF8");
		out.off();
		writer.flush();

		assertEquals("Not all bytes were read", 0, in.available());
		byte[] bytes = baout.toByteArray();
		bytes = new String(bytes, "UTF8").getBytes("US-ASCII");
		assertTrue("Content differs", Arrays.equals(inData, bytes));
	}

	public void testCopy_inputStreamToWriter_Encoding_nullIn() throws Exception {
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(out, "US-ASCII");
		try {
			Streams.copy((InputStream)null, writer, "UTF8");
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	public void testCopy_inputStreamToWriter_Encoding_nullOut() throws Exception {
		final InputStream in = new ByteArrayInputStream(inData);
		try {
			Streams.copy(in, (Writer)null, "UTF8");
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_inputStreamToWriter_Encoding_nullEncoding() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final YellOnFlushAndCloseOutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(baout, "US-ASCII");

		Streams.copy(in, writer, (String)null);
		out.off();
		writer.flush();

		assertEquals("Not all bytes were read", 0, in.available());
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	public void testCopy_readerToOutputStream() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, false, true);

		Streams.copy(reader, out); // deliberately testing deprecated method
		// Note: this method *does* flush. It is equivalent to:
		// OutputStreamWriter _out = new OutputStreamWriter(fout);
		// Streams.copy( fin, _out, 4096 ); // copy( Reader, Writer, int );
		// _out.flush();
		// out = fout;

		// Note: rely on the method to flush
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
	}

	public void testCopy_readerToOutputStream_nullIn() throws Exception { // deliberately testing
																			// deprecated method
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		try {
			Streams.copy((Reader)null, out);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	@SuppressWarnings("resource")
	public void testCopy_readerToOutputStream_nullOut() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");
		try {
			Streams.copy(reader, (OutputStream)null); // deliberately testing deprecated method
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_readerToOutputStream_Encoding() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, false, true);

		Streams.copy(reader, out, "UTF16");
		// note: this method *does* flush.
		// note: we don't flush here; this Streams. method does it for us

		byte[] bytes = baout.toByteArray();
		bytes = new String(bytes, "UTF16").getBytes("US-ASCII");
		assertTrue("Content differs", Arrays.equals(inData, bytes));
	}

	public void testCopy_readerToOutputStream_Encoding_nullIn() throws Exception {
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		try {
			Streams.copy((Reader)null, out, "UTF16");
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_readerToOutputStream_Encoding_nullOut() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");
		try {
			Streams.copy(reader, (OutputStream)null, "UTF16");
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_readerToOutputStream_Encoding_nullEncoding() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, false, true);

		Streams.copy(reader, out, (String)null);
		// note: this method *does* flush.
		// note: we don't flush here; this Streams. method does it for us

		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
	}

	// -----------------------------------------------------------------------
	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_readerToWriter() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");

		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final YellOnFlushAndCloseOutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(baout, "US-ASCII");

		final int count = Streams.copy(reader, writer);
		out.off();
		writer.flush();
		assertEquals("The number of characters returned by copy is wrong", inData.length, count);
		assertEquals("Sizes differ", inData.length, baout.size());
		assertTrue("Content differs", Arrays.equals(inData, baout.toByteArray()));
	}

	public void testCopy_readerToWriter_nullIn() throws Exception {
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		final OutputStream out = new YellOnFlushAndCloseOutputStream(baout, true, true);
		final Writer writer = new OutputStreamWriter(out, "US-ASCII");
		try {
			Streams.copy((Reader)null, writer);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	@SuppressWarnings("resource")
	// 'in' is deliberately not closed
	public void testCopy_readerToWriter_nullOut() throws Exception {
		InputStream in = new ByteArrayInputStream(inData);
		in = new YellOnCloseInputStream(in);
		final Reader reader = new InputStreamReader(in, "US-ASCII");
		try {
			Streams.copy(reader, (Writer)null);
			fail();
		}
		catch (final NullPointerException ex) {
		}
	}

	/**
	 * Test Copying file > 2GB - see issue# IO-84
	 */
	public void testCopy_readerToWriter_IO84() throws Exception {
		final long size = (long)Integer.MAX_VALUE + (long)1;
		final Reader reader = new NullReader(size);
		final Writer writer = new NullWriter();

		// Test copy() method
		assertEquals(-1, Streams.copy(reader, writer));

		// reset the input
		reader.close();

		// Test copyLarge() method
		assertEquals("copyLarge()", size, Streams.copyLarge(reader, writer));

	}

}

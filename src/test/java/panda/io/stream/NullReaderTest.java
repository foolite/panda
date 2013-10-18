package panda.io.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

/**
 * JUnit Test Case for {@link NullReader}.
 */
public class NullReaderTest extends TestCase {

	/** Constructor */
	public NullReaderTest(final String name) {
		super(name);
	}

	/** Set up */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/** Tear Down */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test <code>available()</code> method.
	 */
	public void testRead() throws Exception {
		final int size = 5;
		final TestNullReader reader = new TestNullReader(size);
		for (int i = 0; i < size; i++) {
			assertEquals("Check Value [" + i + "]", i, reader.read());
		}

		// Check End of File
		assertEquals("End of File", -1, reader.read());

		// Test reading after the end of file
		try {
			final int result = reader.read();
			fail("Should have thrown an IOException, value=[" + result + "]");
		}
		catch (final IOException e) {
			assertEquals("Read after end of file", e.getMessage());
		}

		// Close - should reset
		reader.close();
		assertEquals("Available after close", 0, reader.getPosition());
	}

	/**
	 * Test <code>read(char[])</code> method.
	 */
	public void testReadCharArray() throws Exception {
		final char[] chars = new char[10];
		final Reader reader = new TestNullReader(15);

		// Read into array
		final int count1 = reader.read(chars);
		assertEquals("Read 1", chars.length, count1);
		for (int i = 0; i < count1; i++) {
			assertEquals("Check Chars 1", i, chars[i]);
		}

		// Read into array
		final int count2 = reader.read(chars);
		assertEquals("Read 2", 5, count2);
		for (int i = 0; i < count2; i++) {
			assertEquals("Check Chars 2", count1 + i, chars[i]);
		}

		// End of File
		final int count3 = reader.read(chars);
		assertEquals("Read 3 (EOF)", -1, count3);

		// Test reading after the end of file
		try {
			final int count4 = reader.read(chars);
			fail("Should have thrown an IOException, value=[" + count4 + "]");
		}
		catch (final IOException e) {
			assertEquals("Read after end of file", e.getMessage());
		}

		// reset by closing
		reader.close();

		// Read into array using offset & length
		final int offset = 2;
		final int lth = 4;
		final int count5 = reader.read(chars, offset, lth);
		assertEquals("Read 5", lth, count5);
		for (int i = offset; i < lth; i++) {
			assertEquals("Check Chars 3", i, chars[i]);
		}
	}

	/**
	 * Test when configured to throw an EOFException at the end of file (rather than return -1).
	 */
	public void testEOFException() throws Exception {
		final Reader reader = new TestNullReader(2, false, true);
		assertEquals("Read 1", 0, reader.read());
		assertEquals("Read 2", 1, reader.read());
		try {
			final int result = reader.read();
			fail("Should have thrown an EOFException, value=[" + result + "]");
		}
		catch (final EOFException e) {
			// expected
		}
		reader.close();
	}

	/**
	 * Test <code>mark()</code> and <code>reset()</code> methods.
	 */
	public void testMarkAndReset() throws Exception {
		int position = 0;
		final int readlimit = 10;
		final Reader reader = new TestNullReader(100, true, false);

		assertTrue("Mark Should be Supported", reader.markSupported());

		// No Mark
		try {
			reader.reset();
			fail("Read limit exceeded, expected IOException ");
		}
		catch (final IOException e) {
			assertEquals("No Mark IOException message", "No position has been marked", e.getMessage());
		}

		for (; position < 3; position++) {
			assertEquals("Read Before Mark [" + position + "]", position, reader.read());
		}

		// Mark
		reader.mark(readlimit);

		// Read further
		for (int i = 0; i < 3; i++) {
			assertEquals("Read After Mark [" + i + "]", position + i, reader.read());
		}

		// Reset
		reader.reset();

		// Read From marked position
		for (int i = 0; i < readlimit + 1; i++) {
			assertEquals("Read After Reset [" + i + "]", position + i, reader.read());
		}

		// Reset after read limit passed
		try {
			reader.reset();
			fail("Read limit exceeded, expected IOException ");
		}
		catch (final IOException e) {
			assertEquals("Read limit IOException message", "Marked position [" + position
					+ "] is no longer valid - passed the read limit [" + readlimit + "]", e.getMessage());
		}
		reader.close();
	}

	/**
	 * Test <code>mark()</code> not supported.
	 */
	public void testMarkNotSupported() throws Exception {
		final Reader reader = new TestNullReader(100, false, true);
		assertFalse("Mark Should NOT be Supported", reader.markSupported());

		try {
			reader.mark(5);
			fail("mark() should throw UnsupportedOperationException");
		}
		catch (final UnsupportedOperationException e) {
			assertEquals("mark() error message", "Mark not supported", e.getMessage());
		}

		try {
			reader.reset();
			fail("reset() should throw UnsupportedOperationException");
		}
		catch (final UnsupportedOperationException e) {
			assertEquals("reset() error message", "Mark not supported", e.getMessage());
		}
		reader.close();
	}

	/**
	 * Test <code>skip()</code> method.
	 */
	public void testSkip() throws Exception {
		final Reader reader = new TestNullReader(10, true, false);
		assertEquals("Read 1", 0, reader.read());
		assertEquals("Read 2", 1, reader.read());
		assertEquals("Skip 1", 5, reader.skip(5));
		assertEquals("Read 3", 7, reader.read());
		assertEquals("Skip 2", 2, reader.skip(5)); // only 2 left to skip
		assertEquals("Skip 3 (EOF)", -1, reader.skip(5)); // End of file
		try {
			reader.skip(5); //
			fail("Expected IOException for skipping after end of file");
		}
		catch (final IOException e) {
			assertEquals("Skip after EOF IOException message", "Skip after end of file", e.getMessage());
		}
		reader.close();
	}

	// ------------- Test NullReader implementation -------------

	private static final class TestNullReader extends NullReader {
		public TestNullReader(final int size) {
			super(size);
		}

		public TestNullReader(final int size, final boolean markSupported, final boolean throwEofException) {
			super(size, markSupported, throwEofException);
		}

		@Override
		protected int processChar() {
			return (int)getPosition() - 1;
		}

		@Override
		protected void processChars(final char[] chars, final int offset, final int length) {
			final int startPos = (int)getPosition() - length;
			for (int i = offset; i < length; i++) {
				chars[i] = (char)(startPos + i);
			}
		}

	}
}

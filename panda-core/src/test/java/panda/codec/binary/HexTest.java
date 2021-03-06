package panda.codec.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import panda.codec.DecoderException;
import panda.codec.EncoderException;
import panda.lang.Charsets;
import panda.lang.Strings;

/**
 * Tests {@link panda.codec.binary.Hex}.
 */
public class HexTest {

	private static final String BAD_ENCODING_NAME = "UNKNOWN";

	private final static boolean LOG = false;

	private boolean charsetSanityCheck(final String name) {
		final String source = "the quick brown dog jumped over the lazy fox";
		try {
			final byte[] bytes = source.getBytes(name);
			final String str = new String(bytes, name);
			final boolean equals = source.equals(str);
			if (equals == false) {
				// Here with:
				//
				// Java Sun 1.4.2_19 x86 32-bits on Windows XP
				// JIS_X0212-1990
				// x-JIS0208
				//
				// Java Sun 1.5.0_17 x86 32-bits on Windows XP
				// JIS_X0212-1990
				// x-IBM834
				// x-JIS0208
				// x-MacDingbat
				// x-MacSymbol
				//
				// Java Sun 1.6.0_14 x86 32-bits
				// JIS_X0212-1990
				// x-IBM834
				// x-JIS0208
				// x-MacDingbat
				// x-MacSymbol
				//
				log("FAILED charsetSanityCheck=Interesting Java charset oddity: Roundtrip failed for " + name);
			}
			return equals;
		}
		catch (final UnsupportedEncodingException e) {
			// Should NEVER happen since we are getting the name from the Charset class.
			if (LOG) {
				log("FAILED charsetSanityCheck=" + name + ", e=" + e);
				log(e);
			}
			return false;
		}
		catch (final UnsupportedOperationException e) {
			// Caught here with:
			// x-JISAutoDetect on Windows XP and Java Sun 1.4.2_19 x86 32-bits
			// x-JISAutoDetect on Windows XP and Java Sun 1.5.0_17 x86 32-bits
			// x-JISAutoDetect on Windows XP and Java Sun 1.6.0_14 x86 32-bits
			if (LOG) {
				log("FAILED charsetSanityCheck=" + name + ", e=" + e);
				log(e);
			}
			return false;
		}
	}

	private void checkDecodeHexCharArrayOddCharacters(final char[] data) {
		try {
			Hex.decodeHex(data);
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	private void checkDecodeHexCharArrayOddCharacters(final String data) {
		try {
			Hex.decodeHex(data);
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	private void log(final String s) {
		if (LOG) {
			System.out.println(s);
			System.out.flush();
		}
	}

	private void log(final Throwable t) {
		if (LOG) {
			t.printStackTrace(System.out);
			System.out.flush();
		}
	}

	@Test
	public void testCustomCharset() throws UnsupportedEncodingException, DecoderException {
		for (final String name : Charset.availableCharsets().keySet()) {
			testCustomCharset(name, "testCustomCharset");
		}
	}

	/**
	 * @param name
	 * @param parent
	 * @throws UnsupportedEncodingException
	 * @throws DecoderException
	 */
	private void testCustomCharset(final String name, final String parent)
			throws UnsupportedEncodingException, DecoderException {
		if (charsetSanityCheck(name) == false) {
			return;
		}
		log(parent + "=" + name);
		final Hex customCodec = new Hex(name);
		// source data
		final String sourceString = "Hello World";
		final byte[] sourceBytes = sourceString.getBytes(name);
		// test 1
		// encode source to hex string to bytes with charset
		final byte[] actualEncodedBytes = customCodec.encode(sourceBytes);
		// encode source to hex string...
		String expectedHexString = Hex.encodeHexString(sourceBytes);
		// ... and get the bytes in the expected charset
		final byte[] expectedHexStringBytes = expectedHexString.getBytes(name);
		Assert.assertTrue(Arrays.equals(expectedHexStringBytes, actualEncodedBytes));
		// test 2
		String actualStringFromBytes = new String(actualEncodedBytes, name);
		assertEquals(
			name + ", expectedHexString=" + expectedHexString + ", actualStringFromBytes=" + actualStringFromBytes,
			expectedHexString, actualStringFromBytes);
		// second test:
		final Hex utf8Codec = new Hex();
		expectedHexString = "48656c6c6f20576f726c64";
		final byte[] decodedUtf8Bytes = (byte[])utf8Codec.decode(expectedHexString);
		actualStringFromBytes = new String(decodedUtf8Bytes, utf8Codec.getCharset());
		// sanity check:
		assertEquals(name, sourceString, actualStringFromBytes);
		// actual check:
		final byte[] decodedCustomBytes = customCodec.decode(actualEncodedBytes);
		actualStringFromBytes = new String(decodedCustomBytes, name);
		assertEquals(name, sourceString, actualStringFromBytes);
	}

	@Test(expected = UnsupportedCharsetException.class)
	public void testCustomCharsetBadName() {
		new Hex(BAD_ENCODING_NAME);
	}

	@Test
	public void testCustomCharsetToString() {
		assertTrue(new Hex().toString().indexOf(Hex.DEFAULT_CHARSET_NAME) >= 0);
	}

	@Test
	public void testDecodeArrayOddCharacters() {
		try {
			new Hex().decode(new byte[] { 65 });
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeBadCharacterPos0() {
		try {
			new Hex().decode("q0");
			fail("An exception wasn't thrown when trying to decode an illegal character");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeBadCharacterPos1() {
		try {
			new Hex().decode("0q");
			fail("An exception wasn't thrown when trying to decode an illegal character");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeByteArrayEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], new Hex().decode(new byte[0])));
	}

	@Test
	public void testDecodeByteArrayObjectEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], (byte[])new Hex().decode((Object)new byte[0])));
	}

	@Test
	public void testDecodeByteArrayOddCharacters() {
		try {
			new Hex().decode(new byte[] { 65 });
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeByteBufferEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], new Hex().decode(ByteBuffer.allocate(0))));
	}

	@Test
	public void testDecodeByteBufferObjectEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], (byte[])new Hex().decode((Object)ByteBuffer.allocate(0))));
	}

	@Test
	public void testDecodeByteBufferOddCharacters() {
		final ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put((byte)65);
		try {
			new Hex().decode(buffer);
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeHexCharArrayEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], Hex.decodeHex(new char[0])));
	}

	@Test
	public void testDecodeHexStringEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], Hex.decodeHex("")));
	}

	@Test
	public void testDecodeClassCastException() {
		try {
			new Hex().decode(new int[] { 65 });
			fail("An exception wasn't thrown when trying to decode.");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeHexCharArrayOddCharacters1() {
		checkDecodeHexCharArrayOddCharacters(new char[] { 'A' });
	}

	@Test
	public void testDecodeHexStringOddCharacters1() {
		checkDecodeHexCharArrayOddCharacters("A");
	}

	@Test
	public void testDecodeHexCharArrayOddCharacters3() {
		checkDecodeHexCharArrayOddCharacters(new char[] { 'A', 'B', 'C' });
	}

	@Test
	public void testDecodeHexCharArrayOddCharacters5() {
		checkDecodeHexCharArrayOddCharacters(new char[] { 'A', 'B', 'C', 'D', 'E' });
	}

	@Test
	public void testDecodeHexStringOddCharacters() {
		try {
			new Hex().decode("6");
			fail("An exception wasn't thrown when trying to decode an odd number of characters");
		}
		catch (final DecoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testDecodeStringEmpty() throws DecoderException {
		assertTrue(Arrays.equals(new byte[0], (byte[])new Hex().decode("")));
	}

	@Test
	public void testEncodeByteArrayEmpty() {
		assertTrue(Arrays.equals(new byte[0], new Hex().encode(new byte[0])));
	}

	@Test
	public void testEncodeByteArrayObjectEmpty() throws EncoderException {
		assertTrue(Arrays.equals(new char[0], (char[])new Hex().encode((Object)new byte[0])));
	}

	@Test
	public void testEncodeByteBufferEmpty() {
		assertTrue(Arrays.equals(new byte[0], new Hex().encode(ByteBuffer.allocate(0))));
	}

	@Test
	public void testEncodeByteBufferObjectEmpty() throws EncoderException {
		assertTrue(Arrays.equals(new char[0], (char[])new Hex().encode((Object)ByteBuffer.allocate(0))));
	}

	@Test
	public void testEncodeClassCastException() {
		try {
			new Hex().encode(new int[] { 65 });
			fail("An exception wasn't thrown when trying to encode.");
		}
		catch (final EncoderException e) {
			// Expected exception
		}
	}

	@Test
	public void testEncodeDecodeHexCharArrayRandom() throws DecoderException, EncoderException {
		final Random random = new Random();

		final Hex hex = new Hex();
		for (int i = 5; i > 0; i--) {
			final byte[] data = new byte[random.nextInt(10000) + 1];
			random.nextBytes(data);

			// static API
			final char[] encodedChars = Hex.encodeHex(data);
			byte[] decodedBytes = Hex.decodeHex(encodedChars);
			assertTrue(Arrays.equals(data, decodedBytes));

			// instance API with array parameter
			final byte[] encodedStringBytes = hex.encode(data);
			decodedBytes = hex.decode(encodedStringBytes);
			assertTrue(Arrays.equals(data, decodedBytes));

			// instance API with char[] (Object) parameter
			String dataString = new String(encodedChars);
			char[] encodedStringChars = (char[])hex.encode(dataString);
			decodedBytes = (byte[])hex.decode(encodedStringChars);
			assertTrue(Arrays.equals(Strings.getBytesUtf8(dataString), decodedBytes));

			// instance API with String (Object) parameter
			dataString = new String(encodedChars);
			encodedStringChars = (char[])hex.encode(dataString);
			decodedBytes = (byte[])hex.decode(new String(encodedStringChars));
			assertTrue(Arrays.equals(Strings.getBytesUtf8(dataString), decodedBytes));
		}
	}

	@Test
	public void testEncodeHexByteArrayEmpty() {
		assertTrue(Arrays.equals(new char[0], Hex.encodeHex(new byte[0])));
		assertTrue(Arrays.equals(new byte[0], new Hex().encode(new byte[0])));
	}

	@Test
	public void testHelloWorldLowerCaseHex() {
		final byte[] b = Strings.getBytesUtf8("Hello World");
		final String expected = "48656c6c6f20576f726c64";
		char[] actual;
		actual = Hex.encodeHex(b);
		assertEquals(expected, new String(actual));
		actual = Hex.encodeHex(b, true);
		assertEquals(expected, new String(actual));
		actual = Hex.encodeHex(b, false);
		assertFalse(expected.equals(new String(actual)));
	}

	@Test
	public void testEncodeHexByteArrayHelloWorldUpperCaseHex() {
		final byte[] b = Strings.getBytesUtf8("Hello World");
		final String expected = "48656C6C6F20576F726C64";
		char[] actual;
		actual = Hex.encodeHex(b);
		assertFalse(expected.equals(new String(actual)));
		actual = Hex.encodeHex(b, true);
		assertFalse(expected.equals(new String(actual)));
		actual = Hex.encodeHex(b, false);
		assertTrue(expected.equals(new String(actual)));
	}

	@Test
	public void testEncodeHexByteArrayZeroes() {
		final char[] c = Hex.encodeHex(new byte[36]);
		assertEquals("000000000000000000000000000000000000000000000000000000000000000000000000", new String(c));
	}

	@Test
	public void testEncodeHexByteBufferEmpty() {
		assertTrue(Arrays.equals(new char[0], Hex.encodeHex(ByteBuffer.allocate(0))));
		assertTrue(Arrays.equals(new byte[0], new Hex().encode(ByteBuffer.allocate(0))));
	}

	@Test
	public void testEncodeHexByteBufferHelloWorldLowerCaseHex() {
		final ByteBuffer b = Strings.getByteBufferUtf8("Hello World");
		final String expected = "48656c6c6f20576f726c64";
		char[] actual;
		actual = Hex.encodeHex(b);
		assertEquals(expected, new String(actual));
		actual = Hex.encodeHex(b, true);
		assertEquals(expected, new String(actual));
		actual = Hex.encodeHex(b, false);
		assertFalse(expected.equals(new String(actual)));
	}

	@Test
	public void testEncodeHexByteBufferHelloWorldUpperCaseHex() {
		final ByteBuffer b = Strings.getByteBufferUtf8("Hello World");
		final String expected = "48656C6C6F20576F726C64";
		char[] actual;
		actual = Hex.encodeHex(b);
		assertFalse(expected.equals(new String(actual)));
		actual = Hex.encodeHex(b, true);
		assertFalse(expected.equals(new String(actual)));
		actual = Hex.encodeHex(b, false);
		assertTrue(expected.equals(new String(actual)));
	}

	@Test
	public void testEncodeHex_ByteBufferOfZeroes() {
		final char[] c = Hex.encodeHex(ByteBuffer.allocate(36));
		assertEquals("000000000000000000000000000000000000000000000000000000000000000000000000", new String(c));
	}

	@Test
	public void testEncodeHexByteString_ByteBufferOfZeroes() {
		final String c = Hex.encodeHexString(ByteBuffer.allocate(36));
		assertEquals("000000000000000000000000000000000000000000000000000000000000000000000000", c);
	}

	@Test
	public void testEncodeHexByteString_ByteArrayOfZeroes() {
		final String c = Hex.encodeHexString(new byte[36]);
		assertEquals("000000000000000000000000000000000000000000000000000000000000000000000000", c);
	}

	@Test
	public void testEncodeHexByteString_ByteArrayBoolean_ToLowerCase() {
		assertEquals("0a", Hex.encodeHexString(new byte[] { 10 }, true));
	}

	@Test
	public void testEncodeHexByteString_ByteArrayBoolean_ToUpperCase() {
		assertEquals("0A", Hex.encodeHexString(new byte[] { 10 }, false));
	}

	@Test
	public void testEncodeHexByteString_ByteBufferBoolean_ToLowerCase() {
		assertEquals("0a", Hex.encodeHexString(ByteBuffer.wrap(new byte[] { 10 }), true));
	}

	@Test
	public void testEncodeHexByteString_ByteBufferBoolean_ToUpperCase() {
		assertEquals("0A", Hex.encodeHexString(ByteBuffer.wrap(new byte[] { 10 }), false));
	}

	@Test
	public void testEncodeStringEmpty() throws EncoderException {
		assertTrue(Arrays.equals(new char[0], (char[])new Hex().encode("")));
	}

	@Test
	public void testGetCharset() {
		Assert.assertEquals(Charsets.CS_UTF_8, new Hex(Charsets.UTF_8).getCharset());
	}

	@Test
	public void testGetCharsetName() {
		Assert.assertEquals(Charsets.UTF_8, new Hex(Charsets.UTF_8).getCharsetName());
	}

	@Test
	public void testRequiredCharset() throws UnsupportedEncodingException, DecoderException {
		testCustomCharset("UTF-8", "testRequiredCharset");
		testCustomCharset("UTF-16", "testRequiredCharset");
		testCustomCharset("UTF-16BE", "testRequiredCharset");
		testCustomCharset("UTF-16LE", "testRequiredCharset");
		testCustomCharset("US-ASCII", "testRequiredCharset");
		testCustomCharset("ISO8859_1", "testRequiredCharset");
	}
}

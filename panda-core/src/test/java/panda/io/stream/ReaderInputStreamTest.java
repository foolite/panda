package panda.io.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Test;

@SuppressWarnings("resource")
public class ReaderInputStreamTest {
    private static final String TEST_STRING = "\u00e0 peine arriv\u00e9s nous entr\u00e2mes dans sa chambre";
    private static final String LARGE_TEST_STRING;
    
    static {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i<100; i++) {
            buffer.append(TEST_STRING);
        }
        LARGE_TEST_STRING = buffer.toString();
    }
    
    private Random random = new Random();
    
	private void testWithSingleByteRead(String testString, String charsetName) throws IOException {
        byte[] bytes = testString.getBytes(charsetName);
        ReaderInputStream in = new ReaderInputStream(new StringReader(testString), charsetName);
        for (byte b : bytes) {
            int read = in.read();
            assertTrue(read >= 0);
            assertTrue(read <= 255);
            assertEquals(b, (byte)read);
        }
        assertEquals(-1, in.read());
    }
    
    private void testWithBufferedRead(String testString, String charsetName) throws IOException {
        byte[] expected = testString.getBytes(charsetName);
        ReaderInputStream in = new ReaderInputStream(new StringReader(testString), charsetName);
        byte[] buffer = new byte[128];
        int offset = 0;
        while (true) {
            int bufferOffset = random.nextInt(64);
            int bufferLength = random.nextInt(64);
            int read = in.read(buffer, bufferOffset, bufferLength);
            if (read == -1) {
                assertEquals(offset, expected.length);
                break;
            } else {
                assertTrue(read <= bufferLength);
                while (read > 0) {
                    assertTrue(offset < expected.length);
                    assertEquals(expected[offset], buffer[bufferOffset]);
                    offset++;
                    bufferOffset++;
                    read--;
                }
            }
        }
    }
    
    @Test
    public void testUTF8WithSingleByteRead() throws IOException {
        testWithSingleByteRead(TEST_STRING, "UTF-8");
    }
    
    @Test
    public void testLargeUTF8WithSingleByteRead() throws IOException {
        testWithSingleByteRead(LARGE_TEST_STRING, "UTF-8");
    }
    
    @Test
    public void testUTF8WithBufferedRead() throws IOException {
        testWithBufferedRead(TEST_STRING, "UTF-8");
    }
    
    @Test
    public void testLargeUTF8WithBufferedRead() throws IOException {
        testWithBufferedRead(LARGE_TEST_STRING, "UTF-8");
    }
    
    @Test
    public void testUTF16WithSingleByteRead() throws IOException {
        testWithSingleByteRead(TEST_STRING, "UTF-16");
    }
    
    @Test
    public void testReadZero() throws Exception {
        final String inStr = "test";
        ReaderInputStream r = new ReaderInputStream(new StringReader(inStr));
        byte[] bytes = new byte[30];
        assertEquals(0, r.read(bytes, 0, 0));
        assertEquals(inStr.length(), r.read(bytes, 0, inStr.length()+1));
        // Should always return 0 for length == 0
        assertEquals(0, r.read(bytes, 0, 0));
    }
    
    @Test
    public void testReadZeroEmptyString() throws Exception {
        ReaderInputStream r = new ReaderInputStream(new StringReader(""));
        byte[] bytes = new byte[30];
        // Should always return 0 for length == 0
        assertEquals(0, r.read(bytes, 0, 0));
        assertEquals(-1, r.read(bytes, 0, 1));
        assertEquals(0, r.read(bytes, 0, 0));
        assertEquals(-1, r.read(bytes, 0, 1));
    }
    
    /**
     * Tests https://issues.apache.org/jira/browse/IO-277
	 * @throws IOException if an error occurs
     */
    @Test
    public void testCharsetMismatchInfiniteLoop() throws IOException {
        // Input is UTF-8 bytes: 0xE0 0xB2 0xA0
        char[] inputChars = new char[] { (char) 0xE0, (char) 0xB2, (char) 0xA0 };
        // Charset charset = Charset.forName("UTF-8"); // works
        Charset charset = Charset.forName("ASCII"); // infinite loop
        ReaderInputStream stream = new ReaderInputStream(new CharArrayReader(inputChars), charset);
        try {
            while (stream.read() != -1) {
            }
        } finally {
            stream.close();
        }
    }
}

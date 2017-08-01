package panda.net.smtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class SMTPHeaderTestCase {

	private SMTPHeader header;
	private Date beforeDate;

	@Before
	public void setUp() {
		beforeDate = new Date();
		header = new SMTPHeader("from@here.invalid", "to@there.invalid", "Test email");
	}

	@Test
	public void testToString() {
		assertNotNull(header);
		// Note that the DotTerminatedMessageWriter converts LF to CRLF
		assertEquals("From: from@here.invalid\nTo: to@there.invalid\nSubject: Test email\n\n",
			checkDate(header.toString()));
	}

	@Test
	public void testToStringNoSubject() {
		SMTPHeader hdr = new SMTPHeader("from@here.invalid", "to@there.invalid", null);
		assertNotNull(hdr);
		// Note that the DotTerminatedMessageWriter converts LF to CRLF
		assertEquals("From: from@here.invalid\nTo: to@there.invalid\n\n", checkDate(hdr.toString()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToStringNoFrom() {
		new SMTPHeader(null, null, null);
	}

	@Test
	public void testToStringNoTo() {
		SMTPHeader hdr = new SMTPHeader("from@here.invalid", null, null);
		assertNotNull(hdr);
		// Note that the DotTerminatedMessageWriter converts LF to CRLF
		assertEquals("From: from@here.invalid\n\n", checkDate(hdr.toString()));
	}

	@Test
	public void testToStringAddHeader() {
		SMTPHeader hdr = new SMTPHeader("from@here.invalid", null, null);
		assertNotNull(hdr);
		hdr.set("X-Header1", "value 1");
		hdr.set("X-Header2", "value 2");
		// Note that the DotTerminatedMessageWriter converts LF to CRLF
		assertEquals("X-Header1: value 1\nX-Header2: value 2\nFrom: from@here.invalid\n\n", checkDate(hdr.toString()));
	}

	@Test
	public void testToStringAddHeaderDate() {
		SMTPHeader hdr = new SMTPHeader("from@here.invalid", null, null);
		assertNotNull(hdr);
		hdr.set("Date", "dummy date");
		// does not replace the Date field
		assertEquals("Date: dummy date\nFrom: from@here.invalid\n\n", hdr.toString());
	}

	// Returns the msg without a date
	private String checkDate(String msg) {
		Pattern pat = Pattern.compile("^(Date: (.+))$", Pattern.MULTILINE);
		Matcher m = pat.matcher(msg);
		if (m.find()) {
			String date = m.group(2);
			final String pattern = "EEE, dd MMM yyyy HH:mm:ss Z"; // Fri, 21 Nov 1997 09:55:06 -0600
			final SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
			try {
				final Date sentDate = format.parse(date);
				// Round to nearest second because the text format does not include ms
				long sentSecs = sentDate.getTime() / 1000;
				long beforeDateSecs = beforeDate.getTime() / 1000;
				Date afterDate = new Date();
				long afterDateSecs = afterDate.getTime() / 1000;
				if (sentSecs < beforeDateSecs) {
					fail(sentDate + " should be after " + beforeDate);
				}
				if (sentSecs > (afterDateSecs)) {
					fail(sentDate + " should be before " + afterDate);
				}
			}
			catch (ParseException e) {
				fail("" + e);
			}

			int start = m.start(1);
			int end = m.end(1);
			if (start == 0) {
				return msg.substring(end + 1);
			}
			else {
				return msg.substring(0, start) + msg.substring(end + 1);
			}
		}
		else {
			fail("Expecting Date header in " + msg);
		}
		return null;
	}
}

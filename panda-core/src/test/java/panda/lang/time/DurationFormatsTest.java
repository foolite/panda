package panda.lang.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

/**
 * TestCase for DurationFormats.
 */
public class DurationFormatsTest {

	// -----------------------------------------------------------------------
	@Test
	public void testConstructor() {
		assertNotNull(new DurationFormats());
		final Constructor<?>[] cons = DurationFormats.class.getDeclaredConstructors();
		assertEquals(1, cons.length);
		assertTrue(Modifier.isPublic(cons[0].getModifiers()));
		assertTrue(Modifier.isPublic(DurationFormats.class.getModifiers()));
		assertFalse(Modifier.isFinal(DurationFormats.class.getModifiers()));
	}

	// -----------------------------------------------------------------------
	@Test
	public void testFormatDurationWords() {
		String text = null;

		text = DurationFormats.formatDurationWords(50 * 1000, true, false);
		assertEquals("50 seconds", text);
		text = DurationFormats.formatDurationWords(65 * 1000, true, false);
		assertEquals("1 minute 5 seconds", text);
		text = DurationFormats.formatDurationWords(120 * 1000, true, false);
		assertEquals("2 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(121 * 1000, true, false);
		assertEquals("2 minutes 1 second", text);
		text = DurationFormats.formatDurationWords(72 * 60 * 1000, true, false);
		assertEquals("1 hour 12 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(24 * 60 * 60 * 1000, true, false);
		assertEquals("1 day 0 hours 0 minutes 0 seconds", text);

		text = DurationFormats.formatDurationWords(50 * 1000, true, true);
		assertEquals("50 seconds", text);
		text = DurationFormats.formatDurationWords(65 * 1000, true, true);
		assertEquals("1 minute 5 seconds", text);
		text = DurationFormats.formatDurationWords(120 * 1000, true, true);
		assertEquals("2 minutes", text);
		text = DurationFormats.formatDurationWords(121 * 1000, true, true);
		assertEquals("2 minutes 1 second", text);
		text = DurationFormats.formatDurationWords(72 * 60 * 1000, true, true);
		assertEquals("1 hour 12 minutes", text);
		text = DurationFormats.formatDurationWords(24 * 60 * 60 * 1000, true, true);
		assertEquals("1 day", text);

		text = DurationFormats.formatDurationWords(50 * 1000, false, true);
		assertEquals("0 days 0 hours 0 minutes 50 seconds", text);
		text = DurationFormats.formatDurationWords(65 * 1000, false, true);
		assertEquals("0 days 0 hours 1 minute 5 seconds", text);
		text = DurationFormats.formatDurationWords(120 * 1000, false, true);
		assertEquals("0 days 0 hours 2 minutes", text);
		text = DurationFormats.formatDurationWords(121 * 1000, false, true);
		assertEquals("0 days 0 hours 2 minutes 1 second", text);
		text = DurationFormats.formatDurationWords(72 * 60 * 1000, false, true);
		assertEquals("0 days 1 hour 12 minutes", text);
		text = DurationFormats.formatDurationWords(24 * 60 * 60 * 1000, false, true);
		assertEquals("1 day", text);

		text = DurationFormats.formatDurationWords(50 * 1000, false, false);
		assertEquals("0 days 0 hours 0 minutes 50 seconds", text);
		text = DurationFormats.formatDurationWords(65 * 1000, false, false);
		assertEquals("0 days 0 hours 1 minute 5 seconds", text);
		text = DurationFormats.formatDurationWords(120 * 1000, false, false);
		assertEquals("0 days 0 hours 2 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(121 * 1000, false, false);
		assertEquals("0 days 0 hours 2 minutes 1 second", text);
		text = DurationFormats.formatDurationWords(72 * 60 * 1000, false, false);
		assertEquals("0 days 1 hour 12 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(24 * 60 * 60 * 1000 + 72 * 60 * 1000, false, false);
		assertEquals("1 day 1 hour 12 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(2 * 24 * 60 * 60 * 1000 + 72 * 60 * 1000, false, false);
		assertEquals("2 days 1 hour 12 minutes 0 seconds", text);
		for (int i = 2; i < 31; i++) {
			text = DurationFormats.formatDurationWords(i * 24 * 60 * 60 * 1000L, false, false);
			// assertEquals(i + " days 0 hours 0 minutes 0 seconds", text);
			//
			// junit.framework.ComparisonFailure: expected:<25 days 0 hours 0 minutes 0...> but
			// was:<-24 days -17 hours
			// -2 minutes -47...>
			// at junit.framework.Assert.assertEquals(Assert.java:81)
			// at junit.framework.Assert.assertEquals(Assert.java:87)
			// at
			// org.apache.commons.lang.time.DurationFormatUtilsTest.testFormatDurationWords(DurationFormatUtilsTest.java:124)
			// at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
			// at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
			// at
			// sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
			// at java.lang.reflect.Method.invoke(Method.java:324)
			// at junit.framework.TestCase.runTest(TestCase.java:154)
			// at junit.framework.TestCase.runBare(TestCase.java:127)
			// at junit.framework.TestResult$1.protect(TestResult.java:106)
			// at junit.framework.TestResult.runProtected(TestResult.java:124)
			// at junit.framework.TestResult.run(TestResult.java:109)
			// at junit.framework.TestCase.run(TestCase.java:118)
			// at junit.framework.TestSuite.runTest(TestSuite.java:208)
			// at junit.framework.TestSuite.run(TestSuite.java:203)
			// at
			// org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:478)
			// at
			// org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:344)
			// at
			// org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
		}
	}

	/**
	 * Tests that "1 <unit>s" gets converted to "1 <unit>" but that "11 <unit>s" is left alone.
	 */
	@Test
	public void testFormatDurationPluralWords() {
		final long oneSecond = 1000;
		final long oneMinute = oneSecond * 60;
		final long oneHour = oneMinute * 60;
		final long oneDay = oneHour * 24;
		String text = null;

		text = DurationFormats.formatDurationWords(oneSecond, false, false);
		assertEquals("0 days 0 hours 0 minutes 1 second", text);
		text = DurationFormats.formatDurationWords(oneSecond * 2, false, false);
		assertEquals("0 days 0 hours 0 minutes 2 seconds", text);
		text = DurationFormats.formatDurationWords(oneSecond * 11, false, false);
		assertEquals("0 days 0 hours 0 minutes 11 seconds", text);

		text = DurationFormats.formatDurationWords(oneMinute, false, false);
		assertEquals("0 days 0 hours 1 minute 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneMinute * 2, false, false);
		assertEquals("0 days 0 hours 2 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneMinute * 11, false, false);
		assertEquals("0 days 0 hours 11 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneMinute + oneSecond, false, false);
		assertEquals("0 days 0 hours 1 minute 1 second", text);

		text = DurationFormats.formatDurationWords(oneHour, false, false);
		assertEquals("0 days 1 hour 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneHour * 2, false, false);
		assertEquals("0 days 2 hours 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneHour * 11, false, false);
		assertEquals("0 days 11 hours 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneHour + oneMinute + oneSecond, false, false);
		assertEquals("0 days 1 hour 1 minute 1 second", text);

		text = DurationFormats.formatDurationWords(oneDay, false, false);
		assertEquals("1 day 0 hours 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneDay * 2, false, false);
		assertEquals("2 days 0 hours 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneDay * 11, false, false);
		assertEquals("11 days 0 hours 0 minutes 0 seconds", text);
		text = DurationFormats.formatDurationWords(oneDay + oneHour + oneMinute + oneSecond, false, false);
		assertEquals("1 day 1 hour 1 minute 1 second", text);
	}

	@Test
	public void testFormatDurationHMS() {
		long time = 0;
		assertEquals("00:00:00.000", DurationFormats.formatDurationHMS(time));

		time = 1;
		assertEquals("00:00:00.001", DurationFormats.formatDurationHMS(time));

		time = 15;
		assertEquals("00:00:00.015", DurationFormats.formatDurationHMS(time));

		time = 165;
		assertEquals("00:00:00.165", DurationFormats.formatDurationHMS(time));

		time = 1675;
		assertEquals("00:00:01.675", DurationFormats.formatDurationHMS(time));

		time = 13465;
		assertEquals("00:00:13.465", DurationFormats.formatDurationHMS(time));

		time = 72789;
		assertEquals("00:01:12.789", DurationFormats.formatDurationHMS(time));

		time = 12789 + 32 * 60000;
		assertEquals("00:32:12.789", DurationFormats.formatDurationHMS(time));

		time = 12789 + 62 * 60000;
		assertEquals("01:02:12.789", DurationFormats.formatDurationHMS(time));
	}

	@Test
	public void testFormatDurationISO() {
		assertEquals("P0Y0M0DT0H0M0.000S", DurationFormats.formatDurationISO(0L));
		assertEquals("P0Y0M0DT0H0M0.001S", DurationFormats.formatDurationISO(1L));
		assertEquals("P0Y0M0DT0H0M0.010S", DurationFormats.formatDurationISO(10L));
		assertEquals("P0Y0M0DT0H0M0.100S", DurationFormats.formatDurationISO(100L));
		assertEquals("P0Y0M0DT0H1M15.321S", DurationFormats.formatDurationISO(75321L));
	}

	@Test
	public void testFormatDuration() {
		long duration = 0;
		assertEquals("0", DurationFormats.formatDuration(duration, "y"));
		assertEquals("0", DurationFormats.formatDuration(duration, "M"));
		assertEquals("0", DurationFormats.formatDuration(duration, "d"));
		assertEquals("0", DurationFormats.formatDuration(duration, "H"));
		assertEquals("0", DurationFormats.formatDuration(duration, "m"));
		assertEquals("0", DurationFormats.formatDuration(duration, "s"));
		assertEquals("0", DurationFormats.formatDuration(duration, "S"));
		assertEquals("0000", DurationFormats.formatDuration(duration, "SSSS"));
		assertEquals("0000", DurationFormats.formatDuration(duration, "yyyy"));
		assertEquals("0000", DurationFormats.formatDuration(duration, "yyMM"));

		duration = 60 * 1000;
		assertEquals("0", DurationFormats.formatDuration(duration, "y"));
		assertEquals("0", DurationFormats.formatDuration(duration, "M"));
		assertEquals("0", DurationFormats.formatDuration(duration, "d"));
		assertEquals("0", DurationFormats.formatDuration(duration, "H"));
		assertEquals("1", DurationFormats.formatDuration(duration, "m"));
		assertEquals("60", DurationFormats.formatDuration(duration, "s"));
		assertEquals("60000", DurationFormats.formatDuration(duration, "S"));
		assertEquals("01:00", DurationFormats.formatDuration(duration, "mm:ss"));

		final Calendar base = Calendar.getInstance();
		base.set(2000, 0, 1, 0, 0, 0);
		base.set(Calendar.MILLISECOND, 0);

		final Calendar cal = Calendar.getInstance();
		cal.set(2003, 1, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		duration = cal.getTime().getTime() - base.getTime().getTime(); // duration from 2000-01-01
																		// to cal
		// don't use 1970 in test as time zones were less reliable in 1970 than now
		// remember that duration formatting ignores time zones, working on strict hour lengths
		final int days = 366 + 365 + 365 + 31;
		assertEquals("0 0 " + days, DurationFormats.formatDuration(duration, "y M d"));
	}

	@Test
	public void testFormatPeriodISO() {
		final TimeZone timeZone = TimeZone.getTimeZone("GMT-3");
		final Calendar base = Calendar.getInstance(timeZone);
		base.set(1970, 0, 1, 0, 0, 0);
		base.set(Calendar.MILLISECOND, 0);

		final Calendar cal = Calendar.getInstance(timeZone);
		cal.set(2002, 1, 23, 9, 11, 12);
		cal.set(Calendar.MILLISECOND, 1);
		String text;
		// repeat a test from testDateTimeISO to compare extended and not extended.
		// test fixture is the same as above, but now with extended format.
		text = DurationFormats.formatPeriod(base.getTime().getTime(), cal.getTime().getTime(),
			DurationFormats.ISO_EXTENDED_FORMAT_PATTERN, false, timeZone);
		assertEquals("P32Y1M22DT9H11M12.001S", text);
		// test fixture from example in http://www.w3.org/TR/xmlschema-2/#duration
		cal.set(1971, 1, 3, 10, 30, 0);
		cal.set(Calendar.MILLISECOND, 0);
		text = DurationFormats.formatPeriod(base.getTime().getTime(), cal.getTime().getTime(),
			DurationFormats.ISO_EXTENDED_FORMAT_PATTERN, false, timeZone);
		assertEquals("P1Y1M2DT10H30M0.000S", text);
		// want a way to say 'don't print the seconds in format()' or other fields for that matter:
		// assertEquals("P1Y2M3DT10H30M", text);
	}

	@Test
	public void testFormatPeriod() {
		final Calendar cal1970 = Calendar.getInstance();
		cal1970.set(1970, 0, 1, 0, 0, 0);
		cal1970.set(Calendar.MILLISECOND, 0);
		final long time1970 = cal1970.getTime().getTime();

		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "y"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "M"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "d"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "H"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "m"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "s"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time1970, "S"));
		assertEquals("0000", DurationFormats.formatPeriod(time1970, time1970, "SSSS"));
		assertEquals("0000", DurationFormats.formatPeriod(time1970, time1970, "yyyy"));
		assertEquals("0000", DurationFormats.formatPeriod(time1970, time1970, "yyMM"));

		long time = time1970 + 60 * 1000;
		assertEquals("0", DurationFormats.formatPeriod(time1970, time, "y"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time, "M"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time, "d"));
		assertEquals("0", DurationFormats.formatPeriod(time1970, time, "H"));
		assertEquals("1", DurationFormats.formatPeriod(time1970, time, "m"));
		assertEquals("60", DurationFormats.formatPeriod(time1970, time, "s"));
		assertEquals("60000", DurationFormats.formatPeriod(time1970, time, "S"));
		assertEquals("01:00", DurationFormats.formatPeriod(time1970, time, "mm:ss"));

		final Calendar cal = Calendar.getInstance();
		cal.set(1973, 6, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		time = cal.getTime().getTime();
		assertEquals("36", DurationFormats.formatPeriod(time1970, time, "yM"));
		assertEquals("3 years 6 months", DurationFormats.formatPeriod(time1970, time, "y' years 'M' months'"));
		assertEquals("03/06", DurationFormats.formatPeriod(time1970, time, "yy/MM"));

		cal.set(1973, 10, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		time = cal.getTime().getTime();
		assertEquals("310", DurationFormats.formatPeriod(time1970, time, "yM"));
		assertEquals("3 years 10 months", DurationFormats.formatPeriod(time1970, time, "y' years 'M' months'"));
		assertEquals("03/10", DurationFormats.formatPeriod(time1970, time, "yy/MM"));

		cal.set(1974, 0, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		time = cal.getTime().getTime();
		assertEquals("40", DurationFormats.formatPeriod(time1970, time, "yM"));
		assertEquals("4 years 0 months", DurationFormats.formatPeriod(time1970, time, "y' years 'M' months'"));
		assertEquals("04/00", DurationFormats.formatPeriod(time1970, time, "yy/MM"));
		assertEquals("48", DurationFormats.formatPeriod(time1970, time, "M"));
		assertEquals("48", DurationFormats.formatPeriod(time1970, time, "MM"));
		assertEquals("048", DurationFormats.formatPeriod(time1970, time, "MMM"));
	}

	@Test
	public void testLexx() {
		// tests each constant
		assertArrayEquals(new DurationFormats.Token[] { new DurationFormats.Token(DurationFormats.y, 1),
				new DurationFormats.Token(DurationFormats.M, 1),
				new DurationFormats.Token(DurationFormats.d, 1),
				new DurationFormats.Token(DurationFormats.H, 1),
				new DurationFormats.Token(DurationFormats.m, 1),
				new DurationFormats.Token(DurationFormats.s, 1),
				new DurationFormats.Token(DurationFormats.S, 1) }, DurationFormats.lexx("yMdHmsS"));

		// tests the ISO8601-like
		assertArrayEquals(new DurationFormats.Token[] { new DurationFormats.Token(DurationFormats.H, 1),
				new DurationFormats.Token(new StringBuilder(":"), 1),
				new DurationFormats.Token(DurationFormats.m, 2),
				new DurationFormats.Token(new StringBuilder(":"), 1),
				new DurationFormats.Token(DurationFormats.s, 2),
				new DurationFormats.Token(new StringBuilder("."), 1),
				new DurationFormats.Token(DurationFormats.S, 3) }, DurationFormats.lexx("H:mm:ss.SSS"));

		// test the iso extended format
		assertArrayEquals(new DurationFormats.Token[] { new DurationFormats.Token(new StringBuilder("P"), 1),
				new DurationFormats.Token(DurationFormats.y, 4),
				new DurationFormats.Token(new StringBuilder("Y"), 1),
				new DurationFormats.Token(DurationFormats.M, 1),
				new DurationFormats.Token(new StringBuilder("M"), 1),
				new DurationFormats.Token(DurationFormats.d, 1),
				new DurationFormats.Token(new StringBuilder("DT"), 1),
				new DurationFormats.Token(DurationFormats.H, 1),
				new DurationFormats.Token(new StringBuilder("H"), 1),
				new DurationFormats.Token(DurationFormats.m, 1),
				new DurationFormats.Token(new StringBuilder("M"), 1),
				new DurationFormats.Token(DurationFormats.s, 1),
				new DurationFormats.Token(new StringBuilder("."), 1),
				new DurationFormats.Token(DurationFormats.S, 1),
				new DurationFormats.Token(new StringBuilder("S"), 1) },
			DurationFormats.lexx(DurationFormats.ISO_EXTENDED_FORMAT_PATTERN));

		// test failures in equals
		final DurationFormats.Token token = new DurationFormats.Token(DurationFormats.y, 4);
		assertFalse("Token equal to non-Token class. ", token.equals(new Object()));
		assertFalse("Token equal to Token with wrong value class. ",
			token.equals(new DurationFormats.Token(new Object())));
		assertFalse("Token equal to Token with different count. ",
			token.equals(new DurationFormats.Token(DurationFormats.y, 1)));
		final DurationFormats.Token numToken = new DurationFormats.Token(Integer.valueOf(1), 4);
		assertTrue("Token with Number value not equal to itself. ", numToken.equals(numToken));
	}

	// http://issues.apache.org/bugzilla/show_bug.cgi?id=38401
	@Test
	public void testBugzilla38401() {
		assertEqualDuration("0000/00/30 16:00:00 000", new int[] { 2006, 0, 26, 18, 47, 34 }, new int[] { 2006, 1, 26,
				10, 47, 34 }, "yyyy/MM/dd HH:mm:ss SSS");
	}

	// https://issues.apache.org/jira/browse/LANG-281
	@Test
	public void testJiraLang281() {
		assertEqualDuration("09", new int[] { 2005, 11, 31, 0, 0, 0 }, new int[] { 2006, 9, 6, 0, 0, 0 }, "MM");
	}

	@Test
	public void testLANG815() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 6, 30, 0, 0, 0);
		final long startMillis = calendar.getTimeInMillis();

		calendar.set(2012, 8, 8);
		final long endMillis = calendar.getTimeInMillis();

		assertEquals("1 9", DurationFormats.formatPeriod(startMillis, endMillis, "M d"));
	}

	// Testing the under a day range in DurationFormatUtils.formatPeriod
	@Test
	public void testLowDurations() {
		for (int hr = 0; hr < 24; hr++) {
			for (int min = 0; min < 60; min++) {
				for (int sec = 0; sec < 60; sec++) {
					assertEqualDuration(hr + ":" + min + ":" + sec, new int[] { 2000, 0, 1, 0, 0, 0, 0 }, new int[] {
							2000, 0, 1, hr, min, sec }, "H:m:s");
				}
			}
		}
	}

	// Attempting to test edge cases in DurationFormatUtils.formatPeriod
	@Test
	public void testEdgeDurations() {
		assertEqualDuration("01", new int[] { 2006, 0, 15, 0, 0, 0 }, new int[] { 2006, 2, 10, 0, 0, 0 }, "MM");
		assertEqualDuration("12", new int[] { 2005, 0, 15, 0, 0, 0 }, new int[] { 2006, 0, 15, 0, 0, 0 }, "MM");
		assertEqualDuration("12", new int[] { 2005, 0, 15, 0, 0, 0 }, new int[] { 2006, 0, 16, 0, 0, 0 }, "MM");
		assertEqualDuration("11", new int[] { 2005, 0, 15, 0, 0, 0 }, new int[] { 2006, 0, 14, 0, 0, 0 }, "MM");

		assertEqualDuration("01 26", new int[] { 2006, 0, 15, 0, 0, 0 }, new int[] { 2006, 2, 10, 0, 0, 0 }, "MM dd");
		assertEqualDuration("54", new int[] { 2006, 0, 15, 0, 0, 0 }, new int[] { 2006, 2, 10, 0, 0, 0 }, "dd");

		assertEqualDuration("09 12", new int[] { 2006, 1, 20, 0, 0, 0 }, new int[] { 2006, 11, 4, 0, 0, 0 }, "MM dd");
		assertEqualDuration("287", new int[] { 2006, 1, 20, 0, 0, 0 }, new int[] { 2006, 11, 4, 0, 0, 0 }, "dd");

		assertEqualDuration("11 30", new int[] { 2006, 0, 2, 0, 0, 0 }, new int[] { 2007, 0, 1, 0, 0, 0 }, "MM dd");
		assertEqualDuration("364", new int[] { 2006, 0, 2, 0, 0, 0 }, new int[] { 2007, 0, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("12 00", new int[] { 2006, 0, 1, 0, 0, 0 }, new int[] { 2007, 0, 1, 0, 0, 0 }, "MM dd");
		assertEqualDuration("365", new int[] { 2006, 0, 1, 0, 0, 0 }, new int[] { 2007, 0, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("31", new int[] { 2006, 0, 1, 0, 0, 0 }, new int[] { 2006, 1, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("92", new int[] { 2005, 9, 1, 0, 0, 0 }, new int[] { 2006, 0, 1, 0, 0, 0 }, "dd");
		assertEqualDuration("77", new int[] { 2005, 9, 16, 0, 0, 0 }, new int[] { 2006, 0, 1, 0, 0, 0 }, "dd");

		// test month larger in start than end
		assertEqualDuration("136", new int[] { 2005, 9, 16, 0, 0, 0 }, new int[] { 2006, 2, 1, 0, 0, 0 }, "dd");
		// test when start in leap year
		assertEqualDuration("136", new int[] { 2004, 9, 16, 0, 0, 0 }, new int[] { 2005, 2, 1, 0, 0, 0 }, "dd");
		// test when end in leap year
		assertEqualDuration("137", new int[] { 2003, 9, 16, 0, 0, 0 }, new int[] { 2004, 2, 1, 0, 0, 0 }, "dd");
		// test when end in leap year but less than end of feb
		assertEqualDuration("135", new int[] { 2003, 9, 16, 0, 0, 0 }, new int[] { 2004, 1, 28, 0, 0, 0 }, "dd");

		assertEqualDuration("364", new int[] { 2007, 0, 2, 0, 0, 0 }, new int[] { 2008, 0, 1, 0, 0, 0 }, "dd");
		assertEqualDuration("729", new int[] { 2006, 0, 2, 0, 0, 0 }, new int[] { 2008, 0, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("365", new int[] { 2007, 2, 2, 0, 0, 0 }, new int[] { 2008, 2, 1, 0, 0, 0 }, "dd");
		assertEqualDuration("333", new int[] { 2007, 1, 2, 0, 0, 0 }, new int[] { 2008, 0, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("28", new int[] { 2008, 1, 2, 0, 0, 0 }, new int[] { 2008, 2, 1, 0, 0, 0 }, "dd");
		assertEqualDuration("393", new int[] { 2007, 1, 2, 0, 0, 0 }, new int[] { 2008, 2, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("369", new int[] { 2004, 0, 29, 0, 0, 0 }, new int[] { 2005, 1, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("338", new int[] { 2004, 1, 29, 0, 0, 0 }, new int[] { 2005, 1, 1, 0, 0, 0 }, "dd");

		assertEqualDuration("28", new int[] { 2004, 2, 8, 0, 0, 0 }, new int[] { 2004, 3, 5, 0, 0, 0 }, "dd");

		assertEqualDuration("48", new int[] { 1992, 1, 29, 0, 0, 0 }, new int[] { 1996, 1, 29, 0, 0, 0 }, "M");

		// this seems odd - and will fail if I throw it in as a brute force
		// below as it expects the answer to be 12. It's a tricky edge case
		assertEqualDuration("11", new int[] { 1996, 1, 29, 0, 0, 0 }, new int[] { 1997, 1, 28, 0, 0, 0 }, "M");
		// again - this seems odd
		assertEqualDuration("11 28", new int[] { 1996, 1, 29, 0, 0, 0 }, new int[] { 1997, 1, 28, 0, 0, 0 }, "M d");

	}

	@Test
	public void testDurationsByBruteForce() {
		bruteForce(2006, 0, 1, "d", Calendar.DAY_OF_MONTH);
		bruteForce(2006, 0, 2, "d", Calendar.DAY_OF_MONTH);
		bruteForce(2007, 1, 2, "d", Calendar.DAY_OF_MONTH);
		bruteForce(2004, 1, 29, "d", Calendar.DAY_OF_MONTH);
		bruteForce(1996, 1, 29, "d", Calendar.DAY_OF_MONTH);

		bruteForce(1969, 1, 28, "M", Calendar.MONTH); // tests for 48 years
		// bruteForce(1996, 1, 29, "M", Calendar.MONTH); // this will fail
	}

	private static final int FOUR_YEARS = 365 * 3 + 366;

	// Takes a minute to run, so generally turned off
	// public void testBrutally() {
	// Calendar c = Calendar.getInstance();
	// c.set(2004, 0, 1, 0, 0, 0);
	// for (int i=0; i < FOUR_YEARS; i++) {
	// bruteForce(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), "d",
	// Calendar.DAY_OF_MONTH );
	// c.add(Calendar.DAY_OF_MONTH, 1);
	// }
	// }

	private void bruteForce(final int year, final int month, final int day, final String format, final int calendarType) {
		final String msg = year + "-" + month + "-" + day + " to ";
		final Calendar c = Calendar.getInstance();
		c.set(year, month, day, 0, 0, 0);
		final int[] array1 = new int[] { year, month, day, 0, 0, 0 };
		final int[] array2 = new int[] { year, month, day, 0, 0, 0 };
		for (int i = 0; i < FOUR_YEARS; i++) {
			array2[0] = c.get(Calendar.YEAR);
			array2[1] = c.get(Calendar.MONTH);
			array2[2] = c.get(Calendar.DAY_OF_MONTH);
			final String tmpMsg = msg + array2[0] + "-" + array2[1] + "-" + array2[2] + " at ";
			assertEqualDuration(tmpMsg + i, Integer.toString(i), array1, array2, format);
			c.add(calendarType, 1);
		}
	}

	private void assertEqualDuration(final String expected, final int[] start, final int[] end, final String format) {
		assertEqualDuration(null, expected, start, end, format);
	}

	private void assertEqualDuration(final String message, final String expected, final int[] start, final int[] end,
			final String format) {
		final Calendar cal1 = Calendar.getInstance();
		cal1.set(start[0], start[1], start[2], start[3], start[4], start[5]);
		cal1.set(Calendar.MILLISECOND, 0);
		final Calendar cal2 = Calendar.getInstance();
		cal2.set(end[0], end[1], end[2], end[3], end[4], end[5]);
		cal2.set(Calendar.MILLISECOND, 0);
		final long milli1 = cal1.getTime().getTime();
		final long milli2 = cal2.getTime().getTime();
		final String result = DurationFormats.formatPeriod(milli1, milli2, format);
		if (message == null) {
			assertEquals(expected, result);
		}
		else {
			assertEquals(message, expected, result);
		}
	}

	private void assertArrayEquals(final DurationFormats.Token[] obj1, final DurationFormats.Token[] obj2) {
		assertEquals("Arrays are unequal length. ", obj1.length, obj2.length);
		for (int i = 0; i < obj1.length; i++) {
			assertTrue("Index " + i + " not equal, " + obj1[i] + " vs " + obj2[i], obj1[i].equals(obj2[i]));
		}
	}

}

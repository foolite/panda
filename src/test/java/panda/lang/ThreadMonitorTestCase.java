package panda.lang;

import junit.framework.TestCase;

/**
 * Tests for {@link ThreadMonitor}.
 */
public class ThreadMonitorTestCase extends TestCase {

	public ThreadMonitorTestCase(final String name) {
		super(name);
	}

	/**
	 * Test timeout.
	 */
	public void testTimeout() {
		try {
			final Thread monitor = ThreadMonitor.start(100);
			Thread.sleep(200);
			ThreadMonitor.stop(monitor);
			fail("Expected InterruptedException");
		}
		catch (final InterruptedException e) {
			// expected result - timout
		}
	}

	/**
	 * Test task completed before timeout.
	 */
	public void testCompletedWithoutTimeout() {
		try {
			final Thread monitor = ThreadMonitor.start(200);
			Thread.sleep(100);
			ThreadMonitor.stop(monitor);
		}
		catch (final InterruptedException e) {
			fail("Timed Out");
		}
	}

	/**
	 * Test No timeout.
	 */
	public void testNoTimeout() {

		// timeout = -1
		try {
			final Thread monitor = ThreadMonitor.start(-1);
			assertNull("Timeout -1, Monitor should be null", monitor);
			Thread.sleep(100);
			ThreadMonitor.stop(monitor);
		}
		catch (final Exception e) {
			fail("Timeout -1, threw " + e);
		}

		// timeout = 0
		try {
			final Thread monitor = ThreadMonitor.start(0);
			assertNull("Timeout 0, Monitor should be null", monitor);
			Thread.sleep(100);
			ThreadMonitor.stop(monitor);
		}
		catch (final Exception e) {
			fail("Timeout 0, threw " + e);
		}
	}
}

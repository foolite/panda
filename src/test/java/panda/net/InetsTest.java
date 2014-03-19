package panda.net;

import java.net.InetAddress;

import org.junit.Assert;

import junit.framework.TestCase;

/**
 */
public class InetsTest extends TestCase {

	/**
	 */
	public void testLocalhost() throws Exception {
		InetAddress localhost = InetAddress.getLocalHost();
		System.out.println(localhost);
	}

	/**
	 */
	public void testGetAllByName() throws Exception {
		InetAddress[] ias = InetAddress.getAllByName("www.google.com");
		for (InetAddress ia : ias) {
			System.out.println(ia);
		}
	}


	/**
	 */
	public void testIsAnyLocalHost() throws Exception {
		// Class A: 10.0.0.0 ~ 10.255.255.255 （10.0.0.0/8）
		// Class B: 172.16.0.0 ~ 172.31.255.255 （172.16.0.0/12）
		// Class C: 192.168.0.0 ~ 192.168.255.255 （192.168.0.0/16）
		Assert.assertTrue("10.0.0.0", Inets.isIntranetHost("10.0.0.0"));
		Assert.assertTrue("10.0.0.1", Inets.isIntranetHost("10.0.0.1"));
		Assert.assertTrue("172.16.0.0", Inets.isIntranetHost("172.16.0.0"));
		Assert.assertTrue("172.16.0.1", Inets.isIntranetHost("172.16.0.1"));
		Assert.assertTrue("192.168.0.0", Inets.isIntranetHost("192.168.0.0"));
		Assert.assertTrue("192.168.0.1", Inets.isIntranetHost("192.168.0.1"));
		Assert.assertTrue("127.0.0.0", Inets.isIntranetHost("127.0.0.0"));
		Assert.assertTrue("127.0.0.1", Inets.isIntranetHost("127.0.0.1"));
		Assert.assertTrue("0:0:0:0:0:0:0:0", Inets.isIntranetHost("0:0:0:0:0:0:0:0"));
		Assert.assertTrue("0:0:0:0:0:0:0:1", Inets.isIntranetHost("0:0:0:0:0:0:0:1"));
	}
}

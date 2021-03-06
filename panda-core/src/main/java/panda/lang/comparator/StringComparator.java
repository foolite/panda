package panda.lang.comparator;

import java.util.Comparator;

import panda.lang.Objects;


/**
 * string comparator for string
 */
public class StringComparator implements Comparator<String> {
	private final static StringComparator i = new StringComparator();
	
	public final static StringComparator i() {
		return i;
	}
	
	private StringComparator() {
	}

	/**
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first argument is less than,
	 *         equal to, or greater than the second.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(String o1, String o2) {
		return Objects.compare(o1, o2);
	}
}

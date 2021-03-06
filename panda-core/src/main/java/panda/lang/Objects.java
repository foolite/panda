package panda.lang;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import panda.lang.builder.CompareToBuilder;
import panda.lang.builder.EqualsBuilder;
import panda.lang.builder.ToStringBuilder;
import panda.lang.mutable.MutableInt;

/**
 * utility class for Object. 
 */
public abstract class Objects {
	/**
	 * <p>
	 * Singleton used as a {@code null} placeholder where {@code null} has another meaning.
	 * </p>
	 * <p>
	 * For example, in a {@code HashMap} the {@link java.util.HashMap#get(java.lang.Object)} method
	 * returns {@code null} if the {@code Map} contains {@code null} or if there is no matching key.
	 * The {@code Null} placeholder can be used to distinguish between these two cases.
	 * </p>
	 * <p>
	 * Another example is {@code Hashtable}, where {@code null} cannot be stored.
	 * </p>
	 * <p>
	 * This instance is Serializable.
	 * </p>
	 */
	public static final Null NULL = new Null();

	// Defaulting
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Returns a default value if the object passed is {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * defaultIfNull(null, null)      = null
	 * defaultIfNull(null, "")        = ""
	 * defaultIfNull(null, "zz")      = "zz"
	 * defaultIfNull("abc", *)        = "abc"
	 * defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
	 * </pre>
	 * 
	 * @param <T> the type of the object
	 * @param object the {@code Object} to test, may be {@code null}
	 * @param defaultValue the default value to return, may be {@code null}
	 * @return {@code object} if it is not {@code null}, defaultValue otherwise
	 */
	public static <T> T defaultIfNull(final T object, final T defaultValue) {
		return object != null ? object : defaultValue;
	}

	/**
	 * <p>
	 * Returns the first value in the array which is not {@code null}. If all the values are
	 * {@code null} or the array is {@code null} or empty then {@code null} is returned.
	 * </p>
	 * 
	 * <pre>
	 * firstNonNull(null, null)      = null
	 * firstNonNull(null, "")        = ""
	 * firstNonNull(null, null, "")  = ""
	 * firstNonNull(null, "zz")      = "zz"
	 * firstNonNull("abc", *)        = "abc"
	 * firstNonNull(null, "xyz", *)  = "xyz"
	 * firstNonNull(Boolean.TRUE, *) = Boolean.TRUE
	 * firstNonNull()                = null
	 * </pre>
	 * 
	 * @param <T> the component type of the array
	 * @param values the values to test, may be {@code null} or empty
	 * @return the first value from {@code values} which is not {@code null}, or {@code null} if
	 *         there are no non-null values
	 */
	@SafeVarargs
	public static <T> T firstNonNull(final T... values) {
		if (values != null) {
			for (final T val : values) {
				if (val != null) {
					return val;
				}
			}
		}
		return null;
	}

	// Null-safe equals/hashCode
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Compares two objects for equality, where either one or both objects may be {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * equals(null, null)                  = true
	 * equals(null, "")                    = false
	 * equals("", null)                    = false
	 * equals("", "")                      = true
	 * equals(Boolean.TRUE, null)          = false
	 * equals(Boolean.TRUE, "true")        = false
	 * equals(Boolean.TRUE, Boolean.TRUE)  = true
	 * equals(Boolean.TRUE, Boolean.FALSE) = false
	 * </pre>
	 * 
	 * @param object1 the first object, may be {@code null}
	 * @param object2 the second object, may be {@code null}
	 * @return {@code true} if the values of both objects are the same
	 */
	public static boolean equals(final Object object1, final Object object2) {
		if (object1 == object2) {
			return true;
		}
		if (object1 == null || object2 == null) {
			return false;
		}
		return object1.equals(object2);
	}

	/**
	 * <p>
	 * Compares two objects for inequality, where either one or both objects may be {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * notEqual(null, null)                  = false
	 * notEqual(null, "")                    = true
	 * notEqual("", null)                    = true
	 * notEqual("", "")                      = false
	 * notEqual(Boolean.TRUE, null)          = true
	 * notEqual(Boolean.TRUE, "true")        = true
	 * notEqual(Boolean.TRUE, Boolean.TRUE)  = false
	 * notEqual(Boolean.TRUE, Boolean.FALSE) = true
	 * </pre>
	 * 
	 * @param object1 the first object, may be {@code null}
	 * @param object2 the second object, may be {@code null}
	 * @return {@code false} if the values of both objects are the same
	 */
	public static boolean notEqual(final Object object1, final Object object2) {
		return Objects.equals(object1, object2) == false;
	}

	/**
	 * <p>
	 * Gets the hash code of an object returning zero when the object is {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * hashCode(null)   = 0
	 * hashCode(obj)    = obj.hashCode()
	 * </pre>
	 * 
	 * @param obj the object to obtain the hash code of, may be {@code null}
	 * @return the hash code of the object, or zero if null
	 */
	public static int hashCode(final Object obj) {
		// hashCode(Object) retained for performance, as hash code is often critical
		return obj == null ? 0 : obj.hashCode();
	}

	/**
	 * <p>
	 * Gets the hash code for multiple objects.
	 * </p>
	 * <p>
	 * This allows a hash code to be rapidly calculated for a number of objects. The hash code for a
	 * single object is the <em>not</em> same as {@link #hashCode(Object)}. The hash code for
	 * multiple objects is the same as that calculated by an {@code ArrayList} containing the
	 * specified objects.
	 * </p>
	 * 
	 * <pre>
	 * hash((Object[]) null)  = 0
	 * hash(new Object[0])    = 1
	 * hash(a)                = 31 + a.hashCode()
	 * hash(a,b)              = (31 + a.hashCode()) * 31 + b.hashCode()
	 * hash(a,b,c)            = ((31 + a.hashCode()) * 31 + b.hashCode()) * 31 + c.hashCode()
	 * </pre>
	 * 
	 * @param a the objects to obtain the hash code of, may be {@code null}
	 * @return the hash code of the objects, or zero if null
	 */
	public static int hash(final Object... a) {
		if (a == null) {
			return 0;
		}
		
		int hash = 1;
		for (final Object object : a) {
			hash = hash * 31 + hashCode(object);
		}
		return hash;
	}

	// Identity ToString
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the toString that would be produced by {@code Object} if a class did not override
	 * toString itself. {@code null} will return {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * identityToString(null)         = null
	 * identityToString("")           = "java.lang.String@1e23"
	 * identityToString(Boolean.TRUE) = "java.lang.Boolean@7fa"
	 * </pre>
	 * 
	 * @param object the object to create a toString for, may be {@code null}
	 * @return the default toString text, or {@code null} if {@code null} passed in
	 */
	public static String identityToString(final Object object) {
		if (object == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		identityToString(buffer, object);
		return buffer.toString();
	}

	/**
	 * <p>
	 * Appends the toString that would be produced by {@code Object} if a class did not override
	 * toString itself. {@code null} will throw a NullPointerException for either of the two
	 * parameters.
	 * </p>
	 * 
	 * <pre>
	 * identityToString(buf, "")            = buf.append("java.lang.String@1e23"
	 * identityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa"
	 * identityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
	 * </pre>
	 * 
	 * @param buffer the buffer to append to
	 * @param object the object to create a toString for
	 */
	public static void identityToString(StringBuilder buffer, Object object) {
		if (object == null) {
			throw new NullPointerException("Cannot get the toString of a null identity");
		}
		buffer.append(object.getClass().getName()).append('@')
			.append(Integer.toHexString(System.identityHashCode(object)));
	}

	// ToString
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Gets the {@code toString} of an {@code Object} returning an empty string ("") if {@code null}
	 * input.
	 * </p>
	 * 
	 * <pre>
	 * toString(null)         = ""
	 * toString("")           = ""
	 * toString("bat")        = "bat"
	 * toString(Boolean.TRUE) = "true"
	 * </pre>
	 * 
	 * @see Strings#defaultString(String)
	 * @see String#valueOf(Object)
	 * @param obj the Object to {@code toString}, may be null
	 * @return the passed in Object's toString, or {@code ""} if {@code null} input
	 */
	public static String toString(final Object obj) {
		return toString(obj, Strings.EMPTY);
	}

	/**
	 * <p>
	 * Gets the {@code toString} of an {@code Object} returning a specified text if {@code null}
	 * input.
	 * </p>
	 * 
	 * <pre>
	 * toString(null, null)           = null
	 * toString(null, "null")         = "null"
	 * toString("", "null")           = ""
	 * toString("bat", "null")        = "bat"
	 * toString(Boolean.TRUE, "null") = "true"
	 * </pre>
	 * 
	 * @see Strings#defaultString(String,String)
	 * @see String#valueOf(Object)
	 * @param obj the Object to {@code toString}, may be null
	 * @param nullStr the String to return if {@code null} input, may be null
	 * @return the passed in Object's toString, or {@code nullStr} if {@code null} input
	 */
	public static String toString(final Object obj, final String nullStr) {
		if (obj == null) {
			return nullStr;
		}
		if (obj instanceof CharSequence) {
			return obj.toString();
		}
		if (obj.getClass().isArray()) {
			return Arrays.toString(obj);
		}
		String str = obj.toString();
		return Strings.defaultString(str, nullStr);
	}

	// Comparable
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Null safe comparison of Comparables.
	 * </p>
	 * 
	 * @param <T> type of the values processed by this method
	 * @param values the set of comparable values, may be null
	 * @return <ul>
	 *         <li>If any objects are non-null and unequal, the lesser object.
	 *         <li>If all objects are non-null and equal, the first.
	 *         <li>If any of the comparables are null, the lesser of the non-null objects.
	 *         <li>If all the comparables are null, null is returned.
	 *         </ul>
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T min(final T... values) {
		T result = null;
		if (values != null) {
			for (final T value : values) {
				if (compare(value, result, true) < 0) {
					result = value;
				}
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Null safe comparison of Comparables.
	 * </p>
	 * 
	 * @param <T> type of the values processed by this method
	 * @param values the set of comparable values, may be null
	 * @return <ul>
	 *         <li>If any objects are non-null and unequal, the greater object.
	 *         <li>If all objects are non-null and equal, the first.
	 *         <li>If any of the comparables are null, the greater of the non-null objects.
	 *         <li>If all the comparables are null, null is returned.
	 *         </ul>
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T max(final T... values) {
		T result = null;
		if (values != null) {
			for (final T value : values) {
				if (compare(value, result, false) > 0) {
					result = value;
				}
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Null safe comparison of Comparables. {@code null} is assumed to be less than a non-
	 * {@code null} value.
	 * </p>
	 * 
	 * @param <T> type of the values processed by this method
	 * @param c1 the first comparable, may be null
	 * @param c2 the second comparable, may be null
	 * @return a negative value if c1 < c2, zero if c1 = c2 and a positive value if c1 > c2
	 */
	public static <T extends Comparable<? super T>> int compare(final T c1, final T c2) {
		return compare(c1, c2, false);
	}

	/**
	 * <p>
	 * Null safe comparison of Comparables.
	 * </p>
	 * 
	 * @param <T> type of the values processed by this method
	 * @param c1 the first comparable, may be null
	 * @param c2 the second comparable, may be null
	 * @param nullGreater if true {@code null} is considered greater than a non-{@code null} value
	 *            or if false {@code null} is considered less than a Non-{@code null} value
	 * @return a negative value if c1 < c2, zero if c1 = c2 and a positive value if c1 > c2
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public static <T extends Comparable<? super T>> int compare(final T c1, final T c2, final boolean nullGreater) {
		if (c1 == c2) {
			return 0;
		}
		
		if (c1 == null) {
			return nullGreater ? 1 : -1;
		}
		
		if (c2 == null) {
			return nullGreater ? -1 : 1;
		}
		return c1.compareTo(c2);
	}

	/**
	 * Find the "best guess" middle value among comparables. If there is an even number of total
	 * values, the lower of the two middle values will be returned.
	 * 
	 * @param <T> type of values processed by this method
	 * @param items to compare
	 * @return T at middle position
	 * @throws NullPointerException if items is {@code null}
	 * @throws IllegalArgumentException if items is empty or contains {@code null} values
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T median(final T... items) {
		Asserts.notEmpty(items);
		Asserts.noNullElements(items);
		TreeSet<T> sort = new TreeSet<T>();
		Collections.addAll(sort, items);
		@SuppressWarnings("unchecked")
		// we know all items added were T instances
		final T result = (T)sort.toArray()[(sort.size() - 1) / 2];
		return result;
	}

	/**
	 * Find the "best guess" middle value among comparables. If there is an even number of total
	 * values, the lower of the two middle values will be returned.
	 * 
	 * @param <T> type of values processed by this method
	 * @param comparator to use for comparisons
	 * @param items to compare
	 * @return T at middle position
	 * @throws NullPointerException if items or comparator is {@code null}
	 * @throws IllegalArgumentException if items is empty or contains {@code null} values
	 */
	@SafeVarargs
	public static <T> T median(final Comparator<T> comparator, final T... items) {
		Asserts.notEmpty(items, "null/empty items");
		Asserts.noNullElements(items);
		Asserts.notNull(comparator, "null comparator");
		TreeSet<T> sort = new TreeSet<T>(comparator);
		Collections.addAll(sort, items);
		@SuppressWarnings("unchecked")
		// we know all items added were T instances
		T result = (T)sort.toArray()[(sort.size() - 1) / 2];
		return result;
	}

	// Mode
	// -----------------------------------------------------------------------
	/**
	 * Find the most frequently occurring item.
	 * 
	 * @param <T> type of values processed by this method
	 * @param items to check
	 * @return most populous T, {@code null} if non-unique or no items supplied
	 */
	@SafeVarargs
	public static <T> T mode(final T... items) {
		if (Arrays.isNotEmpty(items)) {
			final HashMap<T, MutableInt> occurrences = new HashMap<T, MutableInt>(items.length);
			for (final T t : items) {
				final MutableInt count = occurrences.get(t);
				if (count == null) {
					occurrences.put(t, new MutableInt(1));
				}
				else {
					count.increment();
				}
			}
			T result = null;
			int max = 0;
			for (final Map.Entry<T, MutableInt> e : occurrences.entrySet()) {
				final int cmp = e.getValue().intValue();
				if (cmp == max) {
					result = null;
				}
				else if (cmp > max) {
					max = cmp;
					result = e.getKey();
				}
			}
			return result;
		}
		return null;
	}

	// Null
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Class used as a null placeholder where {@code null} has another meaning.
	 * </p>
	 * <p>
	 * For example, in a {@code HashMap} the {@link java.util.HashMap#get(java.lang.Object)} method
	 * returns {@code null} if the {@code Map} contains {@code null} or if there is no matching key.
	 * The {@code Null} placeholder can be used to distinguish between these two cases.
	 * </p>
	 * <p>
	 * Another example is {@code Hashtable}, where {@code null} cannot be stored.
	 * </p>
	 */
	public static class Null implements Serializable {
		/**
		 * Required for serialization support. Declare serialization compatibility with Commons Lang
		 * 1.0
		 * 
		 * @see java.io.Serializable
		 */
		private static final long serialVersionUID = 7092611880189329093L;

		/**
		 * Restricted constructor - singleton.
		 */
		Null() {
			super();
		}

		/**
		 * <p>
		 * Ensure singleton.
		 * </p>
		 * 
		 * @return the singleton value
		 */
		private Object readResolve() {
			return Objects.NULL;
		}
	}

	/**
	 * is empty
	 * 
	 * @param o object
	 * @return true if object is an empty string
	 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}

		if (o instanceof CharSequence) {
			return ((CharSequence)o).length() == 0;
		}
		else if (o instanceof Collection) {
			return ((Collection<?>)o).isEmpty();
		}
		else if (o instanceof Map) {
			return ((Map<?, ?>)o).isEmpty();
		}
		else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		}
		else {
			return false;
		}
	}

	/**
	 * is not empty
	 * 
	 * @param o object
	 * @return true if object is not an empty string
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/**
	 * Determine if the given objects are equal, returning <code>true</code> if both are
	 * <code>null</code> or <code>false</code> if only one is <code>null</code>.
	 * <p>
	 * Compares arrays with <code>Arrays.equals</code>, performing an equality check based on the
	 * array elements rather than the array reference.
	 * 
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[])o1, (Object[])o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[])o1, (boolean[])o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[])o1, (byte[])o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[])o1, (char[])o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[])o1, (double[])o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[])o1, (float[])o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[])o1, (int[])o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[])o1, (long[])o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[])o1, (short[])o2);
			}
		}
		return false;
	}

	/**
	 * Return a hex String form of an object's identity hash code.
	 * 
	 * @param obj the object
	 * @return the object's identity code in hex notation
	 */
	public static String getIdentityHexString(Object obj) {
		return Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * create a ToStringBuilder instance
	 * @return ToStringBuilder instance
	 */
	public static ToStringBuilder toStringBuilder() {
		return new ToStringBuilder();
	}

	/**
	 * @return EqualsBuilder instance
	 */
	public static EqualsBuilder equalsBuilder() {
		return new EqualsBuilder();
	}
	
	/**
	 * @return CompareToBuilder instance
	 */
	public static CompareToBuilder compareToBuilder() {
		return new CompareToBuilder();
	}
}

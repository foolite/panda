package panda.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Miscellaneous {@link Constructor} related utility functions.
 * 
 * @author yf.frank.wang@gmail.com
 */
@SuppressWarnings("unchecked")
public class Constructors {
	/**
	 * Returns a {@link Constructor} for the given method signature, or <tt>null</tt> if no such
	 * <tt>Constructor</tt> can be found.
	 * 
	 * @param type the (non-<tt>null</tt>) type of {@link Object} the returned {@link Constructor}
	 *            should create
	 * @param argTypes a non-<tt>null</tt> array of types describing the parameters to the
	 *            {@link Constructor}.
	 * @return a {@link Constructor} for the given method signature, or <tt>null</tt> if no such
	 *         <tt>Constructor</tt> can be found.
	 * @see #invokeConstructor
	 */
	public static <T> Constructor<T> getConstructor(Class<T> type, Class[] argTypes) {
		if (null == type || null == argTypes) {
			throw new NullPointerException();
		}
		
		Constructor<T> ctor = null;
		try {
			ctor = type.getConstructor(argTypes);
		}
		catch (Exception e) {
			ctor = null;
		}
		if (null == ctor) {
			// no directly declared matching constructor,
			// look for something that will work
			// this should really be more careful to
			// adhere to the jls mechanism for late binding
			Constructor[] ctors = type.getConstructors();
			for (int i = 0; i < ctors.length; i++) {
				Class[] paramtypes = ctors[i].getParameterTypes();
				if (paramtypes.length == argTypes.length) {
					boolean canuse = true;
					for (int j = 0; j < paramtypes.length; j++) {
						if (paramtypes[j].isAssignableFrom(argTypes[j])) {
							continue;
						}
						else {
							canuse = false;
							break;
						}
					}
					if (canuse == true) {
						ctor = ctors[i];
						break;
					}
				}
			}
		}
		return ctor;
	}

	/**
	 * Creates a new instance of the specified <tt><i>type</i></tt> using a {@link Constructor}
	 * described by the given parameter types and values.
	 * 
	 * @param type the type of {@link Object} to be created
	 * @param argTypes a non-<tt>null</tt> array of types describing the parameters to the
	 *            {@link Constructor}.
	 * @param argValues a non-<tt>null</tt> array containing the values of the parameters to the
	 *            {@link Constructor}.
	 * @return a new instance of the specified <tt><i>type</i></tt> using a {@link Constructor}
	 *         described by the given parameter types and values.
	 * @exception InstantiationException if an error occurs
	 * @exception IllegalAccessException if an error occurs
	 * @exception InvocationTargetException if an error occurs
	 */
	public static <T> T invokeConstructor(Class<T> type, Class<?>[] argTypes, Object[] argValues)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return Constructors.getConstructor(type, argTypes).newInstance(argValues);
	}
}

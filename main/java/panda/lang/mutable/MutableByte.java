package panda.lang.mutable;

/**
 * A mutable <code>byte</code> wrapper.
 * <p>
 * Note that as MutableByte does not extend Byte, it is not treated by String.format as a Byte parameter. 
 * 
 */
public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {

	/**
	 * Required for serialization support.
	 * 
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = -1585823265L;

	/** The mutable value. */
	private byte value;

	/**
	 * Constructs a new MutableByte with the default value of zero.
	 */
	public MutableByte() {
		super();
	}

	/**
	 * Constructs a new MutableByte with the specified value.
	 * 
	 * @param value the initial value to store
	 */
	public MutableByte(byte value) {
		super();
		this.value = value;
	}

	/**
	 * Constructs a new MutableByte with the specified value.
	 * 
	 * @param value the initial value to store, not null
	 * @throws NullPointerException if the object is null
	 */
	public MutableByte(Number value) {
		super();
		this.value = value.byteValue();
	}

	/**
	 * Constructs a new MutableByte parsing the given string.
	 * 
	 * @param value the string to parse, not null
	 * @throws NumberFormatException if the string cannot be parsed into a byte
	 */
	public MutableByte(String value) throws NumberFormatException {
		super();
		this.value = Byte.parseByte(value);
	}

	// -----------------------------------------------------------------------
	/**
	 * Gets the value as a Byte instance.
	 * 
	 * @return the value as a Byte, never null
	 */
	public Byte getValue() {
		return Byte.valueOf(this.value);
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value to set
	 */
	public void setValue(byte value) {
		this.value = value;
	}

	/**
	 * Sets the value from any Number instance.
	 * 
	 * @param value the value to set, not null
	 * @throws NullPointerException if the object is null
	 */
	public void setValue(Number value) {
		this.value = value.byteValue();
	}

	// -----------------------------------------------------------------------
	/**
	 * Increments the value.
	 */
	public void increment() {
		value++;
	}

	/**
	 * Decrements the value.
	 */
	public void decrement() {
		value--;
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a value to the value of this instance.
	 * 
	 * @param operand the value to add, not null
	 */
	public void add(byte operand) {
		this.value += operand;
	}

	/**
	 * Adds a value to the value of this instance.
	 * 
	 * @param operand the value to add, not null
	 * @throws NullPointerException if the object is null
	 */
	public void add(Number operand) {
		this.value += operand.byteValue();
	}

	/**
	 * Subtracts a value from the value of this instance.
	 * 
	 * @param operand the value to subtract, not null
	 */
	public void subtract(byte operand) {
		this.value -= operand;
	}

	/**
	 * Subtracts a value from the value of this instance.
	 * 
	 * @param operand the value to subtract, not null
	 * @throws NullPointerException if the object is null
	 */
	public void subtract(Number operand) {
		this.value -= operand.byteValue();
	}

	// -----------------------------------------------------------------------
	// shortValue relies on Number implementation
	/**
	 * Returns the value of this MutableByte as a byte.
	 * 
	 * @return the numeric value represented by this object after conversion to type byte.
	 */
	@Override
	public byte byteValue() {
		return value;
	}

	/**
	 * Returns the value of this MutableByte as an int.
	 * 
	 * @return the numeric value represented by this object after conversion to type int.
	 */
	@Override
	public int intValue() {
		return value;
	}

	/**
	 * Returns the value of this MutableByte as a long.
	 * 
	 * @return the numeric value represented by this object after conversion to type long.
	 */
	@Override
	public long longValue() {
		return value;
	}

	/**
	 * Returns the value of this MutableByte as a float.
	 * 
	 * @return the numeric value represented by this object after conversion to type float.
	 */
	@Override
	public float floatValue() {
		return value;
	}

	/**
	 * Returns the value of this MutableByte as a double.
	 * 
	 * @return the numeric value represented by this object after conversion to type double.
	 */
	@Override
	public double doubleValue() {
		return value;
	}

	// -----------------------------------------------------------------------
	/**
	 * Gets this mutable as an instance of Byte.
	 * 
	 * @return a Byte instance containing the value from this mutable
	 */
	public Byte toByte() {
		return Byte.valueOf(byteValue());
	}

	// -----------------------------------------------------------------------
	/**
	 * Compares this object to the specified object. The result is <code>true</code> if and only if
	 * the argument is not <code>null</code> and is a <code>MutableByte</code> object that contains
	 * the same <code>byte</code> value as this object.
	 * 
	 * @param obj the object to compare with, null returns false
	 * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MutableByte) {
			return value == ((MutableByte)obj).byteValue();
		}
		return false;
	}

	/**
	 * Returns a suitable hash code for this mutable.
	 * 
	 * @return a suitable hash code
	 */
	@Override
	public int hashCode() {
		return value;
	}

	// -----------------------------------------------------------------------
	/**
	 * Compares this mutable to another in ascending order.
	 * 
	 * @param other the other mutable to compare to, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	public int compareTo(MutableByte other) {
		byte anotherVal = other.value;
		return value < anotherVal ? -1 : (value == anotherVal ? 0 : 1);
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns the String value of this mutable.
	 * 
	 * @return the mutable value as a string
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}

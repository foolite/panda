package panda.lang.collection.kv;

import java.util.Map;

public class DefaultKeyValue<K, V> extends AbstractKeyValue<K, V> {

    /**
     * Constructs a new pair with a null key and null value.
     */
    public DefaultKeyValue() {
        super(null, null);
    }

    /**
     * Constructs a new pair with the specified key and given value.
     *
     * @param key  the key for the entry, may be null
     * @param value  the value for the entry, may be null
     */
    public DefaultKeyValue(final K key, final V value) {
        super(key, value);
    }

    /**
     * Constructs a new pair from the specified <code>KeyValue</code>.
     *
     * @param pair  the pair to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultKeyValue(final KeyValue<? extends K, ? extends V> pair) {
        super(pair.getKey(), pair.getValue());
    }

    /**
     * Constructs a new pair from the specified <code>Map.Entry</code>.
     *
     * @param entry  the entry to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultKeyValue(final Map.Entry<? extends K, ? extends V> entry) {
        super(entry.getKey(), entry.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the key.
     *
     * @param key  the new key
     * @return the old key
     * @throws IllegalArgumentException if key is this object
     */
    @Override
    public K setKey(final K key) {
        if (key == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
        }

        return super.setKey(key);
    }

    /**
     * Sets the value.
     *
     * @return the old value of the value
     * @param value the new value
     * @throws IllegalArgumentException if value is this object
     */
    @Override
    public V setValue(final V value) {
        if (value == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
        }

        return super.setValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new <code>Map.Entry</code> object with key and value from this pair.
     *
     * @return a MapEntry instance
     */
    public Map.Entry<K, V> toMapEntry() {
        return new DefaultMapEntry<K, V>(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this <code>Map.Entry</code> with another <code>Map.Entry</code>.
     * <p>
     * Returns true if the compared object is also a <code>DefaultKeyValue</code>,
     * and its key and value are equal to this object's key and value.
     *
     * @param obj  the object to compare to
     * @return true if equal key and value
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DefaultKeyValue == false) {
            return false;
        }

        final DefaultKeyValue<?, ?> other = (DefaultKeyValue<?, ?>) obj;
        return
            (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
            (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
    }

    /**
     * Gets a hashCode compatible with the equals method.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()},
     * however subclasses may override this.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^
               (getValue() == null ? 0 : getValue().hashCode());
    }

}

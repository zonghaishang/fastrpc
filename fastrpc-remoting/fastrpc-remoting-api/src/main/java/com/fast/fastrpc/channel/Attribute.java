package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : Attribute.java, v 0.1 2020-08-03
 */
public interface Attribute<T> {

    /**
     * Returns the key of this attribute.
     */
    AttributeKey<T> key();

    /**
     * Returns the current value, which may be {@code null}
     */
    T get();

    /**
     * Sets the value
     */
    void set(T value);

    /**
     * Atomically sets to the given value and returns the old value which may be {@code null} if non was set before.
     */
    T getAndSet(T value);
}

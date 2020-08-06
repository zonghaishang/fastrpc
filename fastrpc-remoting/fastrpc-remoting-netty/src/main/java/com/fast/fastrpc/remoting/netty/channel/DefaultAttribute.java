package com.fast.fastrpc.remoting.netty.channel;

import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;

/**
 * @author yiji
 * @version : DefaultAttribute.java, v 0.1 2020-08-06
 */
public class DefaultAttribute<T> implements Attribute<T> {

    private AttributeKey<T> key;

    private io.netty.util.Attribute<T> attribute;

    public DefaultAttribute(AttributeKey<T> key, io.netty.util.Attribute<T> attribute) {
        if (key == null) throw new IllegalArgumentException("key is required.");
        if (attribute == null) throw new IllegalArgumentException("attribute is required.");
        this.key = key;
        this.attribute = attribute;
    }

    @Override
    public AttributeKey<T> key() {
        return this.key;
    }

    @Override
    public T get() {
        return this.attribute.get();
    }

    @Override
    public void set(T value) {
        if (value == null) {
            this.key.clear();
        }
        this.attribute.set(value);
    }

    @Override
    public T getAndSet(T value) {
        if (value == null) {
            this.key.clear();
        }
        return this.attribute.getAndSet(value);
    }
}

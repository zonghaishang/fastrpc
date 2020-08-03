package com.fast.fastrpc.channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : AttributeKey.java, v 0.1 2020-08-03
 */
public final class AttributeKey<T> {

    private int id;
    private String name;

    private final static ConstantPool<AttributeKey<Object>> pool = new ConstantPool();

    private AttributeKey(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static <T> AttributeKey<T> valueOf(String name) {
        return (AttributeKey<T>) pool.valueOf(name);
    }

    static class ConstantPool<T> {
        final AtomicInteger nextId = new AtomicInteger(1);
        final ConcurrentHashMap<String, AttributeKey<T>> constants = new ConcurrentHashMap<>();

        AttributeKey<T> valueOf(String name) {
            AttributeKey<T> constant = constants.get(name);
            if (constant == null) {
                final AttributeKey<T> key = new AttributeKey<>(nextId.getAndIncrement(), name);
                constant = constants.putIfAbsent(name, key);
                if (constant == null) {
                    return key;
                }
            }
            return constant;
        }
    }

}

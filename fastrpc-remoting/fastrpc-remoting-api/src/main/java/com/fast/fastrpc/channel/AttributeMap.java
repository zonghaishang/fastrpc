package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : AttributeMap.java, v 0.1 2020-08-03
 */
public interface AttributeMap {

    <T> Attribute<T> attr(AttributeKey<T> key);

    <T> boolean hasAttr(AttributeKey<T> key);
}

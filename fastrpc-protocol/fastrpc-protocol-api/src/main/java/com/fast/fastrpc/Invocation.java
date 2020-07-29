package com.fast.fastrpc;

import java.util.Map;

/**
 * @author yiji
 * @version : Invocation.java, v 0.1 2020-07-28
 */
public interface Invocation {
    /**
     * get method name.
     *
     * @return method name.
     * @serial
     */
    String getMethodName();

    /**
     * get parameter types.
     *
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     *
     * @return arguments.
     */
    Object[] getArguments();

    /**
     * get attachments.
     *
     * @return attachments.
     */
    Map<String, String> getAttachments();

    /**
     * get attachment by key.
     *
     * @return attachment value.
     */
    String getAttachment(String key);

    /**
     * get the invoker in current context.
     *
     * @return invoker.
     */
    Invoker<?> getInvoker();
}

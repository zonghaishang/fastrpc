package com.fast.fastrpc.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : PrefixThreadFactory.java, v 0.1 2020-08-05
 */
public class PrefixThreadFactory implements ThreadFactory {

    protected final AtomicInteger order = new AtomicInteger(1);

    protected final String prefix;

    protected final boolean daemon;

    protected final ThreadGroup group;

    public PrefixThreadFactory(String prefix) {
        this(prefix, true);
    }

    public PrefixThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix + "-thread-";
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + order.getAndIncrement();
        Thread ret = new Thread(group, runnable, name, 0);
        ret.setDaemon(daemon);
        return ret;
    }
}

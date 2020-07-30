package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invoker;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : AbstractExporter.java, v 0.1 2020-07-30
 */
public abstract class AbstractExporter<T> implements Exporter<T> {

    protected AtomicBoolean destroyed;

    protected Invoker<T> invoker;

    public AbstractExporter(Invoker<T> invoker) {
        if (invoker == null)
            throw new IllegalStateException("service invoker is required.");
        this.invoker = invoker;
        this.destroyed = new AtomicBoolean();
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            getInvoker().destroy();
        }
    }
}

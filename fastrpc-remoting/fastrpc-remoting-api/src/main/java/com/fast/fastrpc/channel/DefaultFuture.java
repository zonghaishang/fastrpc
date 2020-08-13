package com.fast.fastrpc.channel;

import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Timeout;
import com.fast.fastrpc.TimeoutException;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : DefaultFuture.java, v 0.1 2020-08-04
 */
public class DefaultFuture implements InvokeFuture {

    public static final Object SUCCESS = new Object();

    private static final Object guard = new Object();

    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    private final CountDownLatch takeLock = new CountDownLatch(1);

    private Channel channel;

    private int invokeId;

    private volatile Object value;

    private Request request;

    private volatile Response response;

    private volatile Throwable cause;

    private volatile List<InvokeListener> invokeCallbacks = new CopyOnWriteArrayList<>();

//    private volatile OperationListener operationListener;

    private int defaultTimeout = 3000;

    private Timeout timeout;

    private volatile Future<?> future;

    public DefaultFuture(Channel channel) {
        this.channel = channel;
    }

    public DefaultFuture(Channel channel, int invokeId) {
        this(channel, invokeId, null);
    }

    public DefaultFuture(Channel channel, Throwable cause) {
        this.channel = channel;
        this.cause = cause;
    }

    public DefaultFuture(Channel channel, int invokeId, InvokeListener invokeCallback) {
        this.channel = channel;
        this.invokeId = invokeId;
        this.invokeCallbacks.add(invokeCallback);
    }

    @Override
    public Object get() throws RemotingException {
        return get(defaultTimeout);
    }

    @Override
    public Object get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = defaultTimeout;
        }

        if (!isDone()) {
            try {
                boolean notified = this.takeLock.await(timeout, TimeUnit.MILLISECONDS);
                if (!notified) {
                    // todo 区分客户端和服务端超时
                    return new TimeoutException(this.channel, "waiting response timeout.");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return parseValue();
    }

    @Override
    public int invokeId() {
        return this.invokeId;
    }

    public void receive(Object value) {
        if (value instanceof Response) {
            this.response = (Response) value;
        } else {
            this.value = value;
        }
        this.takeLock.countDown();
        executeCallback();
    }

    @Override
    public void addListener(InvokeListener callback) {
        this.invokeCallbacks.add(callback);
        if (isDone()) {
            executeCallback();
        }
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public void cancelTimeout() {
        if (this.timeout != null && !this.timeout.isCancelled()) {
            this.timeout.cancel();
        }
    }

    @Override
    public boolean isDone() {
        return this.takeLock.getCount() <= 0 || this.value != guard;
    }

    @Override
    public boolean isSuccess() {
        return isDone() && this.cause != null;
    }

    public Channel getChannel() {
        return channel;
    }

    public Object parseValue() throws RemotingException {
        Object parsed = value;
        if (parsed != guard) return parsed;

        Response response = this.response;
        if (response == null) {
            throw new IllegalStateException("response cannot be null");
        }

        switch (response.getStatus()) {
            case Response.OK: {
                this.value = response.getPayload();
                return response.getPayload();
            }
            case Response.CLIENT_TIMEOUT:
            case Response.SERVER_TIMEOUT: {
                throw new TimeoutException(this.channel, "timeout");
            }
        }

        throw new RemotingException(this.channel, response.getError());

    }

    protected void executeCallback() {
        if (!invokeCallbacks.isEmpty()) {
            for (InvokeListener listener : invokeCallbacks) {
                listener.complete(this);
            }
        }
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }

    // internal netty future.
//    public void setFuture(Future<?> future) {
//        this.future = future;
//        if (future != null && future.isDone()) {
//            executeOperation();
//        }
//    }
//
//    public void executeOperation() {
//        Future<?> f = future;
//        if (this.operationListener != null && f != null) {
//            this.operationListener.complete(f);
//        }
//    }
//
//    public void setOperationListener(OperationListener operationListener) {
//        this.operationListener = operationListener;
//        Future<?> f = this.future;
//        if (f != null && f.isDone()) {
//            executeOperation();
//        }
//    }
}

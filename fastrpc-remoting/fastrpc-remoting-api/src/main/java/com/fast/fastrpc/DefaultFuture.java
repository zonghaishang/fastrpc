package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : DefaultFuture.java, v 0.1 2020-08-04
 */
public class DefaultFuture implements InvokeFuture {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    private final CountDownLatch takeLock = new CountDownLatch(1);

    private Channel channel;

    private int invokeId;

    private volatile Object value;

    private Request request;

    private volatile Response response;

    private volatile InvokeCallback invokeCallback;

    private int defaultTimeout = 3000;

    public DefaultFuture(Channel channel, int invokeId) {
        this(channel, invokeId, null);
    }

    public DefaultFuture(Channel channel, int invokeId, InvokeCallback invokeCallback) {
        this.channel = channel;
        this.invokeId = invokeId;
        this.invokeCallback = invokeCallback;
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

    @Override
    public void receive(Object value) {
        this.value = value;
        this.takeLock.countDown();
        executeCallback();
    }

    @Override
    public void setCallback(InvokeCallback callback) {
        this.invokeCallback = callback;
        if (isDone()) {
            executeCallback();
        }
    }

    @Override
    public InvokeCallback getCallback() {
        return this.invokeCallback;
    }

    @Override
    public boolean isDone() {
        return this.takeLock.getCount() <= 0;
    }

    public Channel getChannel() {
        return channel;
    }

    public Object parseValue() throws RemotingException {
        if (value != null) return value;

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
        if (invokeCallback != null) {
            try {
                Object value = parseValue();
                this.invokeCallback.complete(value);
            } catch (Throwable e) {
                this.invokeCallback.caught(e);
            }
        }
    }

}

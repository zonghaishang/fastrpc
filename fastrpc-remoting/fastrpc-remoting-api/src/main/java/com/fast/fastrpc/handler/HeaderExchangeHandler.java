package com.fast.fastrpc.handler;

import com.fast.fastrpc.DefaultFuture;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.ExchangeProxyHandler;
import com.fast.fastrpc.ExecutionException;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;

import java.util.Map;

/**
 * @author yiji
 * @version : HeaderExchangeHandler.java, v 0.1 2020-08-03
 */
public class HeaderExchangeHandler implements ExchangeProxyHandler {

    static final AttributeKey<Long> readTimestamp = AttributeKey.valueOf("readTimestamp");
    static final AttributeKey<Long> writeTimestamp = AttributeKey.valueOf("writeTimestamp");

    final AttributeKey<Map<Integer, DefaultFuture>> futures = AttributeKey.valueOf("futures");

    private ExchangeHandler handler;

    public HeaderExchangeHandler(ExchangeHandler handler) {
        if (handler == null) throw new IllegalArgumentException("handler is required.");
        this.handler = handler;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        updateTimestamp(channel);
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        updateTimestamp(channel);
        handler.disconnected(channel);
    }

    @Override
    public void write(Channel channel, Object message) throws RemotingException {
        updateWriteTimestamp(channel);
        handler.write(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        updateReadTimestamp(channel);
        if (message instanceof Request) {
            handleRequest(channel, (Request) message);
        } else if (message instanceof Response) {
            handleResponse(channel, (Response) message);
        } else {
            handler.received(channel, message);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        if (exception instanceof ExecutionException) {
            ExecutionException e = (ExecutionException) exception;
            if (e.getSource() != null && e.getSource() instanceof Request) {
                Request request = (Request) e.getSource();
                if (!request.isOneWay() && !request.isHeartbeat()) {
                    Response response = new Response(request.getId(), request.getVersion());
                    response.setStatus(Response.SERVER_ERROR);
                    // todo 响应错误堆栈
                    response.setError(e.getMessage());
                    channel.write(response, request.getTimeout());
                    return;
                }
            }
        }
        handler.caught(channel, exception);
    }

    protected Response handleRequest(Channel channel, Request request) throws RemotingException {

        if (request.isHeartbeat()) {
            // todo 处理心跳
        }

        if (request.isReadOnly()) {
            // todo 处理readonly下线
        }

        Response response = new Response(request.getId(), request.getVersion());
        if (request.isBroken()) {
            Object data = request.getPayload();
            String msg = (data instanceof Throwable) ? ((Throwable) data).getMessage() : data.toString();
            response.setError("Fail to decode request due to: " + msg);
            response.setStatus(Response.BAD_REQUEST);
            return response;
        }

        try {
            Object result = handler.reply(channel, request.getPayload());
            response.setStatus(Response.OK);
            response.setPayload(result);
        } catch (Throwable e) {
            response.setStatus(Response.SERVICE_ERROR);
            // todo 详细堆栈返回给客户端
            response.setError(e.getMessage());
        }

        if (!request.isOneWay()) {
            channel.write(response, request.getTimeout());
        }

        return response;
    }

    protected void handleResponse(Channel channel, Response response) throws RemotingException {
        Map<Integer, DefaultFuture> future = channel.attr(futures).get();
        DefaultFuture invokeFuture = future.remove(response.getId());
        if (invokeFuture != null && !response.isHeartbeat()) {
            invokeFuture.receive(response.getPayload());
        }
    }

    @Override
    public ExchangeHandler getHandler() {
        return this.handler;
    }

    private void updateTimestamp(Channel channel) {
        long now = System.currentTimeMillis();
        channel.attr(readTimestamp).set(now);
        channel.attr(writeTimestamp).set(now);
    }

    private void updateReadTimestamp(Channel channel) {
        channel.attr(readTimestamp).set(System.currentTimeMillis());
    }

    private void updateWriteTimestamp(Channel channel) {
        channel.attr(writeTimestamp).set(System.currentTimeMillis());
    }
}

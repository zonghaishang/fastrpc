package com.fast.fastrpc.protocol;

import com.fast.fastrpc.ChannelHandlerAdapter;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.RpcContext;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : FastProtocol.java, v 0.1 2020-08-18
 */
public class FastProtocolV1 extends AbstractFastProtocol {

    public static final String NAME = "fast_v1";

    public static final String VERSION_V1 = String.valueOf(Constants.DEFAULT_PROTOCOL_VERSION);

    private final RpcHandler handler = new RpcHandler();

    @Override
    protected ExchangeHandler getHandler() {
        return handler;
    }

    @Override
    protected <T> Destroyable doExport(Invoker<T> invoker) throws RpcException {
        openServer(invoker.getUrl());
        return super.doExport(invoker);
    }

    @Override
    protected <T> Invoker<T> doRefer(Class<T> type, URL url) throws RpcException {
        return new FastInvoker(type, url, openClient(url));
    }

    @Override
    protected String getVersion() {
        return VERSION_V1;
    }

    class RpcHandler extends ChannelHandlerAdapter {

        @Override
        public Object reply(Channel channel, Object message) throws RemotingException {
            if (message instanceof Invocation) {
                Invocation invocation = (Invocation) message;
                Invoker<?> invoker = findInvoker(channel, invocation);
                decorateIfRequired(channel);
                return invoker.invoke(invocation);
            }

            throw new RemotingException(channel, "Unsupported request: "
                    + (message == null ? null : (message.getClass().getName() + ": " + message))
                    + ", client: " + channel.remoteAddress() + " --> server: " + channel.localAddress());
        }

        private void decorateIfRequired(Channel channel) {
            RpcContext.getContext().setRemoteAddress(channel.remoteAddress());
        }
    }

    private Invoker<?> findInvoker(Channel channel, Invocation invocation) throws RemotingException {
        String service = invocation.getAttachment(Constants.SERVICE_KEY);
        Exporter<?> exporter = exporterMap.get(service);
        if (exporter == null) {
            throw new RemotingException(channel, "Not found exported service: " + service
                    + ", may be uniqueId not match , client: " + channel.remoteAddress()
                    + ", server: " + channel.localAddress()
                    + ", message:" + invocation);
        }
        return exporter.getInvoker();
    }

}

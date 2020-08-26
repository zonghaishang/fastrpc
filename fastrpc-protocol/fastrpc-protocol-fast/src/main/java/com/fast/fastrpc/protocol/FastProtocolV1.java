package com.fast.fastrpc.protocol;

import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;
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

    @Override
    protected ExchangeHandler getHandler() {
        return null;
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

}

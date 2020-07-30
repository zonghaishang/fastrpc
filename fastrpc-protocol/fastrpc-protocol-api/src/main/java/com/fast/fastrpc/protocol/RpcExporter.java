package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.common.Destroyable;

import java.util.Map;

/**
 * @author yiji
 * @version : RpcExporter.java, v 0.1 2020-07-30
 */
public class RpcExporter<T> extends AbstractExporter<T> {

    private final String key;
    private final Map<String, Exporter<?>> exporterMap;
    private Destroyable destroyable;

    public RpcExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.exporterMap = exporterMap;
        this.key = key;
    }

    @Override
    public void destroy() {
        super.destroy();
        exporterMap.remove(key);
        if (destroyable != null) {
            destroyable.destroy();
        }
    }

    public void setDestroyable(Destroyable destroyable) {
        this.destroyable = destroyable;
    }
}

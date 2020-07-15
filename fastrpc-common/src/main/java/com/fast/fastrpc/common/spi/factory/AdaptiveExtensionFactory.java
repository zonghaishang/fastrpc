package com.fast.fastrpc.common.spi.factory;

import com.fast.fastrpc.common.spi.Adaptive;
import com.fast.fastrpc.common.spi.ExtensionFactory;
import com.fast.fastrpc.common.spi.ExtensionLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiji
 * @version : AdaptiveExtensionFactory.java, v 0.1 2020-07-15
 */
@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {

    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        List<ExtensionFactory> list = new ArrayList<>();
        for (String name : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(name));
        }
        factories = list;
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        for (ExtensionFactory factory : factories) {
            T extension = factory.getExtension(type, name);
            if (extension != null) {
                return extension;
            }
        }
        return null;
    }

}
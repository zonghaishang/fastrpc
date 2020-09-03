package com.fast.fastrpc.registry;


import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : RegistryFactory.java, v 0.1 2020-09-02
 */
@SPI
public interface RegistryFactory {

    Registry getRegistry(URL url);

}

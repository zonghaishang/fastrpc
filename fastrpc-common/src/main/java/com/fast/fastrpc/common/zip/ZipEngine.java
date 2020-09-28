package com.fast.fastrpc.common.zip;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : ZipEngine.java, v 0.1 2020-09-28
 */
@SPI("none")
public interface ZipEngine {

    /**
     * Support string compression.
     *
     * @param url
     * @param buffer
     * @return
     */
    String compress(URL url, String buffer);

    /**
     * Support string decompression.
     *
     * @param url
     * @param buffer
     * @return
     */
    String unCompress(URL url, String buffer);

}

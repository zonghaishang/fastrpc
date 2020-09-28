package com.fast.fastrpc.common.zip;

import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : ZipNoneEngine.java, v 0.1 2020-09-28
 */
public class ZipNoneEngine implements ZipEngine {

    @Override
    public String compress(URL url, String buffer) {
        return buffer;
    }

    @Override
    public String unCompress(URL url, String buffer) {
        return buffer;
    }
}

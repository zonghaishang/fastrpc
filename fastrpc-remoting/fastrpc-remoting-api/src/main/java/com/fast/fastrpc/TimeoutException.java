package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

/**
 * @author yiji
 * @version : TimeoutException.java, v 0.1 2020-08-04
 */
public class TimeoutException extends RemotingException {

    public TimeoutException(String msg) {
        super(null, msg);
    }

    public TimeoutException(Channel channel, String msg) {
        super(channel, msg);
    }
}

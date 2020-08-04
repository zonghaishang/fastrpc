package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

/**
 * @author yiji
 * @version : TimeoutException.java, v 0.1 2020-08-04
 */
public class TimeoutException extends RemotingException {

    public TimeoutException(Channel channel, String msg) {
        super(channel, msg);
    }
}

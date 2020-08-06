package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.Timeout;

/**
 * @author yiji
 * @version : TimeoutTask.java, v 0.1 2020-08-06
 */
public interface TimeoutTask {

    void execute(Timeout timeout) throws Exception;

}

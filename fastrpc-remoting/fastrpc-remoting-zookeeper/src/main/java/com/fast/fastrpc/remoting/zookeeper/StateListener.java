package com.fast.fastrpc.remoting.zookeeper;

/**
 * @author yiji
 * @version : StateListener.java, v 0.1 2020-09-30
 */
public interface StateListener {

    int DISCONNECTED = 0;

    int CONNECTED = 1;

    int RECONNECTED = 2;

    void stateChanged(int state);
}

package com.fast.fastrpc;

/**
 * @author yiji
 * @version : Client.java, v 0.1 2020-08-03
 */
public interface Client extends Peer {

    void connect() throws RemotingException;

}

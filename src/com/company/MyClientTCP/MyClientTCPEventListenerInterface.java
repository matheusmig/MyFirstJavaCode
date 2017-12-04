package com.company.MyClientTCP;

/**
 * Created by mmignoni on 2017-11-20.
 */
public interface MyClientTCPEventListenerInterface {
    void onClientConnect();
    void onClientDiconnect();
    void onDataAvailable();
}

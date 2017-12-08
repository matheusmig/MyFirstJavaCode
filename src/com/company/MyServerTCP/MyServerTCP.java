package com.company.MyServerTCP;

import com.company.ClientTCPEchoMd5;

/**
 * Created by mmignoni on 2017-11-27.
 */
public class MyServerTCP implements MyAsyncServerTCP.MyAsyncServerTCPListener {
    MyAsyncServerTCP asyncServerTCP = null;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public MyServerTCP() {
        asyncServerTCP = new MyAsyncServerTCP();
        asyncServerTCP.setListener(this);
        asyncServerTCP.setServerPort(9999);

        ///////////////////////////////////////////////////
        // Seta classe personalizada para cliente
        try {
            asyncServerTCP.setClientClass(ClientTCPEchoMd5.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        asyncServerTCP.run();

    }

    /*******************************************************************************************************************
     *
     * Implementação da interface
     *
     *******************************************************************************************************************/
    public void onClientConnect(MyAsyncServerTCPClient client){
        System.out.println("New client connected: "+client.address());

    }

    public void onClientDisconnect(MyAsyncServerTCPClient client){
        System.out.println("Cliente desconectado "+client.address());
    }

}

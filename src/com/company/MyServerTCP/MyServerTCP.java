package com.company.MyServerTCP;

import com.company.ClientTCPEchoMd5;
import com.company.Main;

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
        Main.getWriter().WriteMsg("MyServerTCP.onClientConnect : New client connected : "+client.address());

    }

    public void onClientDisconnect(MyAsyncServerTCPClient client){
        Main.getWriter().WriteMsg("MyServerTCP.onClientDisconnect : Disconnected Client : "+client.address());
    }

}

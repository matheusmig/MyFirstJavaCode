package com.company.MyServerTCP;

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

        ///////////////////////////////////////////////////
        // Seta classe personalizada para cliente
      //  try {
      //      asyncServerTCP.setClientClass(TestClass.class);
      //  }catch(Exception e){
      //      e.printStackTrace();
      //  }
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

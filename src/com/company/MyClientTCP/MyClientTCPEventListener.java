package com.company.MyClientTCP;

import java.net.Socket;

/**
 * Created by mmignoni on 2017-11-20.
 */
public class MyClientTCPEventListener implements MyClientTCPEventListenerInterface {
    protected Socket clientSocket = null;        //Socket que comunica com o cliente
    protected String serverText   = null;        //"buffer" com cliente

    protected String IPAddress = "";
    protected int    IPPort    = 0;


    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public MyClientTCPEventListener(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;

        onClientConnect();
    }

    /*******************************************************************************************************************
     *
     * Eventos
     *
     *******************************************************************************************************************/
    @Override
    public void onClientConnect() {
        //Armazena valores localmente
        this.IPAddress = this.clientSocket.getLocalAddress().toString();
        this.IPPort    = this.clientSocket.getLocalPort();


        System.out.println("MyClientTCPEventListener.onClientConnect : Client "+
                this.IPAddress +":"+String.valueOf(this.IPPort)+" Connected!");
    }

    @Override
    public void onClientDiconnect(){
        System.out.println("MyClientTCPEventListener.onClientDiconnect : Client "+
                this.IPAddress +":"+String.valueOf(this.IPPort)+" Disonnected!");
    };

    @Override
    public void onDataAvailable(){

    };
}

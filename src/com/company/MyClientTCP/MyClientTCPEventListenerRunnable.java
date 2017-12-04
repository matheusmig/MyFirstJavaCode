package com.company.MyClientTCP;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *   Criação: Matheus Mignoni
 * Descrição: Classe cliente utilizada no servidor MyServerTCPEventNotifier
 *
 **/
public class MyClientTCPEventListenerRunnable extends MyClientTCPEventListener implements Runnable{

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public MyClientTCPEventListenerRunnable(Socket clientSocket, String serverText) {
        super(clientSocket, serverText);
    }

    /*******************************************************************************************************************
     *
     * Método principal da thread
     *
     *******************************************************************************************************************/
    public void run(){
        try {
            InputStream  input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();


            //DUMB TEST:
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
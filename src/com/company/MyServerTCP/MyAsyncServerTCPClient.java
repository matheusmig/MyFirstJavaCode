package com.company.MyServerTCP;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by mmignoni on 2017-11-27.
 */
public class MyAsyncServerTCPClient{

    private SocketChannel channel        = null;  //Canal do socket client
    private Selector      demultiplexer  = null;  //Seletor

    private LocalDateTime lastData       = LocalDateTime.now();  //Hora do último dado recebido do cliente

    public MyAsyncServerTCPClient(){
    }

    public long secondsSinceLastSignal(){ return lastData.until( LocalDateTime.now(), ChronoUnit.SECONDS);}

    /*******************************************************************************************************************
     *
     * Getters e Setters
     *
     *******************************************************************************************************************/

    public void setChannel(SocketChannel channel){
        this.channel = channel;
    }

    public SocketChannel getChannel(){ return this.channel; }

    public void setSelector(Selector demultiplexer){
        this.demultiplexer = demultiplexer;
    }


    /*******************************************************************************************************************
     *
     * Socket I/O
     *
     *******************************************************************************************************************/
    public void receive(String data){
        ////////////////////////////////
        // Atualiza "heartbeat"
        this.lastData = LocalDateTime.now();

        System.out.println(data);

        this.onDataAvailable(data);
    }

    public boolean send(String data){
        try {
            ByteBuffer bb = ByteBuffer.wrap(data.getBytes(Charset.forName("UTF-8"))); //Converte dados
            //////////////////////////////////////////////////////////
            // Marca que canal quer escrever
            channel.configureBlocking(false);
            channel.register(demultiplexer, SelectionKey.OP_WRITE, bb);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean send(String data, Charset charset){
        try {
            ByteBuffer bb = ByteBuffer.wrap(data.getBytes(charset)); //Converte dados
            //////////////////////////////////////////////////////////
            // Marca que canal quer escrever
            channel.configureBlocking(false);
            channel.register(demultiplexer, SelectionKey.OP_WRITE, bb);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String address(){
        if (channel != null) {
            try {
                SocketAddress addr = this.channel.getRemoteAddress();
                return addr.toString();
            } catch (IOException e) {
                return e.getMessage();
            }
        } else {
            return "error getting address";
        }
    }

    //////////////////////////////////////////////////
    // Fecha cliente
    public void close(){
        try {
            channel.shutdownInput();
            channel.shutdownOutput();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onDataAvailable(String data){
    }
}

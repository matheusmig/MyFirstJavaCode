package com.company.MyClientTCP;

import com.company.PatternReactor.ClientEvents.ClientConnectSocketEventHandler;
import com.company.PatternReactor.ClientEvents.ClientReadSocketEventHandler;
import com.company.PatternReactor.ClientEvents.ClientWriteSocketEventHandler;
import com.company.PatternReactor.Reactor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by mmignoni on 2017-11-21.
 */
public class MyAsyncClientTCP {

    SocketChannel  channel  = null;  //Canal, é o socket propriamente dito
    String         address;
    int            port;

    protected Reactor reactor     = null;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyAsyncClientTCP(String address, int port){
        this.address = address;
        this.port    = port;

        this.init();
    }

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyAsyncClientTCP(Reactor reactor, String address, int port){
        this.reactor  = reactor;
        this.address  = address;
        this.port     = port;

        this.init();
    }

    private void init(){
        try{
            this.channel = SocketChannel.open();      // Abre o ServerSocketChannel
            this.channel.configureBlocking(false);    // Obrigatoriamente deve-se configurar como non-blocking, senao nao poderia registrar no seletor
            this.channel.socket().bind(new InetSocketAddress(this.address, this.port));  // Associa o endereço e porta que iremos conectar

            if (this.reactor == null){
                this.reactor = new Reactor();
            }
            this.reactor.registerChannel(SelectionKey.OP_CONNECT, channel);

            ///////////////////////////////////////////////
            // Regitra manipuladores de evento no Reactor
            this.reactor.registerEventHandler( SelectionKey.OP_ACCEPT, new ClientConnectSocketEventHandler(this.reactor.getDemultiplexer()));
            this.reactor.registerEventHandler( SelectionKey.OP_READ,   new ClientReadSocketEventHandler(this.reactor.getDemultiplexer()));
            this.reactor.registerEventHandler( SelectionKey.OP_WRITE,  new ClientWriteSocketEventHandler(this.reactor.getDemultiplexer()));

        } catch (Exception e){
            e.printStackTrace();
        }


    }


}

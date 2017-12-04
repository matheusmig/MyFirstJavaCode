package com.company.PatternReactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by mmignoni on 2017-11-22.
 *
 *
 * Since we are accepting, we must instantiate a serverSocketChannel by calling key.channel().
 * We use this in order to get a socketChannel (which is like a socket in I/O) by calling
 *  serverSocketChannel.accept() and we register that channel to the selector to listen
 *  to a WRITE OPERATION. I do this because my server sends a hello message to each
 *  client that connects to it. This doesn't mean that I will write right NOW. It means that I
 *  told the selector that I am ready to write and that next time Selector.select() gets called
 *  it should give me a key with isWritable(). More on this in the write() method.
 *
 */
public class AcceptSocketEventHandler implements EventHandler {
    private Selector demultiplexer;

    private Reactor.ReactorEventListener listener; //Envia evento

    public AcceptSocketEventHandler(Selector demultiplexer, Reactor.ReactorEventListener listener) {
        this.demultiplexer = demultiplexer;
        this.listener      = listener;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();

        //////////////////////////////////////////////////////
        //Aceita a conexão
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (handle.isValid() && socketChannel != null) {
            ///////////////////////////////////////////////////
            // torna-a nao bloqueante
            socketChannel.configureBlocking(false);

            ///////////////////////////////////////////////////
            //registra que estamos escutando
            socketChannel.register(demultiplexer, SelectionKey.OP_READ);

            ///////////////////////////////////////////////////
            //envia evento de nova conexão
            listener.onClientConnectReactor(socketChannel);
        }
    }
}

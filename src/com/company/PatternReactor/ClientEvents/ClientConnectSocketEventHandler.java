package com.company.PatternReactor.ClientEvents;

import com.company.PatternReactor.EventHandler;

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
public class ClientConnectSocketEventHandler implements EventHandler {
    private Selector demultiplexer;

    public ClientConnectSocketEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);

            ///////////////////////////////////////////////////
            // Registra que estamos escutando
            socketChannel.register(demultiplexer, SelectionKey.OP_READ);
        }
    }
}

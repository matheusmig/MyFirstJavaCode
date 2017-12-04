package com.company.PatternReactor.ClientEvents;

import com.company.PatternReactor.EventHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by mmignoni on 2017-11-22.
 *
 * Registramos a key recebida como parâmetro no Selector. Isto significa que o canal que estamos recebendo de volta
 * do key.channel() é o mesmo canal que foi usado para registrar o Selectir no método accept().
 *
 */
public class ClientWriteSocketEventHandler implements EventHandler {
    private Selector demultiplexer;

    public ClientWriteSocketEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        try {

            /////////////////////////////////////////////////
            // Busca os dados para enviar
            ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();

            if (socketChannel.isConnected()) {
                socketChannel.write(inputBuffer);
            }
        } finally {
            ///////////////////////////////////////////////////
            //registra que estamos escutando
            socketChannel.configureBlocking(false);
            socketChannel.register(demultiplexer, SelectionKey.OP_READ);
        }

    }
}

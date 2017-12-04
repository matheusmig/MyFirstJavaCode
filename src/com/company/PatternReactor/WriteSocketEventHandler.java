package com.company.PatternReactor;

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
public class WriteSocketEventHandler implements EventHandler {
    private Selector demultiplexer;

    public WriteSocketEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        if (handle.isValid() && socketChannel.isConnected()) {
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
                socketChannel.register(demultiplexer, SelectionKey.OP_READ);
            }
        } else {
            ////////////////////////////////
            // Não está mais conectado
            handle.cancel();
        }

    }
}

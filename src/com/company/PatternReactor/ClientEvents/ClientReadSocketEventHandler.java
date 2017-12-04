package com.company.PatternReactor.ClientEvents;

import com.company.PatternReactor.EventHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by mmignoni on 2017-11-22.
 */
public class ClientReadSocketEventHandler implements EventHandler{
        private Selector   demultiplexer;

        private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);  // Aloca buffer para onde serão armazenados os dados lidos

        public ClientReadSocketEventHandler(Selector demultiplexer) {
            this.demultiplexer = demultiplexer;
        }

        public void handleEvent(SelectionKey handle) throws Exception {
            SocketChannel socketChannel = (SocketChannel) handle.channel();
            StringBuilder sb = new StringBuilder();

            //int read = socketChannel.read(inputBuffer); // Lê data do cliente
            int read = 0;
            while( (read = socketChannel.read(inputBuffer)) > 0 ) {
                inputBuffer.flip();

                byte[] buffer = new byte[inputBuffer.limit()];
                inputBuffer.get(buffer);

                sb.append(new String(buffer));
                inputBuffer.clear();
            }
            String msg = sb.toString();

            /////////////////////////////////////////////////////////
            // Verifica se leu com sucesso
            if (sb.length() == 0) {
                System.out.println("Received message from client : " + msg);

                ////////////////////////////////////////////////////////////
                // Registra o interesse para prontidao de leitura para este canal
                // em ordem de responder de volta a mensagem
                //socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
            } else if (read == -1){
                System.out.println("Nothing was there to be read, closing connection "+handle.attachment());
                socketChannel.close();
                handle.cancel();
            }

        }
}

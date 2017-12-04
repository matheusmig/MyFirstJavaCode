package com.company.PatternReactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by mmignoni on 2017-11-22.
 */
public class ReadSocketEventHandler implements EventHandler{
        private Selector   demultiplexer;

        private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);  // Aloca buffer para onde serão armazenados os dados lidos

        private Reactor.ReactorEventListener listener; //Envia evento

        /*******************************************************************************************************************
         *
         * Constante
         *
         *******************************************************************************************************************/
        final private String EOT_CONTROL_CHAR = Character.toString((char)004); //Caractere de controle - End of Transmission
        final private int MAX_PACKTES_TO_READ = 1024; //número máximo de pacotes que será lido no loop

        public ReadSocketEventHandler(Selector demultiplexer, Reactor.ReactorEventListener listener) {
            this.demultiplexer = demultiplexer;
            this.listener      = listener;
        }

        public void handleEvent(SelectionKey handle) throws Exception {


            SocketChannel socketChannel = (SocketChannel) handle.channel();
            StringBuilder sb = new StringBuilder();

            int read;
            boolean EOT = false;
            /////////////////////////////////////////////////////////////
            // Tenta ler até MAX_PACKTES_TO_READ pacotes do cliente
            try {
                while (((read = socketChannel.read(inputBuffer)) > 0) &&
                        (read <= MAX_PACKTES_TO_READ) &&
                        (!EOT)) {
                    inputBuffer.flip();

                    byte[] buffer = new byte[inputBuffer.limit()];
                    inputBuffer.get(buffer);

                    sb.append(new String(buffer));
                    inputBuffer.clear();

                    /////////////////////////////////////
                    // Verifica se recebeu caractere de final de transmissao
                    if ((sb.length() == 1) && (sb.equals(EOT_CONTROL_CHAR))){
                        EOT = true;
                    }
                }

            } catch (IOException e) {
                // The remote forcibly closed the connection, cancel
                // the selection key and close the channel.
                listener.onClientDisconnectReactor(socketChannel);
                socketChannel.close();
                handle.cancel();
                return;
            }

            /////////////////////////////////////////////////////////
            // Verifica se leu com sucesso
            if ((read == -1) || (EOT)){
                listener.onClientDisconnectReactor(socketChannel);
                socketChannel.close();
                handle.cancel();
            } else if (sb.length() > 0) {
                String msg = sb.toString();
                listener.onClientDataAvailableReactor(socketChannel, msg);

            }
        }
}

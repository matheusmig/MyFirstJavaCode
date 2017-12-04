package com.company.PatternReactor;


import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mmignoni on 2017-11-22.
 */
public class Reactor {
    private Map<Integer, EventHandler> registeredHandlers = new ConcurrentHashMap<Integer, EventHandler>();

    private Selector demultiplexer    = null; // Um Selector é um componente JAVA NIO no qual examina um ou mais canais (NIO Channel),
                                              // e determina quais canais estão prontos para, por exemplo, read e write.

    //CONSTANTES
    public final static long TIMEOUT = 1000 * 10; //10s

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public Reactor() throws Exception {
        this.demultiplexer = Selector.open(); //Cria o selector
    }

    /*******************************************************************************************************************
     *
     * Retorna Selector
     *
     *******************************************************************************************************************/
    public Selector getDemultiplexer() {
        return this.demultiplexer;
    }

    /*******************************************************************************************************************
     *
     * Registra Manipulador de eventos
     *
     *******************************************************************************************************************/
    public void registerEventHandler(int eventType, EventHandler eventHandler) {
        registeredHandlers.put(eventType, eventHandler);
    }

    /*******************************************************************************************************************
     *
     * Registra canal no Selector
     *
     *******************************************************************************************************************/
    public void registerChannel(int eventType, SelectableChannel channel) throws Exception {
        channel.register(demultiplexer, eventType);
    }

    public void run() {
        /////////////////////////////////////////////////////////////////////
        // Loop principal da Thread
        while (!Thread.currentThread().isInterrupted()){
                try {

                /////////////////////////////////////////////////////////////////
                // Espera por uma OPERATION. Aqui eh bloqueante
                demultiplexer.select(TIMEOUT);

                ////////////////////////////////////////////////////////////////
                // Se chegamos aqui, é por causa que uma OPERATION aconteceu (ou TIMEOUT expirou)
                // Consultamos as SelectionKeys do Seletor para ver quais OPERATIONS estão disponiveis
                Set<SelectionKey>      readyHandles   = demultiplexer.selectedKeys();
                Iterator<SelectionKey> handleIterator = readyHandles.iterator();

                ///////////////////////////////////////////////////
                // Percorre todos OPERATIONS DISPONVEIS
                while (handleIterator.hasNext()) {
                    SelectionKey handle = handleIterator.next();
                    handleIterator.remove();

                    /////////////////////////////////////////////////////////////
                    // Handle pode estar inválido, se por exemplo cliente fechou conexão
                    if (handle.isValid()){

                        if (handle.isAcceptable()) {
                            EventHandler handler = registeredHandlers.get(SelectionKey.OP_ACCEPT);
                            handler.handleEvent(handle);
                            //handleIterator.remove();
                        } else if (handle.isReadable()) {
                            EventHandler handler = registeredHandlers.get(SelectionKey.OP_READ);
                            handler.handleEvent(handle);
                            //handleIterator.remove();
                        } else if (handle.isConnectable()) {
                            EventHandler handler = registeredHandlers.get(SelectionKey.OP_CONNECT);
                            handler.handleEvent(handle);
                           // handleIterator.remove();
                        } else  if (handle.isWritable()) {
                            EventHandler handler = registeredHandlers.get(SelectionKey.OP_WRITE);
                            handler.handleEvent(handle);
                            //handleIterator.remove();
                        }

                    } else {
                        //Handler inválido
                       // handleIterator.remove();
                        return;
                    }
                }
            } catch (Exception e) {
                    e.printStackTrace();
            }
        }
    }

    /*******************************************************************************************************************
     *
     * Interface - ListenPattern
     *
     *******************************************************************************************************************/
    public interface ReactorEventListener{
        void onClientConnectReactor      (SocketChannel clientChannel);
        void onClientDisconnectReactor   (SocketChannel clientChannel);
        void onClientDataAvailableReactor(SocketChannel clientChannel, String data);
    }
}

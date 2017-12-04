package com.company.MyServerTCP;

import com.company.PatternReactor.AcceptSocketEventHandler;
import com.company.PatternReactor.Reactor;
import com.company.PatternReactor.ReadSocketEventHandler;
import com.company.PatternReactor.WriteSocketEventHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Criação: Matheus Mignoni 2017-11-21.
 *
 * Non-blocking server with NIO
 * http://kasunpanorama.blogspot.com.br/2015/04/understanding-reactor-pattern-with-java.html
 *
 * Selector (demultiplexer)
 * Selector is the Java building block, which is analogous to the demultiplexer in the Reactor pattern.
 * Selector is where you register your interest in various I/O events and the objects tell you when those events occur.
 *
 * Reactor/initiation dispatcher
 * We should use the Java NIO Selector in the Dispatcher/Reactor. For this, we can introduce our own Dispatcher/Reactor
 * implementation called ‘Reactor’. The reactor comprises java.nio.channels.Selector and a map of registered handlers.
 * As per the definition of the Dispatcher/Reactor, ‘Reactor’ will call the Selector.select() while waiting for the IO event to occur.
 *
 * Handle
 * In the Java NIO scope, the Handle in the Reactor pattern is realized in the form of a SelectionKey.
 *
 * Event
 * The events that trigger from various IO events are classified as - SlectionKey.OP_READ etc.
 *
 * Handler
 * A handler is often implemented as runnable or callable in Java.
 *
 */
public class MyAsyncServerTCP implements Reactor.ReactorEventListener{

    protected ServerSocketChannel  server      = null;               //SocketServer TCP Channel

    protected Reactor              reactor     = null;

    private   ScheduledExecutorService AliveTimer = null;

    //Server Config
    protected int                  serverPort  = 8080;               //Porta na qual estamos fazendo listening
    protected boolean              isClosed    = true;               //Flag que indica se o servidor está fechado

    protected int                  maxClients  = 0;                  //Número máximo de clientes, 0 se ilimitado.

    //Clientes
    protected Class                clientClass = null;                //Classe personalizada do cliente
    protected List<MyAsyncServerTCPClient> lstClients  = null;        //Lista dos clientes conectados //TODO = TRANSFORMAR LISTA EM HASH

    //Listener Pattern
    private MyAsyncServerTCPListener mListener = null;

    //Constantes
    private static final int HEARTBEAT_INTERVAL_SECONDS = 10;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public MyAsyncServerTCP() {
        System.out.println("initializing server");

        lstClients = new LinkedList<>();

        //Configura timer de alive
        this.AliveTimer = Executors.newScheduledThreadPool(1);
        AliveTimer.scheduleWithFixedDelay(new CheckAliveClients(this.lstClients), 0, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /*******************************************************************************************************************
     *
     * Método principal - inicia server
     *
     *******************************************************************************************************************/
    public void run(){
        try {
            this.server = ServerSocketChannel.open(); // Abre o ServerSocketChannel
            this.server.configureBlocking(false);     // Obrigatoriamente deve-se configurar como non-blocking, senao nao poderia registrar no seletor
            this.server.socket().bind(new InetSocketAddress(serverPort));  // Associa ao endereço que será usado para o server

            this.reactor = new Reactor();
            this.reactor.registerChannel(SelectionKey.OP_ACCEPT, server);

            ///////////////////////////////////////////////
            // Regitra manipuladores de evento no Reactor
            this.reactor.registerEventHandler( SelectionKey.OP_ACCEPT, new AcceptSocketEventHandler(this.reactor.getDemultiplexer(), this));
            this.reactor.registerEventHandler( SelectionKey.OP_READ,   new ReadSocketEventHandler(this.reactor.getDemultiplexer(), this));
            this.reactor.registerEventHandler( SelectionKey.OP_WRITE,  new WriteSocketEventHandler(this.reactor.getDemultiplexer()));

            ///////////////////////////////////////////////
            // Roda loop do dispatcher
            this.reactor.run();

            this.isClosed = false;

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /*******************************************************************************************************************
     *
     * Getters & Setters
     *
     *******************************************************************************************************************/
    public void setClientClass(Class clientClass) throws Exception {
        ////////////////////////////////////////////////////////////////
        // Verifica se a classe passada como parâmetro extende MyAsyncServerTCPClient
        if (MyAsyncServerTCPClient.class.isAssignableFrom(clientClass)){
            this.clientClass = clientClass;
        } else {
            throw new Exception("MyAsyncServerTCP.setClientClass : "+clientClass.toString()+ " do NOT extends MyAsyncServerTCPClient");
        }
    }

    public int  getMaxClients() { return this.maxClients; }
    public void setMaxClientes(int value){ this.maxClients = value;}

    /*******************************************************************************************************************
     *
     * Funções úteis
     *
     *******************************************************************************************************************/
    public int clientCount(){
        return this.lstClients.size();
    }

    public synchronized void close(){
        this.isClosed = true;
        try {
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private synchronized boolean isClosed() {
        return this.isClosed;
    }

    private MyAsyncServerTCPClient findClient(SocketChannel clientChannel){
        for(MyAsyncServerTCPClient client: lstClients){
          if (client.getChannel().equals(clientChannel))
              return client;
        }
        return null;
    }

    public void broadcast(String data){
        for(MyAsyncServerTCPClient client: lstClients){
            client.send(data);
        }

    }

    /*******************************************************************************************************************
     *
     * AliveTimer
     *
     *******************************************************************************************************************/
    private static final class CheckAliveClients implements Runnable {
        List<MyAsyncServerTCPClient> lstClients;

        public CheckAliveClients(List<MyAsyncServerTCPClient> lstClients){
            this.lstClients = lstClients;
        }

        @Override public void run() {
            try {
                for (MyAsyncServerTCPClient client : this.lstClients) {
                    if (client.secondsSinceLastSignal() > HEARTBEAT_INTERVAL_SECONDS * 3) {
                        System.out.println("Client " + client.address() + " disconnect by alive timer");

                        client.close();

                    } else {
                        //Send heartbeat
                        client.send("vivo");
                    }

                }
            } catch (Exception e){
                System.out.println("EXCEPTION: CheckAliveClients " +e.getMessage());
            }
        }
    }

    /*******************************************************************************************************************
     *
     * Reactor interface implementation
     *
     *******************************************************************************************************************/
    public void onClientConnectReactor(SocketChannel clientChannel){
        try {
            if ((this.maxClients <= 0) || (clientCount() < this.maxClients)) {
                /////////////////////////////////////////////////////////////////////
                // Instancia cliente e guarda na lista

                MyAsyncServerTCPClient client;

                ///////////////////////////////////////////////////////////////////
                // Verifica se cliente terá classe personaizada
                if (clientClass != null) {
                    Class a = clientClass.asSubclass(MyAsyncServerTCPClient.class);
                    client = (MyAsyncServerTCPClient) a.newInstance();
                } else {
                    client = new MyAsyncServerTCPClient();
                }

                ///////////////////////////////////////////////////////////////////
                // Seta Selector e Channel do cliente
                client.setSelector(this.reactor.getDemultiplexer());
                client.setChannel(clientChannel);

                lstClients.add(client);

                /////////////////////////////////////////////////////////////////////
                // Chama evento para listener
                if (mListener != null)
                    mListener.onClientConnect(client);

                broadcast("now we have " + String.valueOf(clientCount()) + " client(s)");
            } else {
                //Servidor lotado, derruba cliente
                clientChannel.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClientDisconnectReactor(SocketChannel clientChannel){

        /////////////////////////////////////////////////////////////////////
        // Busca na lista o cliente referido
        MyAsyncServerTCPClient client = findClient(clientChannel);
        if (client != null) {

            /////////////////////////////////////////////////////////////////////
            // Chama evento para listener
            if (mListener != null)
                mListener.onClientDisconnect(client);

            /////////////////////////////////////////////////////////////////////
            // Remove referência do cliente da lista
            lstClients.remove(client);
        } else {
            System.out.println("Cliente desconectado e não encontrado "+clientChannel.toString());
        }
    }

    public void onClientDataAvailableReactor(SocketChannel clientChannel, String data){

        /////////////////////////////////////////////////////////////////////
        // Busca na lista o cliente referido
        MyAsyncServerTCPClient client = findClient(clientChannel);
        if (client != null) {
            /////////////////////////////////////////////////////////////////////
            // Chama evento para o cliente
            client.receive(data);

        } else {
            System.out.println("Cliente não encontrado "+clientChannel.toString());
        }
    }

    /*******************************************************************************************************************
     *
     * Interface para Eventos do servidor
     *
     *******************************************************************************************************************/
    public interface MyAsyncServerTCPListener{
        void onClientConnect(MyAsyncServerTCPClient client);
        void onClientDisconnect(MyAsyncServerTCPClient client);
    }

    // Perimte que o parent se registre como listener
    public void setListener(MyAsyncServerTCPListener listener) {
        this.mListener = listener;
    }

}

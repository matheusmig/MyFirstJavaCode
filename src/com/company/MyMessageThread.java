package com.company;
import java.util.concurrent.*;

/**
 *   Criação: Matheus Mignoni
 * Descrição: Classe que implementa uma thread que processa mensagens
 *
 * A thread possui uma fila bloqueante de mensagens
 * Utiliza Synchronized, Notify e Wait.
 *
 *
 */
public class MyMessageThread extends Thread {
    final static Integer QUEUE_SIZE = 1024;  //Tamanho da fila de mensagens da thread

    BlockingQueue<MyMessage> queueMessages = new LinkedBlockingQueue<MyMessage>(QUEUE_SIZE);

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyMessageThread(){
    }

    /*******************************************************************************************************************
     *
     * Loop principal da Thread de Mensagens
     *
     *******************************************************************************************************************/
    public void run() {
        runCreate();

        MyMessage msg;
        while((msg = GetMessage()) != null){
            ProcessMessage(msg);
            DispatchMessage(msg);
        }

        runDestroy();
    }

    /*******************************************************************************************************************
     *
     * Inicializacoes no contexto da thread
     *
     *******************************************************************************************************************/
    public void runCreate() {

    }

    /*******************************************************************************************************************
     *
     * Desalocações no contexto da thread
     *
     *******************************************************************************************************************/
    public void runDestroy() {

    }



    private synchronized MyMessage GetMessage(){
        MyMessage msg;
        ///////////////////////////////
        // Teste se há mensgens na fila
        if (this.queueMessages.size() == 0) {
            try{
                this.wait(); //Aguarda ser acordada
            }catch(InterruptedException e){
                e.printStackTrace();
                return null;
            }
        }

        ///////////////////////////////////
        //Pega primeira mensagem da fila
        msg = this.queueMessages.peek();

        if (msg.getId() == MyMessage.MSG_THREAD_QUIT){
            //Mesangem de sair
            DispatchMessage(msg);
            return null;
        }
        return msg;

    }

    private void ProcessMessage(MyMessage msg){
       this.MessageHandler(msg);
    }

    private void DispatchMessage(MyMessage msg){
        this.queueMessages.remove(msg);
    }

    protected void MessageHandler(MyMessage Msg){
        System.out.println(Msg.getId().toString());
    }


    public synchronized void SendMessage(Integer Id, String strMsg){
        MyMessage Msg = new MyMessage(Id, strMsg);

        ////////////////////////////////
        // Coloca mensagem na fila
        this.queueMessages.add(Msg);

        ////////////////////////////////
        // Acorda a Thread
        this.notify();
    }


    public synchronized void SendMessage(Integer Id, Object data){
        MyMessage Msg = new MyMessage(Id, data);

        ////////////////////////////////
        // Coloca mensagem na fila
        this.queueMessages.add(Msg);

        ////////////////////////////////
        // Acorda a Thread
        this.notify();
    }

}

package com.company;

/**
 * "Estrutura" que armazena uma mensagem enviada entre threads
 *
 */
public class MyMessage {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTES DE MENSAGENS ENTRE THREADS
    public static final int MSG_THREAD_QUIT            = -1;

    public static final int MSG_FOLDER_EVENT_CREATE    = 001;
    public static final int MSG_FOLDER_EVENT_DELETE    = 002;
    public static final int MSG_FOLDER_EVENT_MODIFIED  = 003;

    public static final int MSG_DATA_REPORT            = 004;

    private Integer id;
    private Object  data;

    /*******************************************************************************************************************
    *
    * Construtor
    *
    *******************************************************************************************************************/
    MyMessage(Integer Id, Object data){
        this.id   = Id;
        this.data = data;
    }

    public Integer getId(){
        return this.id;
    }
    public Object  getData(){
        return this.data;
    }
}

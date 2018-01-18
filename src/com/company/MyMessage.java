package com.company;

/**
 * "Estrutura" que armazena uma mensagem enviada entre threads
 *
 */
public class MyMessage {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTES DE MENSAGENS ENTRE THREADS
    public static final int MSG_THREAD_QUIT               = -1;

    public static final int MSG_FOLDER_EVENT_CREATE       = 001;
    public static final int MSG_FOLDER_EVENT_DELETE       = MSG_FOLDER_EVENT_CREATE + 1;
    public static final int MSG_FOLDER_EVENT_MODIFIED     = MSG_FOLDER_EVENT_DELETE + 1;

    public static final int MSG_DATA_REPORT               = MSG_FOLDER_EVENT_MODIFIED + 1;


    public static final int MSG_LOGWRITER_APPEND_LOG      = MSG_DATA_REPORT + 1;
    public static final int MSG_LOGWRITER_WRITE_TO_FILE   = MSG_LOGWRITER_APPEND_LOG + 1;

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

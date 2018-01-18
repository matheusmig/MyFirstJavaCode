package com.company;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 *   Criação: Matheus Mignoni on 2018-01-15.
 * Descrição: Classe que implemena uma Thread de baixa prioridade que escreve em um arquivo de log
 *
 *
 */
public class MyLogWriter extends MyMessageThread {
    public static final int    BUFFER_WRITE_INTERVAL_MS = 500; //ms
    public static final String TIMESTAMP_LOG_FORMAT     = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIMESTAMP_FILENAME       = "yyyyMMdd";

    private DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP_LOG_FORMAT); //Formato do timestamp
    private String logFilePath;                              //Caminho do arquivo do log
    protected static BufferedWriter bufferedWriter;          //Classe que efetivamente escreve no arquivo
    protected static List<String> arBuffer;                  //Buffer de dados a serem escritos no disco

    private ScheduledExecutorService WriterTimer = null;          //Timer de escrita em disco

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyLogWriter(String strFileName){
        //Seta baixa prioridade para a thread
        this.setPriority(Thread.MIN_PRIORITY);

        //Especifica o nome do arquivo e seu caminho
        DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(TIMESTAMP_FILENAME);
        String absolutePath = System.getProperty("user.dir") + "/";
        String dateSuffix   = LocalDate.now().format(timestampFormat);

        this.logFilePath = absolutePath + strFileName + "_"+ dateSuffix +".log";

        this.arBuffer = Collections.synchronizedList(new ArrayList<String>());
    }

    /*******************************************************************************************************************
     *
     * Override no métooo de inicializacoes
     *
     *******************************************************************************************************************/
    public void runCreate(){

        try {
            // Garante que arquivo será criado
            if(!Files.exists(Paths.get(this.logFilePath))){
                System.out.println("Arquivo criado");
                Files.createFile(Paths.get(this.logFilePath), new FileAttribute[] {});
            }

            // Cria objeto bufferizado de escrita em disco
            this.bufferedWriter = new BufferedWriter(new FileWriter(this.logFilePath, true));

            // Cria e inicializa timer de escrita em disco
            this.WriterTimer = Executors.newScheduledThreadPool(1);
            this.WriterTimer.scheduleWithFixedDelay( new CheckBufferToWrite(),1, BUFFER_WRITE_INTERVAL_MS, TimeUnit.MILLISECONDS);

        } catch(Exception ioe){
            System.out.println("MyLogWriter.runCreate : "+logFilePath.toString()+ " : Exception occurred: ");
            ioe.printStackTrace();
        }

    }

    /*******************************************************************************************************************
     *
     * Override no métooo de inicializacoes
     *
     *******************************************************************************************************************/
    public void runDestroy(){
        try {
            this.bufferedWriter.close();
        } catch(Exception ioe){
            System.out.println("MyLogWriter.runDestroy : "+logFilePath.toString()+ " : Exception occurred: ");
            ioe.printStackTrace();
        }

    }

    /*******************************************************************************************************************
     *
     * Override no métooo de manuseio das mensagens recebidas
     *
     *******************************************************************************************************************/
    protected void MessageHandler(MyMessage Msg){
        switch (Msg.getId()) {
            case MyMessage.MSG_LOGWRITER_APPEND_LOG     : HandleAppendLog(Msg); break;
        }
    }

    /*******************************************************************************************************************
     *
     * Manuseia mensagem de concatenar log
     *
     *******************************************************************************************************************/
    private void HandleAppendLog(MyMessage Msg){
        String strLine = (String)Msg.getData();

        String time = LocalDateTime.now().format(timestampFormat); //Pega horário e formata timestamp
        this.arBuffer.add(time+" : "+strLine);
    }

    public void WriteMsg(String strMsg){
        this.SendMessage(MyMessage.MSG_LOGWRITER_APPEND_LOG, strMsg);
    }
    public void WriteErrorMsg(String strMsg){
        this.SendMessage(MyMessage.MSG_LOGWRITER_APPEND_LOG, "***ERROR***"+strMsg);
    }
    public void WriteWarningMsg(String strMsg){
        this.SendMessage(MyMessage.MSG_LOGWRITER_APPEND_LOG, "***WARNING***"+strMsg);
    }

    /*******************************************************************************************************************
     *
     * Timer de escrita em disco
     *
     *******************************************************************************************************************/
    private static final class CheckBufferToWrite implements Runnable {

        public CheckBufferToWrite(){}

        @Override public void run() {
            try {
                if (arBuffer.size() > 0) {
                    for (String strLine : arBuffer) {
                        bufferedWriter.write(strLine);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush(); //Escreve tudo em disco
                    arBuffer.clear();
                }
            } catch (Exception x) {
                System.out.println("MyLogWriter : Timer:  Exception:"+x);
            }
        }
    }


}

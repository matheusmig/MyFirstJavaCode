package com.company;
import java.io.*;
/**
 *   Criação: Matheus Mignoni
 * Descrição: Thread principal do programa
 *
 *
 */
public class MainThread extends MyMessageThread {
    // Constantes.
    //public static final String PATH_INTPUT = System.getProperty("java.home")+"/data/in/";
    //public static final String PATH_OUTPUT = System.getProperty("java.home")+"/data/out/";
    public static final String PATH_INTPUT = "/Users/mmignoni/Desktop/teste/";
    public static final String PATH_OUTPUT = "/Users/mmignoni/Desktop/teste/out";

    // Objetos principais
    MyFolderMonitor monitor     = null; //Monitor de pastas
    MyFileReader fileReader     = null; //Processador de arquivos e interpretador de dados
    MyReportWriter dataWriter   = null; //Escreve dados

    //Threads auxiliares
    Thread monitorFolderThread;

    ////////////////////////////////////////////////////////////////////
    // Override no métooo de inicializacoes
    public void runCreate(){
        try {
            //inicializa array com as extensõe procuradas
            String[] arrExtensios = {"dat"};

            //Instancia threads
            this.dataWriter          = new MyReportWriter();
            this.fileReader          = new MyFileReader(dataWriter);
            this.monitor             = new MyFolderMonitor(this, PATH_INTPUT, true, arrExtensios);
            this.monitorFolderThread = new Thread(monitor);

            //Starta Threads
            dataWriter.start();
            fileReader.start();
            monitorFolderThread.start();


        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    ////////////////////////////////////////////////////////////////////
    // Override no métooo que manuseia as mensagens recebidas
    protected void MessageHandler(MyMessage Msg){
        System.out.println("Main Thread received ID: "+ Msg.getId().toString()+ " | Data: "+ (String)Msg.getData());
    }


}

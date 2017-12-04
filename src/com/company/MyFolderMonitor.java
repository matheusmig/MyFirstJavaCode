package com.company;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Arrays;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 *   Criação: Matheus Mignoni
 * Descrição: Thread que monitora arquivos em uma pasta específica.
 *
 * Quando um evento é disparado, avisa a thread pai através de mensagem entre threads
 *
 */
public class MyFolderMonitor implements Runnable {
    private MyMessageThread parent;        //Referência para a thread que receberá as mensagens de aviso
    private WatchService watcher;          //Objeto que efetivamente monitora a pasta
    private Path dir;                      //Pasta a ser monitorada
    private WatchKey key;                  //Chave do monitor

    private String[] arrExtensions;        //Extensões de arquivo que estão sendo monitoradas. Se vazio, todas estarão.
    private boolean recursive;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyFolderMonitor(MyMessageThread parent, String folderPath, boolean recursive, String[] arrExtensions ){
        this.parent        = parent;
        this.dir           = Paths.get(folderPath);
        this.watcher       = null;
        this.recursive     = recursive;

        this.arrExtensions = arrExtensions;
    }

    MyFolderMonitor(MyMessageThread parent, String folderPath, boolean recursive){
        this.parent        = parent;
        this.dir           = Paths.get(folderPath);
        this.watcher       = null;
        this.recursive     = recursive;

        this.arrExtensions = new String[0];
    }

    /*******************************************************************************************************************
     *
     * Inicialização de objetos no contexto da thread
     *
     *******************************************************************************************************************/
    private void runCreate(){
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Inicializações no contexto da thread
        try {
            this.watcher = FileSystems.getDefault().newWatchService();    //Cria o watch service
            register(this.watcher);                                       //Registra um objeto com o watch service, para os 3 tipos de eventos
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    /*******************************************************************************************************************
     *
     * Registra uma pasta com o watchservice
     *
     *******************************************************************************************************************/
    private void register(WatchService watcher) throws IOException {
        if (!this.recursive) {
            ///////////////////////////////////////////////////////////////////////////////////
            // Não recursivo: registra apenas a pasta
            this.key = this.dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } else {
            ///////////////////////////////////////////////////////////////////////////////////
            // Recursivo: regitra a pasta e todas suas subpastas
            Files.walkFileTree(this.dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /*******************************************************************************************************************
     *
     * Loop da thread
     *
     *******************************************************************************************************************/
    public void run() {
        runCreate(); //Inicializações

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Loop da thread
        while (!Thread.interrupted()) {
            processFile();
        }
    }

    /*******************************************************************************************************************
     *
     * Métood principal da thread
     *
     *******************************************************************************************************************/
    private void processFile(){
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Espera pela sinalização da key
        try {
            this.key = this.watcher.take();
        } catch (InterruptedException x) {
            return;
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Processa eventos de modificacoes de arquivos
        for (WatchEvent<?> event: key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            // Ignora eventos do tipo OVERFLOW.
            if (kind == OVERFLOW) {
                continue;
            }

            // O nome do arquivo é o contexto do evento
            WatchEvent<Path> ev = (WatchEvent<Path>)event;
            Path filename = ev.context();

            // Verifica se o arquivo capturado contém extensão desejada
            String fileExtension = MyUtils.FileExtensionGetter(filename.toString());
            if ( (this.arrExtensions.length <= 0) ||                                  //OU Não estamos filtrando extensões,
                 (Arrays.asList(this.arrExtensions).contains(fileExtension)) ){       //OU Extensão pertence a lista de extesoes procuradas.

                /////////////////////////////////////////////////////////////////////////////////////////
                // Arquivo correspondente alterado!
                // Avisa thread pai através de mensagem

                //Envia mensagens de evento
                if        (kind == ENTRY_CREATE) {
                    this.parent.SendMessage(MyMessage.MSG_FOLDER_EVENT_CREATE, filename.toString());
                } else if (kind == ENTRY_DELETE) {
                    this.parent.SendMessage(MyMessage.MSG_FOLDER_EVENT_DELETE, filename.toString());
                } else if (kind == ENTRY_MODIFY) {
                    this.parent.SendMessage(MyMessage.MSG_FOLDER_EVENT_MODIFIED, filename.toString());
                }

            }
        }

        this.key.reset();
    }


}

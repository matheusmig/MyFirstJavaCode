package com.company;
import com.company.IllegraDesafio.MyReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.*;
import java.io.BufferedWriter;

/**
 *   Criação: Matheus Mignoni
 * Descrição: Classe que analisa dados recebidos e gera relatórios
 *
 *
 */
public class MyReportWriter extends MyMessageThread {


    /*******************************************************************************************************************
     *
     * Override no métooo de inicializacoes
     *
     *******************************************************************************************************************/
    public void runCreate(){

    }

    /*******************************************************************************************************************
     *
     * Override no métooo de manuseio das mensagens recebidas
     *
     *******************************************************************************************************************/
    protected void MessageHandler(MyMessage Msg){
        switch (Msg.getId()) {
            case MyMessage.MSG_DATA_REPORT   :  HandleReportDone(Msg); break;
        }
    }

    /*******************************************************************************************************************
     *
     * Manuseia mensagem de report finalizado
     *
     *******************************************************************************************************************/
    private void HandleReportDone(MyMessage Msg){
        MyReport report = (MyReport)Msg.getData();


        // Altera extensao:
        String fileOutputName = MyUtils.FileExtensionChange(report.getFileName(), ".done.dat");

        String absolutePath = MainThread.PATH_OUTPUT + fileOutputName;
        Path path = Paths.get(absolutePath);

        /////////////////////////////////////////////////////////////////////////
        // Usa BufferedWriter para escrever arquivo
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            //////////////////////////////////////////////////////
            // Escreve linha a linha do summary
            for (String s: report.summaryReport()) {
                writer.write(s);
                writer.newLine();
            }

        } catch (IOException x) {
            System.out.println("HandleReportDone Exception:"+x);
        }
    }
}

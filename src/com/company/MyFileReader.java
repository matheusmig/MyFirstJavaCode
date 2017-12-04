package com.company;
import com.company.IllegraDesafio.MyReport;

import java.nio.file.Files;
import java.util.stream.*;
import java.nio.file.*;
import java.io.IOException;

/**
 *   Criação: Matheus Mignoni
 * Descrição: Classe que faz a leitura de arquivo
 *
 *
 */
public class MyFileReader extends MyMessageThread {
    private MyMessageThread reportWriter;    //Referência para a thread que escreve em disco

    /*******************************************************************************************************************
     *
     * Constantes
     *
     *******************************************************************************************************************/
    private static final int LINE_ID_SALESMAN = 001;
    private static final int LINE_ID_COSTUMER = 002;
    private static final int LINE_ID_SALES    = 003;

    private static final String SEPARATION_CHARACTER = "ç";

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    MyFileReader(MyMessageThread reportWriter){
        this.reportWriter  = reportWriter;
    }

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
            case MyMessage.MSG_FOLDER_EVENT_CREATE   :  HandleFileCreate(Msg); break;
            case MyMessage.MSG_FOLDER_EVENT_DELETE   :  HandleFileEdit(Msg);   break;
            case MyMessage.MSG_FOLDER_EVENT_MODIFIED :  break; //Nao implementado ainda
        }
    }


    /*******************************************************************************************************************
     *
     * Manuseia mensagem de arquivo criado
     *
     *******************************************************************************************************************/
    private void HandleFileCreate(MyMessage Msg){
        String filename = (String)Msg.getData();

        /////////////////////////////////////////////////////////////////////////
        // Cria objeto de report
        MyReport report = new MyReport(filename);

        /////////////////////////////////////////////////////////////////////////
        // Processa arquivo
        // TODO: Checar se arquivo existe ao abri-lo
        String absolutePath = MainThread.PATH_INTPUT + filename; //Pega caminho abosluto do arquivo
        try (Stream<String> stream = Files.lines(Paths.get(absolutePath))) {
            stream.forEach( (line) -> ProcessFileLine(line, report) ); //Pocessa linha a linha e gera report
        } catch (IOException x) {
            System.out.println("MyFileReader Exception:"+x);
        }

        /////////////////////////////////////////////////////////////////////////
        // Finaliza Report.
        ProcessFileEnd(report);
    }

    /*******************************************************************************************************************
     *
     * Manuseia mensagem de arquivo editado
     *
     *******************************************************************************************************************/
    private void HandleFileEdit(MyMessage Msg){
        String filename = (String)Msg.getData();

        /////////////////////////////////////////////////////////////////////////
        // Cria objeto de report
        MyReport report = new MyReport(filename);

        /////////////////////////////////////////////////////////////////////////
        // Processa arquivo
        // TODO: Checar se arquivo existe ao abri-lo
        String absolutePath = MainThread.PATH_INTPUT + filename; //Pega caminho abosluto do arquivo
        try (Stream<String> stream = Files.lines(Paths.get(absolutePath))) {
            stream.forEach( (line) -> ProcessFileLine(line, report) ); //Pocessa linha a linha e gera report
        } catch (IOException x) {
            System.out.println("MyFileReader Exception:"+x);
        }

        /////////////////////////////////////////////////////////////////////////
        // Finaliza Report.
        ProcessFileEnd(report);
    }

    /*******************************************************************************************************************
     *
     * Processa a linha do arquivo
     *
     *******************************************************************************************************************/

    private void ProcessFileLine(String line, MyReport report){
        try {
            ///////////////////////////////////////////////////////////////////////
            // Quebra a string no caracter de separação
            String[] parts = line.split(SEPARATION_CHARACTER);

            ///////////////////////////////////////////////////////////////////////
            // Processa linha de acordo com ID
            Integer id = Integer.parseInt(parts[0]);
            switch(id){
                case LINE_ID_SALESMAN: ProcessLineSalesman(parts, report); break;
                case LINE_ID_COSTUMER: ProcessLineConsumer(parts, report); break;
                case LINE_ID_SALES:    ProcessLineSales   (parts, report); break;

            }


        } catch (Exception e) {
            System.out.println("ProcessFileLine Exception:"+e);
        }

    }


    /*******************************************************************************************************************
     *
     * Processa a linha de salesman
     *
     * TODO = criar classe especifica para interpreatar as linhas
     *
     *******************************************************************************************************************/
    private void ProcessLineSalesman(String[] parts, MyReport report){
        //System.out.println("Salesman:"+ Arrays.toString(parts));

        String salesmanCPF    = parts[1];
        String salesmanName   = parts[2];
        Float  salesmanSalary = Float.valueOf(parts[3]);

        //Adiciona entrada no report
        report.addSalesman(salesmanCPF, salesmanName, salesmanSalary);
    }

    /*******************************************************************************************************************
     *
     * Processa a linha de consumer
     *
     * TODO = criar classe especifica para interpreatar as linhas
     *
     *******************************************************************************************************************/
    private void ProcessLineConsumer(String[] parts, MyReport report){
        //System.out.println("Consumer:"+Arrays.toString(parts));

        String costumerCNPJ         = parts[1];
        String costumerName         = parts[2];
        String costumerBusinessArea = parts[3];

        //Adiciona entrada no report
        report.addCostumer(costumerCNPJ, costumerName, costumerBusinessArea);
    }

    /*******************************************************************************************************************
     *
     * Processa a linha de sales
     *
     * TODO = criar classe especifica para interpreatar as linhas
     *
     *******************************************************************************************************************/
    private void ProcessLineSales(String[] parts, MyReport report){
        //System.out.println("Sale:"+Arrays.toString(parts));

        Integer saleID           = Integer.parseInt(parts[1]);
        String  saleLstItems     = parts[2];
        String  saleSalesmanName = parts[3];

        //Adiciona entrada no report
        report.addSale(saleID, saleLstItems, saleSalesmanName);
    }

    /*******************************************************************************************************************
     *
     * Processa a linha de sales
     *
     * TODO = criar classe especifica para interpreatar as linhas
     *
     *******************************************************************************************************************/
    private void ProcessFileEnd(MyReport report){
        ////////////////////////////////////////////////////////////////////////
        // Arqiuvo finalizado. Envia para ser salvo
        this.reportWriter.SendMessage(MyMessage.MSG_DATA_REPORT, report);

    }

}

package com.company;

import com.company.MyServerTCP.MyServerTCP;

import java.awt.*;

public class Main {
    static MyLogWriter EventWriter = new MyLogWriter("EventLogTeste");

    public static MyLogWriter getWriter(){
        return EventWriter;
    }

    public static void main(String[] args) {
        getWriter().start();
        getWriter().WriteMsg("Serviço Inicializado");

        MyServerTCP a = new MyServerTCP();

        // Ler configuraÇões de arquivo de configuração (porta TCP)

    }
}
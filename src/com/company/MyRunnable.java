package com.company;

/**
 * Created by mmignoni on 2017-11-09.
 *
 * Implementa thread através de "implements Runanble"
 */
public class MyRunnable implements Runnable {
    String strName; //Nome da thread

    MyRunnable(String strName) {
        this.strName = strName;
    }

    public void run() {
        for(int x = 0; x < 100; x = x + 1) {
            System.out.println("I'm "+this.strName+" Thread");
        }

    }
}

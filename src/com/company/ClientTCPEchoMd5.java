package com.company;

import com.company.MyServerTCP.MyAsyncServerTCPClient;

import java.security.MessageDigest;

/**
 * Created by mmignoni on 2017-12-04.
 */
public class ClientTCPEchoMd5 extends MyAsyncServerTCPClient {
    public void onDataAvailable(String data){
        if (checkConsistency(data)) {
            processMessage(data);
        } else {
            this.close();
        }
    }

    private boolean checkConsistency(String data){
        if (data.contains("|"))
            return true;
        else
            return false;
    }

    private void processMessage(String data){
        String msg[] = data.split("\\|");

        String msgID   = msg[0];
        String msgData = msg[1];

        switch (msgID){
           case "1" : processMD5Replay(msgData);
        }
    }

    private void processMD5Replay(String data){
        try{
            Main.getWriter().WriteMsg("ClientTCPEchoMd5.processMD5Replay : "+this.address()+" : "+data);

            byte[] bytesOfMessage = data.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            String strResultMD5 = md.digest(bytesOfMessage).toString() + System.lineSeparator();

            ///////////////////////////////////
            //Envia de volta a cliente
            this.send(strResultMD5);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

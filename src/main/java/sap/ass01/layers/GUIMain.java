package sap.ass01.layers;

import sap.ass01.layers.PL.WebClient;
import sap.ass01.layers.PL.dialogs.LoginDialog;

import javax.swing.*;

public class GUIMain {
    public static void main(String[] args) {
        WebClient client = new WebClient();
        SwingUtilities.invokeLater(() -> {
            LoginDialog dialog = new LoginDialog(null, client);
            dialog.setVisible(true);
        });
        /*client.requestCreateUser("buba","gugu");
        Thread.sleep(5000);
        client.requestDeleteUser(3);*/
        /*client.login("GiacomoC","password").onComplete(x -> {
            System.out.println(x.succeeded());
        });*/
        /*
        */
        /*client.startMonitoringCountChanges();
        client.queryCount().onComplete(ar -> {
            int x = ar.result().getInteger("value");
            for (int i = x; i <= 7 + x; i++) {
                client.sendCountUpdate(i);
                try {
                    Thread.sleep(500); // Wait for 2 seconds between updates
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/
        // Simulate sending count updates


        // Query the count after some updates
        //client.queryCount();
    }
}

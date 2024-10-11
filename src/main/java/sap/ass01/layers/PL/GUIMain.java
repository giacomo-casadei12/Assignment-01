package sap.ass01.layers.PL;

public class GUIMain {
    public static void main(String[] args) throws InterruptedException {
        WebClient client = new WebClient();
        /*client.login("GiacomoC","password").onComplete(x -> {
            System.out.println(x.succeeded());
        });*/
        client.requestCreateUser("buba","gugu");
        Thread.sleep(5000);
        client.requestDeleteUser(3);
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

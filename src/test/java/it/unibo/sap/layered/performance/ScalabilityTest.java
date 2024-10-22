package it.unibo.sap.layered.performance;

import io.vertx.core.*;
import io.vertx.core.Future;
import sap.ass01.layers.PresentationL.Web.WebClient;
import sap.ass01.layers.PresentationL.Web.WebClientImpl;
import sap.ass01.layers.PresentationL.Web.WebController;

import java.util.Map;
import java.util.concurrent.*;

//100  - 1044 - 1056 - 1059
//500  - 2201 - 2010 - 2174
//1000 - 3034 - 2898 - 2959

public class ScalabilityTest {

    public static final int USERS = 1000;

    public static void main(String[] args) {
        WebClient webClient = new WebClientImpl();
        WebController webController = new WebController();
        CountDownLatch latch = new CountDownLatch(USERS);

        webController.start();

        ExecutorService executorService = Executors.newFixedThreadPool(USERS);
        Map<Integer, Long> map = new ConcurrentHashMap<>();
        map.put(1, 0L);

        for (int i = 0; i < USERS; i++) {
            executorService.submit(() -> {
                Future<Long> future = new WebCallTask(webClient).call();
                future.onComplete(ar -> {
                    if (ar.succeeded()) {
                        long responseTime = ar.result();
                        map.compute(1, (key, value) -> (value == null ? 0 : value) + responseTime);
                        System.out.println("Response time: " + responseTime + " ms");
                    } else {
                        System.err.println("Error during request: " + ar.cause().getMessage());
                    }
                    latch.countDown();
                });
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for completions");
        }

        executorService.shutdown();
        double averageResponseTime = (double) map.get(1) / USERS;
        System.out.println("Average Response Time: " + averageResponseTime + " ms");
        System.exit(0);
    }

    static class WebCallTask {
        private final WebClient webClient;

        public WebCallTask(WebClient webClient) {
            this.webClient = webClient;
        }

        public Future<Long> call() {
            long startTime = System.currentTimeMillis();
            Promise<Long> promise = Promise.promise();

            webClient.requestMultipleReadRide(0, 0, false).onComplete(x -> {
                if (x.succeeded()) {
                    promise.complete(System.currentTimeMillis() - startTime);
                } else {
                    promise.fail(x.cause());
                }
            });

            return promise.future();
        }
    }

}

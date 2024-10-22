package it.unibo.sap.clean.performance;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import sap.ass01.clean.domain.BusinessLogicL.RideManager;
import sap.ass01.clean.domain.BusinessLogicL.RideManagerImpl;
import sap.ass01.clean.domain.ports.AppManager;
import sap.ass01.clean.domain.ports.AppManagerImpl;
import sap.ass01.clean.domain.ports.dataAccessPorts.EBikeDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.RideDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.UserDA;
import sap.ass01.clean.infrastructure.DataAccessL.EBikeDB;
import sap.ass01.clean.infrastructure.DataAccessL.RideDB;
import sap.ass01.clean.infrastructure.DataAccessL.UserDB;
import sap.ass01.clean.infrastructure.Web.WebClient;
import sap.ass01.clean.infrastructure.Web.WebClientImpl;
import sap.ass01.clean.infrastructure.Web.WebController;

import java.util.Map;
import java.util.concurrent.*;

//100  - 1098 - 1087 - 1128
//500  - 2397 - 2147 - 2312
//1000 - 3231 - 3140 - 3375

public class ScalabilityTest {

    public static final int USERS = 1000;

    public static void main(String[] args) {
        WebClient webClient = new WebClientImpl();
        RideManager rideManager = new RideManagerImpl();
        UserDA userDA = new UserDB();
        EBikeDA eBikeDA = new EBikeDB();
        RideDA rideDA = new RideDB();
        AppManager app = new AppManagerImpl(rideManager, rideDA, eBikeDA, userDA);
        WebController webController = new WebController(app);
        CountDownLatch latch = new CountDownLatch(USERS);

        webController.start();

        ExecutorService executorService = Executors.newFixedThreadPool(USERS);
        Map<Integer, Long> map = new ConcurrentHashMap<>();
        map.put(1, 0L);

        for (int i = 0; i < USERS; i++) {
            executorService.submit(() -> {
                io.vertx.core.Future<Long> future = new WebCallTask(webClient).call();
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


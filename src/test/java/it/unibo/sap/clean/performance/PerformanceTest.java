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
import sap.ass01.clean.utils.Pair;
import sap.ass01.clean.utils.Triple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//741 - 729 - 887 - 760 - 805

public class PerformanceTest {

    private static final int OPERATIONS = 13;

    public static void main(String[] args) {
        WebClient webClient = new WebClientImpl();
        RideManager rideManager = new RideManagerImpl();
        UserDA userDA = new UserDB();
        EBikeDA eBikeDA = new EBikeDB();
        RideDA rideDA = new RideDB();
        AppManager app = new AppManagerImpl(rideManager, rideDA, eBikeDA, userDA);
        WebController webController = new WebController(app);
        CountDownLatch latch = new CountDownLatch(OPERATIONS);

        webController.start();

        ExecutorService executorService = Executors.newFixedThreadPool(OPERATIONS);
        Map<Integer, Long> map = new ConcurrentHashMap<>();
        map.put(1, 0L);

        for (int i = 1; i <= OPERATIONS; i++) {
            int finalI = i;
            executorService.submit(() -> {
                Future<Long> future = new WebCallTask(webClient, finalI).call();
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
        double averageResponseTime = (double) map.get(1) / OPERATIONS;
        System.out.println("Average Response Time: " + averageResponseTime + " ms");
        System.exit(0);
    }

    static class WebCallTask {
        private final WebClient webClient;
        private final Integer op;

        public WebCallTask(WebClient webClient, int op) {
            this.webClient = webClient;
            this.op = op;
        }

        public Future<Long> call() {
            long startTime = System.currentTimeMillis();
            Promise<Long> promise = Promise.promise();

            selectRequest(promise, startTime);

            return promise.future();
        }

        private void selectRequest(Promise<Long> promise, Long startTime) {
            switch (op) {
                case 1: {
                    Future<Map<Integer, Triple<String, Integer, Boolean>>> f = webClient.requestReadUser(0, "");
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 2: {
                    Future<Boolean> f = webClient.requestLogin("Grodone", "gpassword");
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 3: {
                    Future<Map<Integer, Triple<String, Integer, Boolean>>> f = webClient.requestReadUser(1, "");
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 4: {
                    Future<Map<Integer, Triple<String, Integer, Boolean>>> f = webClient.requestReadUser(0, "Grodone");
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 5: {
                    Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> f = webClient.requestReadEBike(0, 0, 0, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 6: {
                    Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> f = webClient.requestReadEBike(1, 0, 0, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 7: {
                    Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> f = webClient.requestReadEBike(0, 10, 10, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 8: {
                    Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> f = webClient.requestReadEBike(0, 0, 0, true);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 9: {
                    Future<Map<Integer, Pair<Pair<Integer, Integer>, Pair<String, String>>>> f = webClient.requestMultipleReadRide(0, 0, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 10: {
                    Future<Map<Integer, Pair<Pair<Integer, Integer>, Pair<String, String>>>> f = webClient.requestMultipleReadRide(1, 0, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 11: {
                    Future<Map<Integer, Pair<Pair<Integer, Integer>, Pair<String, String>>>> f = webClient.requestMultipleReadRide(0, 1, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 12: {
                    Future<Map<Integer, Pair<Pair<Integer, Integer>, Pair<String, String>>>> f = webClient.requestMultipleReadRide(0, 0, true);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
                case 13: {
                    Future<Map<Integer, Pair<Pair<Integer, Integer>, Pair<String, String>>>> f = webClient.requestMultipleReadRide(0, 0, false);
                    f.onComplete(x -> {
                        if (x.succeeded()) {
                            promise.complete(System.currentTimeMillis() - startTime);
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                }
            }
        }
    }

}

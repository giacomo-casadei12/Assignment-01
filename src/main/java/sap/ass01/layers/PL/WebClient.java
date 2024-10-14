package sap.ass01.layers.PL;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.http.WebSocket;
import sap.ass01.layers.BLL.Logic.Pair;
import sap.ass01.layers.BLL.Logic.Triple;
import sap.ass01.layers.BLL.Web.VertxSingleton;
import sap.ass01.layers.BLL.Web.WebOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WebClient {

    private static final Logger logger = Logger.getLogger(WebClient.class.getName());
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String USER_COMMAND_PATH = "/api/user/command";
    private static final String USER_QUERY_PATH = "/api/user/query";
    private static final String EBIKE_COMMAND_PATH = "/api/ebike/command";
    private static final String EBIKE_QUERY_PATH = "/api/ebike/query";
    private static final String RIDE_COMMAND_PATH = "/api/ride/command";
    private static final String RIDE_QUERY_PATH = "/api/ride/query";
    private static final String COUNT_UPDATE_PATH = "/api/count/update";
    private static final String COUNT_QUERY_PATH = "/api/count";

    private final io.vertx.ext.web.client.WebClient webClient;
    private final Vertx vertx;

    public WebClient() {
        vertx = VertxSingleton.getInstance().getVertx();
        WebClientOptions options = new WebClientOptions().setDefaultHost(SERVER_HOST).setDefaultPort(SERVER_PORT);
        webClient = io.vertx.ext.web.client.WebClient.create(vertx, options);
    }

    public Future<Boolean> requestCreateUser(String username, String password) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("User created: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to create user: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteUser(int userId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("User deleted: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to delete user: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestUpdateUser(int userId, int credit) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("credit", credit);
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("User updated: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to update user: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestLogin(String username, String password) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        requestPayload.put("operation", WebOperation.LOGIN.ordinal());

        webClient.get(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("Login: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.warning("Login failed: " + ar.result().bodyAsString());
                            promise.complete(false);
                        }
                    } else {
                        logger.severe("Failed to login: " + ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer,Triple<String,Integer,Boolean>>> requestReadUser(int userId, String username) {
        Promise<Map<Integer,Triple<String,Integer,Boolean>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer, Triple<String, Integer, Boolean>> retMap = new HashMap<>();
        if (userId > 0) {
            requestPayload.put("userId", userId);
        }
        if (!username.isBlank()) {
            requestPayload.put("username", username);
        }
        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.get(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        JsonObject res = ar.result().bodyAsJsonObject();
                        if (res.containsKey("result")) {
                            var resList = res.getJsonArray("result");
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                int resId = Integer.parseInt(jsonObj.getString("userId"));
                                var resUser = new Triple<>(jsonObj.getString("username"), Integer.parseInt(jsonObj.getString("credit")), Boolean.parseBoolean(jsonObj.getString("isAdmin")));
                                retMap.put(resId, resUser);
                            }
                            promise.complete(retMap);
                        } else if (res.containsKey("userId")) {
                            int resId = Integer.parseInt(res.getString("userId"));
                            var resUser = new Triple<>(res.getString("username"), Integer.parseInt(res.getString("credit")), Boolean.parseBoolean(res.getString("admin")));
                            retMap.put(resId, resUser);
                            promise.complete(retMap);
                        } else {
                            logger.warning("Error in response received from server");
                            promise.complete(null);
                        }
                    } else {
                        logger.severe("Failed to retrieve users: " + ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestCreateEBike(int x, int y) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("EBike created: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to create eBike: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteEBike(int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("eBike deleted: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to delete eBike: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestUpdateEBike(int eBikeId, int battery, String state, int x, int y) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        if (battery > 0) {
            requestPayload.put("battery", battery);
        }
        if (state != null) {
            requestPayload.put("state", state);
        }
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("eBike updated: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to update eBike: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> requestReadEBike(int eBikeId, int x, int y, boolean available) {
        Promise<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> retMap = new HashMap<>();
        if (eBikeId > 0) {
            requestPayload.put("eBikeId", eBikeId);
        } else {
            requestPayload.put("available", available);
        }
        if (x > 0) {
            requestPayload.put("x", x);
        }
        if (y > 0) {
            requestPayload.put("y", y);
        }

        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.get(EBIKE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("eBikes: " + ar.result().bodyAsString());
                        JsonObject res = ar.result().bodyAsJsonObject();
                        if (res.containsKey("result")) {
                            var resList = res.getJsonArray("result");
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                insertEBikeInMap(retMap, jsonObj);
                            }
                            promise.complete(retMap);
                        } else if (res.containsKey("eBikeId")) {
                            insertEBikeInMap(retMap, res);
                            promise.complete(retMap);
                        } else {
                            logger.warning("Error in response received from server");
                            promise.complete(null);
                        }
                    } else {
                        logger.severe("Failed to retrieve eBikes: " + ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestStartRide(int userId, int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded() && ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                        logger.info("ride started: " + ar.result().bodyAsString());
                        promise.complete(true);
                    } else {
                        logger.severe("Failed to start ride: " + ar.cause().getMessage());
                        promise.complete(false);
                    }
                });
        return promise.future();
    }

    public Future<Pair<Integer,Integer>> requestUpdateRide(int userId, int eBikeId, int x, int y) {
        Promise<Pair<Integer,Integer>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        var res = ar.result().bodyAsJsonObject();
                        if (res.containsKey("credit") && res.containsKey("battery")) {
                            logger.info("ride updated: " + ar.result().bodyAsString());
                            promise.complete(new Pair<>(Integer.parseInt(res.getString("credit")),
                                    Integer.parseInt(res.getString("battery"))));
                        } else {
                            promise.complete(null);
                        }
                    } else {
                        logger.severe("Failed to update ride: " + ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestEndRide(int userId, int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded() && ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                        logger.info("ride ended: " + ar.result().bodyAsString());
                        promise.complete(true);
                    } else {
                        logger.severe("Failed to end ride: " + ar.cause().getMessage());
                        promise.complete(false);
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteRide(int rideId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("rideId", rideId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue("result").toString().equals("ok")) {
                            logger.info("ride deleted: " + ar.result().bodyAsString());
                            promise.complete(true);
                        } else {
                            logger.severe("Failed to delete ride: " + ar.cause().getMessage());
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>>> requestMultipleReadRide(int userId, int eBikeId, boolean ongoing) {
        Promise<Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>> retMap = new HashMap<>();
        if (eBikeId > 0) {
            requestPayload.put("eBikeId", eBikeId);
        }
        if (userId > 0) {
            requestPayload.put("userId", userId);
        }
        if (ongoing) {
            requestPayload.put("ongoing", true);
        }
        requestPayload.put("multiple", true);
        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.get(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("rides: " + ar.result().bodyAsString());
                        var res = ar.result().bodyAsJsonObject();
                        if (res.containsKey("result")) {
                            var resList = res.getJsonArray("result");
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                int resId = Integer.parseInt(jsonObj.getString("rideId"));
                                var resUser = new Pair<>(new Pair<>(Integer.parseInt(jsonObj.getString("userId")),Integer.parseInt(jsonObj.getString("eBikeId"))),
                                        new Pair<>(jsonObj.getString("startDate"), jsonObj.getString("endDate")));
                                retMap.put(resId, resUser);
                            }
                            promise.complete(retMap);
                        }
                    } else {
                        logger.severe("Failed to retrieve rides: " + ar.cause().getMessage());
                    }
                });
        return promise.future();
    }

    public void requestSingleReadRide(int rideId, int userId) {
        JsonObject requestPayload = new JsonObject();
        if (rideId > 0) {
            requestPayload.put("rideId", rideId);
        }
        if (userId > 0) {
            requestPayload.put("userId", userId);
        }
        requestPayload.put("multiple", false);
        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.get(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to retrieve ride: " + ar.cause().getMessage());
                    }
                });
    }

    private void insertEBikeInMap(Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> retMap, JsonObject jsonObj) {
        int resId = Integer.parseInt(jsonObj.getString("eBikeId"));
        var resBike = new Triple<>(new Pair<>(Integer.parseInt(jsonObj.getString("x")), Integer.parseInt(jsonObj.getString("y"))),
                Integer.parseInt(jsonObj.getString("battery")), jsonObj.getString("status"));
        retMap.put(resId, resBike);
    }


    public void sendCountUpdate(int newCount) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("count", newCount);  // Send the count in the JSON body

        webClient.post(COUNT_UPDATE_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("Count update sent: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to send count update: " + ar.cause().getMessage());
                    }
                });
    }

    public Future<JsonObject> queryCount() {
        Promise<JsonObject> promise = Promise.promise();

        webClient.get(COUNT_QUERY_PATH)
                .send(ar -> {
                    if (ar.succeeded()) {
                        logger.info("Count queried: " + ar.result().bodyAsString());
                        JsonObject response = ar.result().bodyAsJsonObject();
                        promise.complete(response);
                    } else {
                        logger.severe("Failed to query count: " + ar.cause().getMessage());
                    }
                });
        return promise.future();
    }

    public void startMonitoringCountChanges(EBikeApp app) {
        vertx.createHttpClient().webSocket(SERVER_PORT, SERVER_HOST, "", asyncResult -> {
            if (asyncResult.succeeded()) {
                WebSocket webSocket = asyncResult.result();
                webSocket.handler(buffer -> {
                    String message = buffer.toString();
                    logger.info("Received message: " + message);
                });
                logger.info("WebSocket monitoring established for count changes.");
            } else {
                logger.severe("Failed to establish WebSocket monitoring: " + asyncResult.cause().getMessage());
            }
        });
    }

}

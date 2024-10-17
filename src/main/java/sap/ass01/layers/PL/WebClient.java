package sap.ass01.layers.PL;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.http.WebSocket;
import sap.ass01.layers.utils.Pair;
import sap.ass01.layers.utils.Triple;
import sap.ass01.layers.utils.VertxSingleton;
import sap.ass01.layers.utils.WebOperation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sap.ass01.layers.utils.JsonFieldsConstants.*;

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


    private final io.vertx.ext.web.client.WebClient client;
    private final Vertx vertx;

    public WebClient() {
        vertx = VertxSingleton.getInstance().getVertx();
        WebClientOptions options = new WebClientOptions().setDefaultHost(SERVER_HOST).setDefaultPort(SERVER_PORT);
        client = io.vertx.ext.web.client.WebClient.create(vertx, options);
    }

    public Future<Boolean> requestCreateUser(String username, String password) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USERNAME, username);
        requestPayload.put(PASSWORD, password);
        requestPayload.put(OPERATION, WebOperation.CREATE.ordinal());

        client.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("User created: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to create user: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteUser(int userId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USER_ID, userId);
        requestPayload.put(OPERATION, WebOperation.DELETE.ordinal());

        client.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("User deleted: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to delete user: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestUpdateUser(int userId, int credit) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USER_ID, userId);
        requestPayload.put(CREDIT, credit);
        requestPayload.put(OPERATION, WebOperation.UPDATE.ordinal());

        client.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("User updated: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to update user: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestLogin(String username, String password) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USERNAME, username);
        requestPayload.put(PASSWORD, password);
        requestPayload.put(OPERATION, WebOperation.LOGIN.ordinal());

        client.get(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("Login: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.warning("Login failed: " + ar.result().bodyAsString());
                            }
                            promise.complete(false);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to login: " + ar.cause().getMessage());
                        }
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer,Triple<String,Integer,Boolean>>> requestReadUser(int userId, String username) {
        Promise<Map<Integer,Triple<String,Integer,Boolean>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer, Triple<String, Integer, Boolean>> retMap = new ConcurrentHashMap<>();
        if (userId > 0) {
            requestPayload.put(USER_ID, userId);
        }
        if (!username.isBlank()) {
            requestPayload.put(USERNAME, username);
        }
        requestPayload.put(OPERATION, WebOperation.READ.ordinal());

        client.get(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        JsonObject res = ar.result().bodyAsJsonObject();
                        if (res.containsKey(RESULT)) {
                            var resList = res.getJsonArray(RESULT);
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                int resId = Integer.parseInt(jsonObj.getString(USER_ID));
                                var resUser = new Triple<>(jsonObj.getString(USERNAME), Integer.parseInt(jsonObj.getString(CREDIT)), Boolean.parseBoolean(jsonObj.getString("isAdmin")));
                                retMap.put(resId, resUser);
                            }
                            promise.complete(retMap);
                        } else if (res.containsKey(USER_ID)) {
                            int resId = Integer.parseInt(res.getString(USER_ID));
                            var resUser = new Triple<>(res.getString(USERNAME), Integer.parseInt(res.getString(CREDIT)), Boolean.parseBoolean(res.getString("admin")));
                            retMap.put(resId, resUser);
                            promise.complete(retMap);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.warning("Error in response received from server");
                            }
                            promise.complete(null);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to retrieve users: " + ar.cause().getMessage());
                        }
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestCreateEBike(int x, int y) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(POSITION_X, x);
        requestPayload.put(POSITION_Y, y);
        requestPayload.put(OPERATION, WebOperation.CREATE.ordinal());

        client.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("EBike created: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to create eBike: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteEBike(int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(E_BIKE_ID, eBikeId);
        requestPayload.put(OPERATION, WebOperation.DELETE.ordinal());

        client.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("eBike deleted: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to delete eBike: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestUpdateEBike(int eBikeId, int battery, String state, int x, int y) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(E_BIKE_ID, eBikeId);
        requestPayload.put(POSITION_X, x);
        requestPayload.put(POSITION_Y, y);
        if (battery > 0) {
            requestPayload.put(BATTERY, battery);
        }
        if (state != null) {
            requestPayload.put("state", state);
        }
        requestPayload.put(OPERATION, WebOperation.UPDATE.ordinal());

        client.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("eBike updated: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to update eBike: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> requestReadEBike(int eBikeId, int x, int y, boolean available) {
        Promise<Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> retMap = new ConcurrentHashMap<>();
        if (eBikeId > 0) {
            requestPayload.put(E_BIKE_ID, eBikeId);
        } else {
            requestPayload.put("available", available);
        }
        if (x > 0) {
            requestPayload.put(POSITION_X, x);
        }
        if (y > 0) {
            requestPayload.put(POSITION_Y, y);
        }

        requestPayload.put(OPERATION, WebOperation.READ.ordinal());

        client.get(EBIKE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.info("eBikes: " + ar.result().bodyAsString());
                        }
                        JsonObject res = ar.result().bodyAsJsonObject();
                        if (res.containsKey(RESULT)) {
                            var resList = res.getJsonArray(RESULT);
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                insertEBikeInMap(retMap, jsonObj);
                            }
                            promise.complete(retMap);
                        } else if (res.containsKey(E_BIKE_ID)) {
                            insertEBikeInMap(retMap, res);
                            promise.complete(retMap);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.warning("Error in response received from server");
                            }
                            promise.complete(null);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to retrieve eBikes: " + ar.cause().getMessage());
                        }
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestStartRide(int userId, int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USER_ID, userId);
        requestPayload.put(E_BIKE_ID, eBikeId);
        requestPayload.put(OPERATION, WebOperation.CREATE.ordinal());

        client.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded() && ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.info("ride started: " + ar.result().bodyAsString());
                        }
                        promise.complete(true);
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to start ride: " + ar.cause().getMessage());
                        }
                        promise.complete(false);
                    }
                });
        return promise.future();
    }

    public Future<Pair<Integer,Integer>> requestUpdateRide(int userId, int eBikeId, int x, int y) {
        Promise<Pair<Integer,Integer>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USER_ID, userId);
        requestPayload.put(E_BIKE_ID, eBikeId);
        requestPayload.put(POSITION_X, x);
        requestPayload.put(POSITION_Y, y);
        requestPayload.put(OPERATION, WebOperation.UPDATE.ordinal());

        client.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        var res = ar.result().bodyAsJsonObject();
                        if (res.containsKey(CREDIT) && res.containsKey(BATTERY)) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("ride updated: " + ar.result().bodyAsString());
                            }
                            promise.complete(new Pair<>(Integer.parseInt(res.getString(CREDIT)),
                                    Integer.parseInt(res.getString(BATTERY))));
                        } else {
                            promise.complete(null);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to update ride: " + ar.cause().getMessage());
                        }
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestEndRide(int userId, int eBikeId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put(USER_ID, userId);
        requestPayload.put(E_BIKE_ID, eBikeId);
        requestPayload.put(OPERATION, WebOperation.DELETE.ordinal());

        client.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded() && ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.info("ride ended: " + ar.result().bodyAsString());
                        }
                        promise.complete(true);
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to end ride: " + ar.cause().getMessage());
                        }
                        promise.complete(false);
                    }
                });
        return promise.future();
    }

    public Future<Boolean> requestDeleteRide(int rideId) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("rideId", rideId);
        requestPayload.put(OPERATION, WebOperation.DELETE.ordinal());

        client.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().bodyAsJsonObject().getValue(RESULT).toString().equals("ok")) {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.info("ride deleted: " + ar.result().bodyAsString());
                            }
                            promise.complete(true);
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.severe("Failed to delete ride: " + ar.cause().getMessage());
                            }
                            promise.complete(false);
                        }
                    }
                });
        return promise.future();
    }

    public Future<Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>>> requestMultipleReadRide(int userId, int eBikeId, boolean ongoing) {
        Promise<Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>>> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>> retMap = new ConcurrentHashMap<>();
        if (eBikeId > 0) {
            requestPayload.put(E_BIKE_ID, eBikeId);
        }
        if (userId > 0) {
            requestPayload.put(USER_ID, userId);
        }
        if (ongoing) {
            requestPayload.put("ongoing", true);
        }
        requestPayload.put("multiple", true);
        requestPayload.put(OPERATION, WebOperation.READ.ordinal());

        client.get(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.info("rides: " + ar.result().bodyAsString());
                        }
                        var res = ar.result().bodyAsJsonObject();
                        if (res.containsKey(RESULT)) {
                            var resList = res.getJsonArray(RESULT);
                            var it = resList.stream().iterator();
                            while (it.hasNext()) {
                                var jsonObj = (JsonObject) it.next();
                                int resId = Integer.parseInt(jsonObj.getString("rideId"));
                                var resUser = new Pair<>(new Pair<>(Integer.parseInt(jsonObj.getString(USER_ID)),Integer.parseInt(jsonObj.getString(E_BIKE_ID))),
                                        new Pair<>(jsonObj.getString("startDate"), jsonObj.getString("endDate")));
                                retMap.put(resId, resUser);
                            }
                            promise.complete(retMap);
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to retrieve rides: " + ar.cause().getMessage());
                        }
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
            requestPayload.put(USER_ID, userId);
        }
        requestPayload.put("multiple", false);
        requestPayload.put(OPERATION, WebOperation.READ.ordinal());

        client.get(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.info("ride: " + ar.result().bodyAsString());
                        }
                    } else {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.severe("Failed to retrieve ride: " + ar.cause().getMessage());
                        }
                    }
                });
    }

    public void startMonitoringEBike(EBikeApp app) {
        vertx.createHttpClient().webSocket(SERVER_PORT, SERVER_HOST, "/api/ebikes/monitoring", asyncResult -> {
            if (asyncResult.succeeded()) {
                WebSocket webSocket = asyncResult.result();
                webSocket.handler(buffer -> {
                    String message = buffer.toString();
                    JsonObject jsonMessage = new JsonObject(message);
                    if (jsonMessage.containsKey("event") &&
                            jsonMessage.getString("event").equals("ebike-changed") &&
                                jsonMessage.containsKey(E_BIKE_ID) && jsonMessage.containsKey(POSITION_X) &&
                                    jsonMessage.containsKey(POSITION_Y) && jsonMessage.containsKey(BATTERY) && jsonMessage.containsKey("status")){
                                int eBikeId = Integer.parseInt(jsonMessage.getString(E_BIKE_ID));
                                int x = Integer.parseInt(jsonMessage.getString(POSITION_X));
                                int y = Integer.parseInt(jsonMessage.getString(POSITION_Y));
                                int battery = Integer.parseInt(jsonMessage.getString(BATTERY));
                                String status = jsonMessage.getString("status");
                                app.updateEBikeFromEventbus(eBikeId, x, y, battery, status);
                    }
                    if (logger.isLoggable(Level.FINE)) {
                        logger.info("Received message: " + message);
                    }
                });
                if (logger.isLoggable(Level.FINE)) {
                    logger.info("WebSocket monitoring established for users changes.");
                }
            } else {
                if (logger.isLoggable(Level.FINE)) {
                    logger.severe("Failed to establish WebSocket users monitoring: " + asyncResult.cause().getMessage());
                }
            }
        });
    }

    private void insertEBikeInMap(Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> retMap, JsonObject jsonObj) {
        int resId = Integer.parseInt(jsonObj.getString(E_BIKE_ID));
        var resBike = new Triple<>(new Pair<>(Integer.parseInt(jsonObj.getString(POSITION_X)), Integer.parseInt(jsonObj.getString(POSITION_Y))),
                Integer.parseInt(jsonObj.getString(BATTERY)), jsonObj.getString("status"));
        retMap.put(resId, resBike);
    }

}

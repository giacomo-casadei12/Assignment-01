package sap.ass01.layers.PL;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.http.WebSocket;
import sap.ass01.layers.BLL.Web.VertxSingleton;
import sap.ass01.layers.BLL.Web.WebOperation;

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

    public void requestCreateUser(String username, String password) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("User created: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to create user: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestDeleteUser(int userId) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("User deleted: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to delete user: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestUpdateUser(int userId, int credit) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("credit", credit);
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(USER_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("User updated: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to update user: " + ar.cause().getMessage());
                    }
                });
    }

    public Future<Boolean> requestLogin(String username, String password) {
        Promise<Boolean> promise = Promise.promise();
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        requestPayload.put("operation", WebOperation.LOGIN.ordinal());

        webClient.post(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("Login: " + ar.result().bodyAsString());
                        promise.complete(true);
                    } else {
                        logger.severe("Failed to login: " + ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }

    public void requestReadUser(int userId) {
        JsonObject requestPayload = new JsonObject();
        if (userId > 0) {
            requestPayload.put("userId", userId);
        }
        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.post(USER_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("Users: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to retrieve users: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestCreateEBike(int x, int y) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("EBike created: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to create eBike: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestDeleteEBike(int eBikeId) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("eBike deleted: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to delete eBike: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestUpdateEBike(int eBikeId, int x, int y) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(EBIKE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("eBike updated: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to update eBike: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestReadEBike(int eBikeId, int x, int y, boolean available) {
        JsonObject requestPayload = new JsonObject();
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

        webClient.post(EBIKE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("eBikes: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to retrieve eBikes: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestStartRide(int userId, int eBikeId) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.CREATE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride started: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to start ride: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestUpdateRide(int userId, int eBikeId, int x, int y) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("x", x);
        requestPayload.put("y", y);
        requestPayload.put("operation", WebOperation.UPDATE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride updated: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to update ride: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestEndRide(int userId, int eBikeId) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("userId", userId);
        requestPayload.put("eBikeId", eBikeId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride ended: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to end ride: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestDeleteRide(int rideId) {
        JsonObject requestPayload = new JsonObject();
        requestPayload.put("rideId", rideId);
        requestPayload.put("operation", WebOperation.DELETE.ordinal());

        webClient.post(RIDE_COMMAND_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride deleted: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to delete ride: " + ar.cause().getMessage());
                    }
                });
    }

    public void requestMultipleReadRide(int eBikeId, int userId, boolean ongoing) {
        JsonObject requestPayload = new JsonObject();
        if (eBikeId > 0) {
            requestPayload.put("eBikeId", eBikeId);
        }
        if (userId > 0) {
            requestPayload.put("userId", userId);
        }
        requestPayload.put("ongoing", ongoing);
        requestPayload.put("multiple", true);
        requestPayload.put("operation", WebOperation.READ.ordinal());

        webClient.post(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("rides: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to retrieve rides: " + ar.cause().getMessage());
                    }
                });
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

        webClient.post(RIDE_QUERY_PATH)
                .sendJson(requestPayload, ar -> {
                    if (ar.succeeded()) {
                        logger.info("ride: " + ar.result().bodyAsString());
                    } else {
                        logger.severe("Failed to retrieve ride: " + ar.cause().getMessage());
                    }
                });
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

    public void startMonitoringCountChanges() {
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

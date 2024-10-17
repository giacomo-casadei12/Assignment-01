package sap.ass01.layers.BLL.Web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sap.ass01.layers.utils.Pair;
import sap.ass01.layers.BLL.Logic.RideManager;
import sap.ass01.layers.BLL.Logic.RideManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManager;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.utils.EBikeState;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.User;
import sap.ass01.layers.utils.VertxSingleton;
import sap.ass01.layers.utils.WebOperation;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sap.ass01.layers.utils.JsonFieldsConstants.*;

public class WebController extends AbstractVerticle {

    private final int port;
    private static final Logger logger = Logger.getLogger("[EBikeCesena]");
    private static final String BIKE_CHANGE_EVENT_TOPIC = "user-Change";
    final Vertx vertx;
    final private RideManager rManager;
    final private PersistenceManager pManager;

    public WebController() {
        this.port = 8080;
        logger.setLevel(Level.INFO);
        this.pManager = new PersistenceManagerImpl();
        this.rManager = new RideManagerImpl(this.pManager);
        vertx = VertxSingleton.getInstance().getVertx();
        vertx.deployVerticle(this);
    }

    @Override
    public void start() {
        logger.log(Level.INFO, "Web server initializing...");
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/api/user/command").handler(this::processServiceUserCmd);
        router.route(HttpMethod.GET, "/api/user/query").handler(this::processServiceUserQuery);
        router.route(HttpMethod.POST, "/api/ebike/command").handler(this::processServiceEBikeCmd);
        router.route(HttpMethod.GET, "/api/ebike/query").handler(this::processServiceEBikeQuery);
        router.route(HttpMethod.POST, "/api/ride/command").handler(this::processServiceRideCmd);
        router.route(HttpMethod.GET, "/api/ride/query").handler(this::processServiceRideQuery);

        server.webSocketHandler(webSocket -> {
            if (webSocket.path().equals("/api/ebikes/monitoring")) {
                webSocket.accept();
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.INFO, "New ebikes monitoring observer registered.");
                }
                EventBus eb = vertx.eventBus();
                eb.consumer(BIKE_CHANGE_EVENT_TOPIC, msg -> {
                    JsonObject ev = (JsonObject) msg.body();
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.INFO, "ebikes changed: " + ev.encodePrettily());
                    }
                    webSocket.writeTextMessage(ev.encodePrettily());
                });
            } else {
                logger.log(Level.INFO, "Ebikes monitoring observer rejected.");
                webSocket.reject();
            }
        });

        server
                .requestHandler(router)
                .listen(port);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "EBikeCesena web server ready on port: " + port);
        }

    }

    protected void processServiceUserCmd(RoutingContext context) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "New request - user cmd " + context.currentRoute().getPath());
        }
        new Thread(() -> {
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                boolean b = false;
                switch (op) {
                    case CREATE:  {
                        if (requestBody.containsKey(USERNAME) && requestBody.containsKey(PASSWORD)) {
                            String username = requestBody.getString(USERNAME);
                            String password = requestBody.getString(PASSWORD);
                            b = pManager.createUser(username, password);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case UPDATE:  {
                        if (requestBody.containsKey(USER_ID) && requestBody.containsKey(CREDIT)) {
                            int id = requestBody.getInteger(USER_ID);
                            int credit = requestBody.getInteger(CREDIT);
                            b = pManager.updateUser(id,credit);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case DELETE:  {
                        if (requestBody.containsKey(USER_ID)) {
                            int id = requestBody.getInteger(USER_ID);
                            b = pManager.deleteUser(id);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    default: invalidJSONReply(context,requestBody);
                }
                checkResponseAndSendReply(context, b);
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    protected void processServiceUserQuery(RoutingContext context) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "New request - user query " + context.currentRoute().getPath());
        }
        new Thread(() -> {
            // Parse the JSON body
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                boolean b;
                User u;
                List<User> users;
                if (op == WebOperation.LOGIN) {
                    if (requestBody.containsKey(USERNAME) && requestBody.containsKey(PASSWORD)) {
                        String username = requestBody.getString(USERNAME);
                        String password = requestBody.getString(PASSWORD);
                        b = pManager.login(username, password);
                        checkResponseAndSendReply(context, b);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                } else if(op == WebOperation.READ) {
                        if (requestBody.containsKey(USER_ID) || requestBody.containsKey(USERNAME)) {
                            int id = requestBody.containsKey(USER_ID) ? requestBody.getInteger(USER_ID) : 0;
                            String username = requestBody.containsKey(USERNAME) ? requestBody.getString(USERNAME) : "";
                            u = pManager.getUser(id,username);
                            var map = new HashMap<String, Object>();
                            map.put(USER_ID, u.ID());
                            map.put(USERNAME, u.userName());
                            map.put(CREDIT, u.credit());
                            map.put("admin", u.admin());
                            composeJSONAndSendReply(context,map);
                        } else {
                            users = pManager.getAllUsers();
                            var array = new ArrayList<Map<String,Object>>();
                            for (User user : users) {
                                var map = new HashMap<String, Object>();
                                map.put(USER_ID, user.ID());
                                map.put(USERNAME, user.userName());
                                map.put(CREDIT, user.credit());
                                map.put("admin", user.admin());
                                array.add(map);
                            }
                            composeJSONArrayAndSendReply(context,array);
                        }
                }
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    protected void processServiceEBikeCmd(RoutingContext context) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.INFO, "New request - ebike cmd " + context.currentRoute().getPath());
        }
        new Thread(() -> {
            // Parse the JSON body
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                boolean b = false;
                switch (op) {
                    case CREATE:  {
                        if (requestBody.containsKey(POSITION_X) && requestBody.containsKey(POSITION_Y)) {
                            int x = requestBody.getInteger(POSITION_X);
                            int y = requestBody.getInteger(POSITION_Y);
                            b = pManager.createEBike(x, y);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case UPDATE:  {
                        if (requestBody.containsKey(E_BIKE_ID) && requestBody.containsKey(POSITION_X) && requestBody.containsKey(POSITION_Y) && !requestBody.containsKey(BATTERY)) {
                            int id = requestBody.getInteger(E_BIKE_ID);
                            int x = requestBody.getInteger(POSITION_X);
                            int y = requestBody.getInteger(POSITION_Y);
                            b = pManager.updateEbikePosition(id,x,y);
                        } else if (requestBody.containsKey(E_BIKE_ID) && requestBody.containsKey(BATTERY) && requestBody.containsKey(STATE)) {
                            int id = requestBody.getInteger(E_BIKE_ID);
                            int battery = requestBody.getInteger(BATTERY);
                            EBikeState state = EBikeState.valueOf(requestBody.getString(STATE));
                            int x = requestBody.getInteger(POSITION_X);
                            int y = requestBody.getInteger(POSITION_Y);
                            b = pManager.updateEBike(id,battery,state, x, y);
                            if (b) {
                                notifyEBikeChanged(id, x, y, battery, state.toString());
                            }
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case DELETE:  {
                        if (requestBody.containsKey(E_BIKE_ID)) {
                            int id = requestBody.getInteger(E_BIKE_ID);
                            b = pManager.deleteEBike(id);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    default: invalidJSONReply(context,requestBody);
                }
                checkResponseAndSendReply(context, b);
                /*notifyEBikeChanged(newCount);*/
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    protected void processServiceEBikeQuery(RoutingContext context) {
        logger.log(Level.INFO, "New request - ebike query " + context.currentRoute().getPath());
            new Thread(() -> {
            // Parse the JSON body
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                EBike eb;
                List<EBike> bikes;
                if (Objects.requireNonNull(op) == WebOperation.READ) {
                    if (requestBody.containsKey(E_BIKE_ID)) {
                        int id = requestBody.getInteger(E_BIKE_ID);
                        eb = pManager.getEBike(id);
                        var map = buildEBikeMap(eb);
                        composeJSONAndSendReply(context,map);
                    } else {
                        if (requestBody.containsKey(POSITION_X) || requestBody.containsKey(POSITION_Y)) {
                            int x = requestBody.containsKey(POSITION_X) ? requestBody.getInteger(POSITION_X) : 0;
                            int y = requestBody.containsKey(POSITION_Y) ? requestBody.getInteger(POSITION_Y) : 0;
                            bikes = pManager.getAllEBikes(x,y,false);
                        } else if (requestBody.containsKey("available")) {
                            boolean avail = requestBody.getBoolean("available");
                            bikes = pManager.getAllEBikes(0,0,avail);
                        } else {
                            bikes = pManager.getAllEBikes(0,0,false);
                        }
                        var array = new ArrayList<Map<String,Object>>();
                        for (EBike eBike : bikes) {
                            var map = buildEBikeMap(eBike);
                            array.add(map);
                        }
                        composeJSONArrayAndSendReply(context,array);
                    }
                } else {
                    invalidJSONReply(context,requestBody);
                }
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    protected void processServiceRideCmd(RoutingContext context) {
        logger.log(Level.INFO, "New request - ride cmd " + context.currentRoute().getPath());
        new Thread(() -> {
            // Parse the JSON body
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                boolean b;
                switch (op) {
                    case CREATE:  {
                        if (requestBody.containsKey(USER_ID) && requestBody.containsKey(E_BIKE_ID)) {
                            int userId = requestBody.getInteger(USER_ID);
                            int eBikeId = requestBody.getInteger(E_BIKE_ID);
                            b = rManager.startRide(userId, eBikeId);
                            this.checkBikeChangesAndNotifyAll(eBikeId);
                            checkResponseAndSendReply(context, b);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case UPDATE:  {
                        if (requestBody.containsKey(USER_ID) && requestBody.containsKey(E_BIKE_ID) &&
                                requestBody.containsKey(POSITION_X) && requestBody.containsKey(POSITION_Y)) {
                            int userId = requestBody.getInteger(USER_ID);
                            int eBikeId = requestBody.getInteger(E_BIKE_ID);
                            int x = requestBody.getInteger(POSITION_X);
                            int y = requestBody.getInteger(POSITION_Y);
                            Pair<Integer, Integer> p = rManager.updateRide(userId,eBikeId,x,y);
                            this.checkBikeChangesAndNotifyAll(eBikeId);
                            var map = new HashMap<String, Object>();
                            map.put(CREDIT, p.first());
                            map.put(BATTERY, p.second());
                            composeJSONAndSendReply(context,map);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    case DELETE:  {
                        if (requestBody.containsKey(USER_ID) && requestBody.containsKey(E_BIKE_ID)) {
                            int userId = requestBody.getInteger(USER_ID);
                            int eBikeId = requestBody.getInteger(E_BIKE_ID);
                            b = rManager.endRide(userId,eBikeId);
                            this.checkBikeChangesAndNotifyAll(eBikeId);
                            checkResponseAndSendReply(context, b);
                        } else if (requestBody.containsKey(RIDE_ID)) {
                            int rideId = requestBody.getInteger(RIDE_ID);
                            b = pManager.deleteRide(rideId);
                            checkResponseAndSendReply(context, b);
                        } else {
                            invalidJSONReply(context,requestBody);
                        }
                        break;
                    }
                    default: invalidJSONReply(context,requestBody);
                }
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    protected void processServiceRideQuery(RoutingContext context) {
        logger.log(Level.INFO, "New request - ride query " + context.currentRoute().getPath());
        new Thread(() -> {
            // Parse the JSON body
            JsonObject requestBody = context.body().asJsonObject();
            if (requestBody != null && requestBody.containsKey(OPERATION)) {
                WebOperation op = WebOperation.values()[requestBody.getInteger(OPERATION)];
                Ride r;
                List<Ride> rides;
                if (Objects.requireNonNull(op) == WebOperation.READ) {
                    if (requestBody.containsKey("multiple")) {
                        if (requestBody.containsKey("ongoing")) {
                            boolean ongoing = requestBody.getBoolean("ongoing");
                            rides = pManager.getAllRides(ongoing,0,0);
                        } else if (requestBody.containsKey(USER_ID)) {
                            int userId = requestBody.getInteger(USER_ID);
                            rides = pManager.getAllRides(false,userId,0);
                        } else if (requestBody.containsKey(E_BIKE_ID)) {
                            int eBikeId = requestBody.getInteger(E_BIKE_ID);
                            rides = pManager.getAllRides(false,0,eBikeId);
                        } else {
                            rides = pManager.getAllRides(false,0,0);
                        }
                        var array = new ArrayList<Map<String,Object>>();
                        for (Ride ride : rides) {
                            var map = buildRideMap(ride);
                            array.add(map);
                        }
                        composeJSONArrayAndSendReply(context,array);
                    } else {
                        if (requestBody.containsKey(RIDE_ID)) {
                            int rideId = requestBody.getInteger(RIDE_ID);
                            r = pManager.getRide(rideId, 0);
                            var array = new ArrayList<Map<String,Object>>();
                            var map = buildRideMap(r);
                            array.add(map);
                            composeJSONArrayAndSendReply(context,array);
                        } else if (requestBody.containsKey(USER_ID)) {
                            int userId = requestBody.getInteger(USER_ID);
                            r = pManager.getRide(0, userId);
                            var array = new ArrayList<Map<String,Object>>();
                            var map = buildRideMap(r);
                            array.add(map);
                            composeJSONArrayAndSendReply(context,array);
                        } else {
                            invalidJSONReply(context, requestBody);
                        }
                    }
                } else {
                    invalidJSONReply(context,requestBody);
                }
            } else {
                invalidJSONReply(context,requestBody);
            }
        }).start();
    }

    private void checkResponseAndSendReply(RoutingContext context, boolean b) {
        JsonObject reply = new JsonObject();
        if (b) {
            reply.put(RESULT, "ok");
        } else {
            reply.put(RESULT, "error");
        }
        sendReply(context, reply);
    }

    private void composeJSONAndSendReply(RoutingContext context, Map<String,Object> body) {
        JsonObject reply = composeJSONFromFieldsMap(body);
        sendReply(context, reply);
    }

    private void composeJSONArrayAndSendReply(RoutingContext context, List<Map<String,Object>> body) {
        JsonObject reply = new JsonObject();
        JsonArray replyArray = new JsonArray();
        for (Map<String,Object> map : body) {
            JsonObject json = composeJSONFromFieldsMap(map);
            replyArray.add(json);
        }
        reply.put(RESULT, replyArray);
        sendReply(context, reply);
    }

    private JsonObject composeJSONFromFieldsMap(Map<String, Object> body) {
        JsonObject reply = new JsonObject();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            String key = entry.getKey();
            reply.put(key, entry.getValue().toString());
        }
        return reply;
    }

    private void invalidJSONReply(RoutingContext context, JsonObject requestBody) {
        logger.warning("Received invalid JSON payload: " + requestBody);
        JsonObject reply = new JsonObject();
        reply.put(RESULT, "not ok");
        sendReply(context, reply);
    }

    private Map<String, Object> buildEBikeMap(EBike eb) {
        var map = new HashMap<String, Object>();
        map.put(E_BIKE_ID, eb.ID());
        map.put(POSITION_X, eb.positionX());
        map.put(POSITION_Y, eb.positionY());
        map.put(BATTERY, eb.battery());
        map.put("status", eb.state());
        return map;
    }

    private Map<String, Object> buildRideMap(Ride r) {
        var map = new HashMap<String, Object>();
        map.put(RIDE_ID, r.ID());
        map.put(USER_ID, r.userID());
        map.put(E_BIKE_ID, r.eBikeID());
        map.put("startDate", r.startDate());
        map.put("endDate", r.endDate() == null ? "" : r.endDate());
        return map;
    }

    private void checkBikeChangesAndNotifyAll(int eBikeId) {
        var bike = this.pManager.getEBike(eBikeId);
        this.notifyEBikeChanged(eBikeId, bike.positionX(), bike.positionY(), bike.battery(), bike.state());
    }

    private void notifyEBikeChanged(int eBikeId, int x, int y, int battery, String status) {
        logger.log(Level.INFO, "notify ebike changed");
        EventBus eb = vertx.eventBus();

        JsonObject obj = new JsonObject();
        obj.put("event", "ebike-changed");
        obj.put(E_BIKE_ID, eBikeId);
        obj.put(POSITION_X, x);
        obj.put(POSITION_Y, y);
        obj.put(BATTERY, battery);
        obj.put("status", status);
        eb.publish(BIKE_CHANGE_EVENT_TOPIC, obj);
    }

    private void sendReply(RoutingContext request, JsonObject reply) {
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "application/json");
        response.end(reply.toString());
    }

}

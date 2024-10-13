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
import sap.ass01.layers.BLL.Logic.Pair;
import sap.ass01.layers.BLL.Logic.RideManager;
import sap.ass01.layers.BLL.Logic.RideManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManager;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.User;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebController extends AbstractVerticle {

    private final int port;
    static Logger logger = Logger.getLogger("[EBikeCesena]");
    private static final String EBIKE_MOVEMENT_EVENT_TOPIC = "bike-Moved";
    Vertx vertx;
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

    public void start() {
        logger.log(Level.INFO, "Web server initializing...");
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        /* static files by default searched in "webroot" directory */
        router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/api/user/command").handler(this::processServiceUserCmd);
        router.route(HttpMethod.GET, "/api/user/query").handler(this::processServiceUserQuery);
        router.route(HttpMethod.POST, "/api/ebike/command").handler(this::processServiceEBikeCmd);
        router.route(HttpMethod.GET, "/api/ebike/query").handler(this::processServiceEBikeQuery);
        router.route(HttpMethod.POST, "/api/ride/command").handler(this::processServiceRideCmd);
        router.route(HttpMethod.GET, "/api/ride/query").handler(this::processServiceRideQuery);

        server.webSocketHandler(webSocket -> {
            if (webSocket.path().equals("/api/ebike/monitoring")) {
                webSocket.accept();
                logger.log(Level.INFO, "New ebike monitoring observer registered.");
                EventBus eb = vertx.eventBus();
                eb.consumer(EBIKE_MOVEMENT_EVENT_TOPIC, msg -> {
                    JsonObject ev = (JsonObject) msg.body();
                    logger.log(Level.INFO, "Bike moved: " + ev.encodePrettily());
                    webSocket.writeTextMessage(ev.encodePrettily());
                });
            } else {
                logger.log(Level.INFO, "Ebike monitoring observer rejected.");
                webSocket.reject();
            }
        });

        server
                .requestHandler(router)
                .listen(port);

        logger.log(Level.INFO, "EBikeCesena web server ready on port: " + port);

    }

    protected void processServiceUserCmd(RoutingContext context) {
        logger.log(Level.INFO, "New request - user cmd " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            boolean b = false;
            switch (op) {
                case CREATE:  {
                    if (requestBody.containsKey("username") && requestBody.containsKey("password")) {
                        String username = requestBody.getString("username");
                        String password = requestBody.getString("password");
                        b = pManager.createUser(username, password);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case UPDATE:  {
                    if (requestBody.containsKey("userId") && requestBody.containsKey("credit")) {
                        int id = requestBody.getInteger("userId");
                        int credit = requestBody.getInteger("credit");
                        b = pManager.updateUser(id,credit);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case DELETE:  {
                    if (requestBody.containsKey("userId")) {
                        int id = requestBody.getInteger("userId");
                        b = pManager.deleteUser(id);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
            }
            checkResponseAndSendReply(context, b);
            /*notifyCountChanged(newCount);*/
        } else {
            invalidJSONReply(context,requestBody);
        }
    }

    protected void processServiceUserQuery(RoutingContext context) {
        logger.log(Level.INFO, "New request - user query " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            boolean b;
            User u;
            List<User> users;
            switch (op) {
                case LOGIN:  {
                    if (requestBody.containsKey("username") && requestBody.containsKey("password")) {
                        String username = requestBody.getString("username");
                        String password = requestBody.getString("password");
                        b = pManager.login(username, password);
                        checkResponseAndSendReply(context, b);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case READ:  {
                    if (requestBody.containsKey("userId") || requestBody.containsKey("username")) {
                        int id = requestBody.containsKey("userId") ? requestBody.getInteger("userId") : 0;
                        String username = requestBody.containsKey("username") ? requestBody.getString("username") : "";
                        u = pManager.getUser(id,username);
                        var map = new HashMap<String, Object>();
                        map.put("userId", u.getID());
                        map.put("username", u.getName());
                        map.put("credit", u.getCredit());
                        map.put("admin", u.isAdmin());
                        composeJSONAndSendReply(context,map);
                    } else {
                        users = pManager.getAllUsers();
                        var array = new ArrayList<Map<String,Object>>();
                        for (User user : users) {
                            var map = new HashMap<String, Object>();
                            map.put("userId", user.getID());
                            map.put("username", user.getName());
                            map.put("credit", user.getCredit());
                            map.put("admin", user.isAdmin());
                            array.add(map);
                        }
                        composeJSONArrayAndSendReply(context,array);
                    }
                    break;
                }
            }
        } else {
            invalidJSONReply(context,requestBody);
        }
    }

    protected void processServiceEBikeCmd(RoutingContext context) {
        logger.log(Level.INFO, "New request - ebike cmd " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            boolean b = false;
            switch (op) {
                case CREATE:  {
                    if (requestBody.containsKey("x") && requestBody.containsKey("y")) {
                        int x = requestBody.getInteger("x");
                        int y = requestBody.getInteger("y");
                        b = pManager.createEBike(x, y);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case UPDATE:  {
                    if (requestBody.containsKey("eBikeId") && requestBody.containsKey("x") && requestBody.containsKey("y")) {
                        int id = requestBody.getInteger("eBikeId");
                        int x = requestBody.getInteger("x");
                        int y = requestBody.getInteger("y");
                        b = pManager.updateEbikePosition(id,x,y);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case DELETE:  {
                    if (requestBody.containsKey("eBikeId")) {
                        int id = requestBody.getInteger("userId");
                        b = pManager.deleteEBike(id);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
            }
            checkResponseAndSendReply(context, b);
            /*notifyCountChanged(newCount);*/
        } else {
            invalidJSONReply(context,requestBody);
        }
    }

    protected void processServiceEBikeQuery(RoutingContext context) {
        logger.log(Level.INFO, "New request - ebike query " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            EBike eb;
            List<EBike> bikes;
            if (Objects.requireNonNull(op) == WebOperation.READ) {
                if (requestBody.containsKey("eBikeId")) {
                    int id = requestBody.getInteger("eBikeId");
                    eb = pManager.getEBike(id);
                    var map = buildEBikeMap(eb);
                    composeJSONAndSendReply(context,map);
                } else {
                    if (requestBody.containsKey("x") || requestBody.containsKey("y")) {
                        int x = requestBody.containsKey("x") ? requestBody.getInteger("x") : 0;
                        int y = requestBody.containsKey("y") ? requestBody.getInteger("y") : 0;
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
    }

    private Map<String, Object> buildEBikeMap(EBike eb) {
        var map = new HashMap<String, Object>();
        map.put("eBikeId", eb.getID());
        map.put("x", eb.getPositionX());
        map.put("y", eb.getPositionY());
        map.put("battery", eb.getBattery());
        map.put("status", eb.getState().toString());
        return map;
    }

    protected void processServiceRideCmd(RoutingContext context) {
        logger.log(Level.INFO, "New request - ride cmd " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            boolean b = false;
            switch (op) {
                case CREATE:  {
                    if (requestBody.containsKey("userId") && requestBody.containsKey("eBikeId")) {
                        int userId = requestBody.getInteger("userId");
                        int eBikeId = requestBody.getInteger("eBikeId");
                        b = rManager.startRide(userId, eBikeId);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case UPDATE:  {
                    if (requestBody.containsKey("userId") && requestBody.containsKey("eBikeId") &&
                            requestBody.containsKey("x") && requestBody.containsKey("y")) {
                        int userId = requestBody.getInteger("userId");
                        int eBikeId = requestBody.getInteger("eBikeId");
                        int x = requestBody.getInteger("x");
                        int y = requestBody.getInteger("y");
                        Pair<Integer, Integer> p = rManager.updateRide(userId,eBikeId,x,y);
                        b = true;
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
                case DELETE:  {
                    if (requestBody.containsKey("userId") && requestBody.containsKey("eBikeId")) {
                        int userId = requestBody.getInteger("userId");
                        int eBikeId = requestBody.getInteger("eBikeId");
                        b = rManager.endRide(userId,eBikeId);
                    } else if (requestBody.containsKey("rideId")) {
                        int rideId = requestBody.getInteger("rideId");
                        b = pManager.deleteRide(rideId);
                    } else {
                        invalidJSONReply(context,requestBody);
                    }
                    break;
                }
            }
            checkResponseAndSendReply(context, b);
            /*notifyCountChanged(newCount);*/
        } else {
            invalidJSONReply(context,requestBody);
        }
    }

    protected void processServiceRideQuery(RoutingContext context) {
        logger.log(Level.INFO, "New request - ride query " + context.currentRoute().getPath());
        // Parse the JSON body
        JsonObject requestBody = context.body().asJsonObject();
        if (requestBody != null && requestBody.containsKey("operation")) {
            WebOperation op = WebOperation.values()[requestBody.getInteger("operation")];
            boolean b = false;
            Ride r;
            List<Ride> rides;
            if (Objects.requireNonNull(op) == WebOperation.READ) {
                if (requestBody.containsKey("multiple")) {
                    if (requestBody.containsKey("ongoing")) {
                        boolean ongoing = requestBody.getBoolean("ongoing");
                        rides = pManager.getAllRides(ongoing,0,0);
                    } else if (requestBody.containsKey("userId")) {
                        int userId = requestBody.getInteger("userId");
                        rides = pManager.getAllRides(false,userId,0);
                    } else if (requestBody.containsKey("eBikeId")) {
                        int eBikeId = requestBody.getInteger("eBikeId");
                        rides = pManager.getAllRides(false,0,eBikeId);
                    } else {
                        rides = pManager.getAllRides(false,0,0);
                    }
                } else {
                    if (requestBody.containsKey("rideId")) {
                        int rideId = requestBody.getInteger("rideId");
                        r = pManager.getRide(rideId, 0);
                    } else if (requestBody.containsKey("userId")) {
                        int userId = requestBody.getInteger("userId");
                        r = pManager.getRide(0, userId);
                    } else {
                        invalidJSONReply(context, requestBody);
                    }
                }
                b = true;
            } else {
                invalidJSONReply(context,requestBody);
            }
            checkResponseAndSendReply(context, b);
        } else {
            invalidJSONReply(context,requestBody);
        }
    }

    private void checkResponseAndSendReply(RoutingContext context, boolean b) {
        JsonObject reply = new JsonObject();
        if (b) {
            reply.put("result", "ok");
        } else {
            reply.put("result", "error");
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
        reply.put("result", replyArray);
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
        reply.put("result", "not ok");
        sendReply(context, reply);
    }

    //notifyCountChanged(0);

    public void notifyCountChanged(int newCount) {
        logger.log(Level.INFO, "notify count changed");
        EventBus eb = vertx.eventBus();

        JsonObject obj = new JsonObject();
        obj.put("event", "count-changed");
        obj.put("value", newCount);
        eb.publish(EBIKE_MOVEMENT_EVENT_TOPIC, obj);
    }

    private void sendReply(RoutingContext request, JsonObject reply) {
        HttpServerResponse response = request.response();
        response.putHeader("content-type", "application/json");
        response.end(reply.toString());
    }

    //public int gugu = 0;

}

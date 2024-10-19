
package sap.ass01.layers.utils;

import io.vertx.core.Vertx;

/**
 * Represents the use of the singleton pattern for sharing the vertx instance
 */
final public class VertxSingleton {

    private static VertxSingleton instance;
    private final Vertx vertx;

    private VertxSingleton() {
        this.vertx = Vertx.vertx();
    }

    /**
     * @return returns the vertx instance, if not present it creates it
     */
    public static VertxSingleton getInstance() {
        if (instance == null) {
            instance = new VertxSingleton();
        }
        return instance;
    }

    /**
     * @return returns the vertx
     */
    public Vertx getVertx() {
        return this.vertx;
    }
}

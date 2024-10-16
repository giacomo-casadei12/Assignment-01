package sap.ass01.layers.PL.simulation;

/**
 * 2-dimensional point
 * objects are completely state-less
 */
public record P2d(double x, double y) implements java.io.Serializable {

    public P2d sum(V2d v) {
        return new P2d(x + v.x(), y + v.y());
    }

    @Override
    public String toString() {
        return "P2d(" + x + "," + y + ")";
    }

}

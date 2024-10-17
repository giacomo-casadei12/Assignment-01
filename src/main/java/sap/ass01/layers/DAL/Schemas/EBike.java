package sap.ass01.layers.DAL.Schemas;

/**
 *  Represent an Electric Bike (abbreviated to EBike)
 */
public interface EBike {
    /**
     * @return the ID of the bike
     */
    int ID();

    /**
     * @return the battery percentage of the bike
     */
    int battery();

    /**
     * @return the actual state of the bike
     */
    String state();

    /**
     * @return the actual X coordinate of the bike
     */
    int positionX();

    /**
     * @return the actual Y coordinate of the bike
     */
    int positionY();
}

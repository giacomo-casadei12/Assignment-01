package sap.ass01.layers.DataAccessL.Schemas;

public interface MutableEBike {

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

    void setID(int id);
    void setBattery(int battery);
    void setState(String state);
    void setPositionX(int x);
    void setPositionY(int y);
}

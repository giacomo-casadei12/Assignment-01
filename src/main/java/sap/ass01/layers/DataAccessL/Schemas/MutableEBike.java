package sap.ass01.layers.DataAccessL.Schemas;

import sap.ass01.layers.PersistenceL.Entities.EBike;

public interface MutableEBike extends EBike {
    void setID(int id);
    void setBattery(int battery);
    void setState(String state);
    void setPositionX(int x);
    void setPositionY(int y);
}

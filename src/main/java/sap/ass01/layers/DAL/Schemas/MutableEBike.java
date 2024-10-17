package sap.ass01.layers.DAL.Schemas;

public interface MutableEBike extends EBike {
    void setID(int id);
    void setBattery(int battery);
    void setState(String state);
    void setPositionX(int x);
    void setPositionY(int y);
}

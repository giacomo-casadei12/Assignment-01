package sap.ass01.layers.DAL.Schemas;

public interface EBike {
    int getID();
    int getBattery();
    EBikeState getState();
    int getPositionX();
    int getPositionY();
}

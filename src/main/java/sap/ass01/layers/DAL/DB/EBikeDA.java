package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.EBikeSchema;

import java.util.List;

public interface EBikeDA {
    List<EBikeSchema> getAllEBikes();
    List<EBikeSchema> getAllAvailableEBikes();
    List<EBikeSchema> getAllEBikesNearby(int positionX, int positionY);
    EBikeSchema getEBikeById(int id);
    boolean createEBike(int positionX, int positionY);
    boolean updateEBike(int id, int battery, int state, int positionX, int positionY);
    boolean deleteEBike(int id);
}

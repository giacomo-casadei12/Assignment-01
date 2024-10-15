package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.utils.EBikeState;

import java.util.List;

public interface EBikeDA {
    List<EBike> getAllEBikes();
    List<EBike> getAllAvailableEBikes();
    List<EBike> getAllEBikesNearby(int positionX, int positionY);
    EBike getEBikeById(int id);
    boolean createEBike(int positionX, int positionY);
    boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY);
    boolean deleteEBike(int id);
}

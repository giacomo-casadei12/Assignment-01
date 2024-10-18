package sap.ass01.layers.DataAccessL.DB;

import sap.ass01.layers.DataAccessL.Schemas.MutableEBike;
import sap.ass01.layers.utils.EBikeState;

import java.util.List;

public interface EBikeDA {
    List<MutableEBike> getAllEBikes();
    List<MutableEBike> getAllAvailableEBikes();
    List<MutableEBike> getAllEBikesNearby(int positionX, int positionY);
    MutableEBike getEBikeById(int id);
    boolean createEBike(int positionX, int positionY);
    boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY);
    boolean deleteEBike(int id);
}

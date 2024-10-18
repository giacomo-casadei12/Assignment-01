package sap.ass01.layers.BusinessLogicL.Logic;


import sap.ass01.layers.utils.Pair;

public interface RideManager {

    boolean startRide(int userId, int eBikeId);
    Pair<Integer, Integer> updateRide(int userId, int eBikeId, int positionX, int positionY);
    boolean endRide(int userId, int eBikeId);

}

package sap.ass01.layers.BLL.Persistence;

import sap.ass01.layers.DAL.Schemas.Ride;

import java.util.List;

public interface RidePersistence {
    List<Ride> getAllRides(boolean ongoing, int userId, int eBikeId);
    Ride getRide(int rideId, int userId);

    boolean createRide(int userId, int eBikeId);
    boolean endRide(int id);
    boolean deleteRide(int id);
}

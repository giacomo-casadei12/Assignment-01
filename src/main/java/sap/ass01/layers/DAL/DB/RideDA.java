package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.RideSchema;

import java.util.List;

public interface RideDA {
    List<RideSchema> getAllRides();
    List<RideSchema> getAllOngoingRides();
    List<RideSchema> getAllRidesByUser(int userId);
    List<RideSchema> getAllRidesByEBike(int eBikeId);
    RideSchema getRideById(int id);
    boolean createRide(int userId, int eBikeId);
    boolean endRide(int id);
    boolean deleteRide(int id);
}

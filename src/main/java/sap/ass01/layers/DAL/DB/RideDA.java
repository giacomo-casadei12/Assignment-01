package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.Ride;

import java.util.List;

public interface RideDA {
    List<Ride> getAllRides();
    List<Ride> getAllOngoingRides();
    List<Ride> getAllRidesByUser(int userId);
    List<Ride> getAllRidesByEBike(int eBikeId);
    Ride getRideById(int id);
    Ride getOngoingRideByUserId(int userId);
    boolean createRide(int userId, int eBikeId);
    boolean endRide(int id);
    boolean deleteRide(int id);
}

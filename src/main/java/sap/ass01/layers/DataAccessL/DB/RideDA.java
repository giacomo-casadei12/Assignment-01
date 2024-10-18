package sap.ass01.layers.DataAccessL.DB;

import sap.ass01.layers.DataAccessL.Schemas.MutableRide;

import java.util.List;

public interface RideDA {
    List<MutableRide> getAllRides();
    List<MutableRide> getAllOngoingRides();
    List<MutableRide> getAllRidesByUser(int userId);
    List<MutableRide> getAllRidesByEBike(int eBikeId);
    MutableRide getRideById(int id);
    MutableRide getOngoingRideByUserId(int userId);
    boolean createRide(int userId, int eBikeId);
    boolean endRide(int id);
    boolean deleteRide(int id);
}

package sap.ass01.clean.domain.ports;


import sap.ass01.clean.utils.Pair;

/**
 * The access point to all methods regarding the
 * persistence of Users, EBikes and Rides.
 */
public interface AppManager extends UserPersistence, EBikePersistence, RidePersistence {
    boolean startRide(int userID, int bikeID);

    Pair<Integer, Integer> updateRide(int userID, int bikeID, int x, int y);

    boolean endRide(int userID, int bikeID);

}

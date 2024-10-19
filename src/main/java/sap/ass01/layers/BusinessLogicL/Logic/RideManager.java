package sap.ass01.layers.BusinessLogicL.Logic;


import sap.ass01.layers.utils.Pair;

/**
 * The interface for the Ride manager that contains the logic for
 * handling an ongoing ride.
 */
public interface RideManager {

    /**
     * Start a ride given a userId and a bikeID.
     *
     * @param userId  the user id
     * @param eBikeId the e bike id
     * @return true if the ride was successfully created
     */
    boolean startRide(int userId, int eBikeId);

    /**
     * Update the position of the bike in the ride.
     *
     * @param userId    the user id of the ride
     * @param eBikeId   the e bike id of the bike used in the ride
     * @param positionX the actual x coordinate
     * @param positionY the actual Y coordinate
     * @return a Pair of Integers that contains the credit left for
     * the user and residual battery of the bike
     */
    Pair<Integer, Integer> updateRide(int userId, int eBikeId, int positionX, int positionY);

    /**
     * End a ride given a userId and a bikeID.
     *
     * @param userId  the user id
     * @param eBikeId the e bike id
     * @return true if the ride was successfully deleted
     */
    boolean endRide(int userId, int eBikeId);

}

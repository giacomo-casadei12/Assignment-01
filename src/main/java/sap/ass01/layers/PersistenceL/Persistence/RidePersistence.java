package sap.ass01.layers.PersistenceL.Persistence;

import sap.ass01.layers.PersistenceL.Entities.Ride;

import java.util.List;

/**
 * The interface exposing method for retrieve
 * info from the Data Access Layer regarding the rides.
 */
public interface RidePersistence {
    /**
     * Gets all rides.
     *
     * @param ongoing if true, returns all the active rides
     * @param userId  if greater than 0, returns all the rides made by that user
     * @param eBikeId if greater than 0, returns all the rides made using that bike
     * @return all rides matching criteria of inputs
     */
    List<Ride> getAllRides(boolean ongoing, int userId, int eBikeId);

    /**
     * Gets a single ride.
     *
     * @param rideId if greater than 0, returns the ride with that specified id
     * @param userId if greater than 0, returns the active ride of that user
     * @return the ride matching criteria of inputs
     */
    Ride getRide(int rideId, int userId);

    /**
     * Create a ride.
     *
     * @param userId  the user starting the ride
     * @param eBikeId the bike used for the ride
     * @return true if the ride was created successfully
     */
    boolean createRide(int userId, int eBikeId);

    /**
     * End ride the specified ride.
     *
     * @param id the id of the ride
     * @return true if the ride was ended successfully
     */
    boolean endRide(int id);

    /**
     * Delete the specified ride
     *
     * @param id the id of the ride
     * @return true if the ride was deleted successfully
     */
    boolean deleteRide(int id);
}

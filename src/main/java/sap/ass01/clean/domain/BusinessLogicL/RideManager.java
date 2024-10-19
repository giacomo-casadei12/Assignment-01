package sap.ass01.clean.domain.BusinessLogicL;


import sap.ass01.clean.domain.entities.EBike;
import sap.ass01.clean.domain.entities.User;
import sap.ass01.clean.utils.Pair;

/**
 * The interface for the Ride manager that contains the logic for
 * handling an ongoing ride.
 */
public interface RideManager {


    boolean startRide(User user, EBike bike);

    Pair<Integer, Integer> updateRide(User user, EBike bike, int x, int y, long timeElapsed);

    boolean endRide(User user, EBike bike);

    void attachPersistenceNotificationService(PersistenceNotificationService persistenceNotificationService);

}

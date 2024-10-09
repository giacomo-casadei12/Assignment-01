package sap.ass01.layers.BLL.Persistence;

import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.EBikeState;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.User;

import java.util.List;

public interface PersistenceManager {

    List<EBike> getAllEBikes(int positionX, int positionY, boolean available);
    EBike getEBike(int id);

    List<Ride> getAllRides(boolean ongoing, int userId, int eBikeId);
    Ride getRide(int rideId, int userId);

    List<User> getAllUsers();
    User getUser(int id, String userName);

    boolean login(String userName, String password);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);
    boolean createEBike(int positionX, int positionY);
    boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY);
    boolean deleteEBike(int id);
    boolean createRide(int userId, int eBikeId);
    boolean endRide(int id);
    boolean deleteRide(int id);

}

package sap.ass01.clean.domain.ports;

import sap.ass01.clean.domain.BusinessLogicL.PersistenceNotificationService;
import sap.ass01.clean.domain.BusinessLogicL.RideManager;
import sap.ass01.clean.domain.entities.*;
import sap.ass01.clean.domain.ports.dataAccessPorts.EBikeDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.RideDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.UserDA;
import sap.ass01.clean.utils.EBikeState;
import sap.ass01.clean.utils.Pair;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of the AppManager interface.
 */
public class AppManagerImpl implements AppManager, PersistenceNotificationService {

    private final EBikeDA bikeDA;
    private final RideDA rideDA;
    private final UserDA userDA;
    private final RideManager rideManager;
    private final Map<Pair<Integer, Integer>, Long> rideUpdateTimes = new ConcurrentHashMap<>();

    /**
     * Instantiates a new App Manager
     *
     * @param rideManager the logic for handle rides
     * @param rideDA      the persistence abstraction for rides
     * @param bikeDA      the persistence abstraction for bikes
     * @param userDA      the persistence abstraction for users
     */
    public AppManagerImpl(RideManager rideManager, RideDA rideDA, EBikeDA bikeDA, UserDA userDA) {
        this.bikeDA = bikeDA;
        this.rideDA = rideDA;
        this.userDA = userDA;
        this.rideManager = rideManager;
    }

    @Override
    public List<EBike> getAllEBikes(int positionX, int positionY, boolean available) {
        List<EBike> res;

        if (available) {
            res = this.bikeDA.getAllAvailableEBikes();
        } else if (positionX > 0 || positionY > 0) {
            res = this.bikeDA.getAllEBikesNearby(positionX, positionY);
        } else {
            res = this.bikeDA.getAllEBikes();
        }

        return res;
    }

    @Override
    public EBike getEBike(int id) {
        return this.bikeDA.getEBikeById(id);
    }

    @Override
    public List<Ride> getAllRides(boolean ongoing, int userId, int eBikeId) {
        List<Ride> res;

        if (ongoing) {
            res = this.rideDA.getAllOngoingRides();
        } else if (userId > 0) {
            res = this.rideDA.getAllRidesByUser(userId);
        } else if (eBikeId > 0) {
            res = this.rideDA.getAllRidesByEBike(eBikeId);
        } else {
            res = this.rideDA.getAllRides();
        }

        return res;
    }

    @Override
    public Ride getRide(int rideId, int userId) {
        return rideId != 0 && userId == 0 ?
                this.rideDA.getRideById(rideId) :
                this.rideDA.getOngoingRideByUserId(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return this.userDA.getAllUsers();
    }

    @Override
    public User getUser(int id, String userName) {
        return id != 0 ? this.userDA.getUserById(id) :
                this.userDA.getUserByName(userName);
    }

    @Override
    public boolean login(String userName, String password) {
        return this.userDA.login(userName, password);
    }

    @Override
    public boolean createUser(String userName, String password) {
        return this.userDA.createUser(userName, password);
    }

    @Override
    public boolean updateUser(int id, int credit) {
        return this.userDA.updateUser(id, credit);
    }

    @Override
    public boolean deleteUser(int id) {
        return this.userDA.deleteUser(id);
    }

    @Override
    public boolean createEBike(int positionX, int positionY) {
        return this.bikeDA.createEBike(positionX, positionY);
    }

    @Override
    public boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY) {
        EBike bike = new EBikeImpl();
        bike.setBattery(battery);
        bike.setID(id);
        bike.setPositionX(positionX);
        bike.setPositionY(positionY);
        bike.setState(state.toString());
        return this.bikeDA.updateEBike(bike);
    }

    @Override
    public boolean updateEbikePosition(int id, int positionX, int positionY) {
        EBike bike = this.bikeDA.getEBikeById(id);
        return this.bikeDA.updateEBike(bike);
    }

    @Override
    public boolean deleteEBike(int id) {
        return this.bikeDA.deleteEBike(id);
    }

    @Override
    public boolean createRide(int userId, int eBikeId) {
        return this.rideDA.createRide(userId, eBikeId);
    }

    @Override
    public boolean endRide(int id) {
        return this.rideDA.endRide(id);
    }

    @Override
    public boolean deleteRide(int id) {
        return this.rideDA.deleteRide(id);
    }

    @Override
    public void notifyUpdateUser(User user) {
        this.updateUser(user.ID(), user.credit());
    }

    @Override
    public void notifyUpdateEBike(EBike bike) {
        this.updateEBike(bike.ID(), bike.battery(), EBikeState.valueOf(bike.state()),
                bike.positionX(), bike.positionY());
    }

    @Override
    public void notifyEndRide(User user, EBike bike) {
        Ride ride = this.getRide(0, user.ID());
        this.endRide(ride.ID());
    }

    @Override
    public boolean startRide(int userID, int bikeID) {
        User user = this.userDA.getUserById(userID);
        EBike eBike = this.bikeDA.getEBikeById(bikeID);
        var now = new Date().getTime();
        rideUpdateTimes.put(new Pair<>(user.ID(), eBike.ID()), now);
        boolean success = this.rideManager.startRide(user,eBike);
        if (success) {
            success = this.rideDA.createRide(userID, bikeID);
        }
        return success;
    }

    @Override
    public Pair<Integer, Integer> updateRide(int userID, int bikeID, int x, int y) {
        User user = this.userDA.getUserById(userID);
        EBike eBike = this.bikeDA.getEBikeById(bikeID);
        var now = new Date().getTime();
        long last = rideUpdateTimes.get(new Pair<>(user.ID(), eBike.ID()));
        long timeElapsed = now - last;
        rideUpdateTimes.put(new Pair<>(user.ID(), eBike.ID()), now);
        return this.rideManager.updateRide(user, eBike, x, y, timeElapsed);
    }

    @Override
    public boolean endRide(int userID, int bikeID) {
        User user = this.userDA.getUserById(userID);
        EBike eBike = this.bikeDA.getEBikeById(bikeID);
        return this.rideManager.endRide(user,eBike);
    }
}

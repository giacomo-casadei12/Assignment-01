package sap.ass01.layers.PersistenceL.Persistence;

import sap.ass01.layers.DataAccessL.DB.*;
import sap.ass01.layers.DataAccessL.Schemas.MutableEBike;
import sap.ass01.layers.DataAccessL.Schemas.MutableRide;
import sap.ass01.layers.DataAccessL.Schemas.MutableUser;
import sap.ass01.layers.PersistenceL.Entities.*;
import sap.ass01.layers.utils.EBikeState;

import java.util.List;
import java.util.stream.Collectors;

public class PersistenceManagerImpl implements PersistenceManager {

    private final EBikeDA bikeDA;
    private final RideDA rideDA;
    private final UserDA userDA;

    public PersistenceManagerImpl() {
        this.bikeDA = new EBikeDB();
        this.rideDA = new RideDB();
        this.userDA = new UserDB();
    }

    @Override
    public List<EBike> getAllEBikes(int positionX, int positionY, boolean available) {
        List<MutableEBike> res;

        if (available) {
            res = this.bikeDA.getAllAvailableEBikes();
        } else if (positionX > 0 || positionY > 0) {
            res = this.bikeDA.getAllEBikesNearby(positionX, positionY);
        } else {
            res = this.bikeDA.getAllEBikes();
        }

        return res.stream()
                .map(this::convertMutableEBikeToEBike)
                .collect(Collectors.toList());
    }

    @Override
    public EBike getEBike(int id) {
        return convertMutableEBikeToEBike(this.bikeDA.getEBikeById(id));
    }

    @Override
    public List<Ride> getAllRides(boolean ongoing, int userId, int eBikeId) {
        List<MutableRide> res;

        if (ongoing) {
            res = this.rideDA.getAllOngoingRides();
        } else if (userId > 0) {
            res = this.rideDA.getAllRidesByUser(userId);
        } else if (eBikeId > 0) {
            res = this.rideDA.getAllRidesByEBike(eBikeId);
        } else {
            res = this.rideDA.getAllRides();
        }

        return res.stream()
                .map(this::convertMutableRideToRide)
                .collect(Collectors.toList());
    }

    @Override
    public Ride getRide(int rideId, int userId) {
        return rideId != 0 && userId == 0 ?
                convertMutableRideToRide(this.rideDA.getRideById(rideId)) :
                convertMutableRideToRide(this.rideDA.getOngoingRideByUserId(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return this.userDA.getAllUsers().stream()
                .map(this::convertMutableUserToUser)
                .collect(Collectors.toList());
    }

    @Override
    public User getUser(int id, String userName) {
        return id != 0 ? convertMutableUserToUser(this.userDA.getUserById(id)) :
                convertMutableUserToUser(this.userDA.getUserByName(userName));
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
        return this.bikeDA.updateEBike(id, battery, state, positionX, positionY);
    }

    @Override
    public boolean updateEbikePosition(int id, int positionX, int positionY) {
        MutableEBike bike = this.bikeDA.getEBikeById(id);
        return this.bikeDA.updateEBike(id, bike.battery(), EBikeState.valueOf(bike.state()), positionX, positionY);
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

    private User convertMutableUserToUser(MutableUser mutableUser) {
       return mutableUser != null ? new UserImpl(mutableUser.ID(),
                mutableUser.userName(),
                mutableUser.credit(),
                mutableUser.admin()) :
               null;
    }

    private EBike convertMutableEBikeToEBike(MutableEBike mutableEBike) {
        return mutableEBike != null ? new EBikeImpl(mutableEBike.ID(),
                mutableEBike.battery(),
                mutableEBike.state(),
                mutableEBike.positionX(),
                mutableEBike.positionY()) :
                null;
    }

    private Ride convertMutableRideToRide(MutableRide mutableRide) {
        return mutableRide != null ? new RideImpl(mutableRide.ID(),
                mutableRide.startDate(),
                mutableRide.endDate(),
                mutableRide.userID(),
                mutableRide.eBikeID()) :
                null;
    }
}

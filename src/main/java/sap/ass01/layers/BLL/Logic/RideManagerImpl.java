package sap.ass01.layers.BLL.Logic;

import sap.ass01.layers.BLL.Persistence.PersistenceManager;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.EBikeState;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.User;

import java.awt.geom.Point2D;
import java.util.*;

public class RideManagerImpl implements RideManager {

    final private static double BATTERY_CONSUMPTION_PER_METER = 0.5;
    final private static double CREDIT_CONSUMPTION_PER_SECOND = 0.1;
    final private PersistenceManager manager;
    final private HashMap<Pair<Integer,Integer>, Long> ongoingRides = new HashMap<>();

    public RideManagerImpl(PersistenceManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean startRide(int userId, int eBikeId) {
        EBike bike = manager.getEBike(eBikeId);
        boolean success = false;
        if (bike.getState() == EBikeState.AVAILABLE) {
            success = manager.createRide(userId, eBikeId);

            if (success) {
                this.ongoingRides.put(new Pair<>(userId, eBikeId),
                        new Date().getTime());

                manager.updateEBike(eBikeId, bike.getBattery(), EBikeState.IN_USE, bike.getPositionX(), bike.getPositionY());
            }
        }

        return success;
    }

    @Override
    public Pair<Integer, Integer> updateRide(int userId, int eBikeId, int positionX, int positionY) {

        int credit;
        int battery;
        boolean stopRide;
        int rideId;
        Ride ride;

        credit = getUpdatedCredit(userId, eBikeId);

        stopRide = credit <= 0;

        battery = getUpdatedBattery(eBikeId, positionX, positionY);

        if (!stopRide) {
            stopRide = battery <= 0;
        }

        if (stopRide) {

            ride = manager.getRide(0, userId);

            if (ride != null) {
                rideId = ride.getID();
                manager.endRide(rideId);
            }

            if (battery > 0) {
                manager.updateEBike(eBikeId, battery, EBikeState.AVAILABLE, positionX, positionY);
            }

            this.ongoingRides.remove(new Pair<>(userId, eBikeId));

        }

        return new Pair<>(credit,battery);
    }

    @Override
    public boolean endRide(int userId, int eBikeId) {

        boolean res = false;
        Ride ride = manager.getRide(0, userId);

        if (ride != null) {
            int rideId = ride.getID();
            res = manager.endRide(rideId);
        }

        EBike bike = manager.getEBike(eBikeId);

        if (bike != null) {
            manager.updateEBike(eBikeId, bike.getBattery(), EBikeState.AVAILABLE, bike.getPositionX(), bike.getPositionY());
        }

        this.ongoingRides.remove(new Pair<>(userId, eBikeId));

        return res;
    }

    private int getUpdatedBattery(int eBikeId, int positionX, int positionY) {
        double distance;
        int battery = 0;
        EBike bike = manager.getEBike(eBikeId);

        if (bike != null) {
            battery = bike.getBattery();
            distance = Point2D.distance(bike.getPositionX(), bike.getPositionY(), positionX, positionY);
            battery -= (int) (distance * BATTERY_CONSUMPTION_PER_METER);

            if (battery <= 0) {
                manager.updateEBike(eBikeId, battery, EBikeState.OUT_OF_CHARGE, positionX, positionY);
            } else {
                manager.updateEBike(eBikeId, battery, EBikeState.IN_USE, positionX, positionY);
            }
        }
        return battery;
    }

    private int getUpdatedCredit(int userId, int eBikeId) {
        User user = manager.getUser(userId, "");
        int credit = 0;

        if (user != null) {
            credit = user.getCredit();
            var now = new Date().getTime();
            var last = ongoingRides.get(new Pair<>(userId, eBikeId));
            long timeElapsed = now - last;
            credit -= (int) (((double) timeElapsed / 1000) * CREDIT_CONSUMPTION_PER_SECOND);
            ongoingRides.put(new Pair<>(userId, eBikeId),now);
            manager.updateUser(userId, credit);
        }
        return credit;
    }

}



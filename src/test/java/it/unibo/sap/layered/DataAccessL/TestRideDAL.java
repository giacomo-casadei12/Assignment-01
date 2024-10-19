package it.unibo.sap.layered.DataAccessL;

import org.junit.Test;
import sap.ass01.layers.DataAccessL.DB.RideDA;
import sap.ass01.layers.DataAccessL.DB.RideDB;
import sap.ass01.layers.DataAccessL.Schemas.MutableRide;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestRideDAL {

    final RideDA rideDA;

    public TestRideDAL() {
        rideDA = new RideDB();
    }

    @Test
    public void createUpdateDeleteRide() {
        boolean b = rideDA.createRide(1,1);
        assertTrue(b);
        List<MutableRide> rs = rideDA.getAllOngoingRides();
        assertFalse(rs.isEmpty());
        assertTrue(rs.stream().anyMatch(ride -> ride.userID() == 1 && ride.eBikeID() == 1));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = rs.stream().
                                filter(ride -> ride.userID() == 1 && ride.eBikeID() == 1).
                                findFirst().
                                get().ID();
        MutableRide r = rideDA.getRideById(id);
        assertNotNull(r);
        assertEquals(1, r.userID());
        assertEquals(1, r.eBikeID());
        b = rideDA.endRide(id);
        assertTrue(b);
        rs = rideDA.getAllOngoingRides();
        if (!rs.isEmpty()) {
            assertFalse(rs.stream().anyMatch(ride -> ride.userID() == 1 && ride.eBikeID() == 1));
        }
        b = rideDA.deleteRide(id);
        assertTrue(b);
        r = rideDA.getRideById(id);
        assertNull(r);
    }

    @Test
    public void getRidesByUserAndEBike() {
        boolean b = rideDA.createRide(1,1);
        assertTrue(b);
        List<MutableRide> rs = rideDA.getAllRidesByUser(1);
        assertFalse(rs.isEmpty());
        assertTrue(rs.stream().anyMatch(ride -> ride.userID() == 1 && ride.eBikeID() == 1));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = rs.stream().
                filter(ride -> ride.userID() == 1 && ride.eBikeID() == 1).
                findFirst().
                get().ID();
        rs = rideDA.getAllRidesByEBike(1);
        assertFalse(rs.isEmpty());
        b = rideDA.deleteRide(id);
        assertTrue(b);
        MutableRide r = rideDA.getRideById(id);
        assertNull(r);
    }

}

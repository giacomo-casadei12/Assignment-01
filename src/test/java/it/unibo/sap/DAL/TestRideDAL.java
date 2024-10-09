package it.unibo.sap.DAL;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.DAL.DB.RideDA;
import sap.ass01.layers.DAL.DB.RideDB;
import sap.ass01.layers.DAL.Schemas.RideSchema;

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
        List<RideSchema> rs = rideDA.getAllOngoingRides();
        assertFalse(rs.isEmpty());
        assertTrue(rs.stream().anyMatch(rideSchema -> rideSchema.getUserID() == 1 && rideSchema.getEBikeID() == 1));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = rs.stream().
                                filter(rideSchema -> rideSchema.getUserID() == 1 && rideSchema.getEBikeID() == 1).
                                findFirst().
                                get().getID();
        RideSchema r = rideDA.getRideById(id);
        assertNotNull(r);
        assertEquals(1, r.getUserID());
        assertEquals(1, r.getEBikeID());
        b = rideDA.endRide(id);
        assertTrue(b);
        rs = rideDA.getAllOngoingRides();
        if (!rs.isEmpty()) {
            assertFalse(rs.stream().anyMatch(rideSchema -> rideSchema.getUserID() == 1 && rideSchema.getEBikeID() == 1));
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
        List<RideSchema> rs = rideDA.getAllRidesByUser(1);
        assertFalse(rs.isEmpty());
        assertTrue(rs.stream().anyMatch(rideSchema -> rideSchema.getUserID() == 1 && rideSchema.getEBikeID() == 1));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = rs.stream().
                filter(rideSchema -> rideSchema.getUserID() == 1 && rideSchema.getEBikeID() == 1).
                findFirst().
                get().getID();
        rs = rideDA.getAllRidesByEBike(1);
        assertFalse(rs.isEmpty());
        b = rideDA.deleteRide(id);
        assertTrue(b);
        RideSchema r = rideDA.getRideById(id);
        assertNull(r);
    }

}
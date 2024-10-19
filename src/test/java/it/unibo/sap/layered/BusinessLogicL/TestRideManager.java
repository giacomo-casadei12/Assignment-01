package it.unibo.sap.layered.BusinessLogicL;

import org.junit.Test;
import sap.ass01.layers.utils.Pair;
import sap.ass01.layers.BusinessLogicL.Logic.RideManager;
import sap.ass01.layers.BusinessLogicL.Logic.RideManagerImpl;
import sap.ass01.layers.PersistenceL.Persistence.PersistenceManager;
import sap.ass01.layers.PersistenceL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.PersistenceL.Entities.EBike;
import sap.ass01.layers.PersistenceL.Entities.Ride;
import sap.ass01.layers.PersistenceL.Entities.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRideManager {

    final PersistenceManager persManager;
    final RideManager rideManager;
    int userID;
    int eBikeID;
    int rideID;

    public TestRideManager() {
        persManager = new PersistenceManagerImpl();
        rideManager = new RideManagerImpl(persManager);

    }

    @Test
    public void testRideCreationDeletion() {

        initializeUserAndEBike();
        boolean b = rideManager.startRide(userID,eBikeID);
        assertTrue(b);

        List<Ride> rs;
        List<Ride> rs2;
        List<Ride> rs3;
        Ride r;
        rs = persManager.getAllRides(true,0,0);
        assertNotNull(rs);
        rs2 = persManager.getAllRides(false,userID,0);
        assertNotNull(rs2);
        rs3 = persManager.getAllRides(false,0,eBikeID);
        assertNotNull(rs3);
        r = persManager.getRide(0,userID);
        assertNotNull(r);

        assertTrue(rs.contains(r));
        assertTrue(rs2.contains(r));
        assertTrue(rs3.contains(r));

        rideID = r.ID();

        b = rideManager.endRide(userID,eBikeID);
        assertTrue(b);

        r = persManager.getRide(r.ID(),0);
        assertNotNull(r.endDate());

        destroyUserEBikeAndRide();
    }

    @Test
    public void testRideUpdate() throws InterruptedException {

        initializeUserAndEBike();

        boolean b = rideManager.startRide(userID,eBikeID);
        assertTrue(b);

        Ride r = persManager.getRide(0,userID);
        assertNotNull(r);
        rideID = r.ID();
        Thread.sleep(10000);
        Pair<Integer,Integer> p = rideManager.updateRide(userID,eBikeID,9900,10000);
        assertEquals(50,p.second());
        assertEquals(90,p.first());

        destroyUserEBikeAndRide();
    }

    private void initializeUserAndEBike(){
        User u;
        EBike b;

        persManager.createUser("TestoRide","NotImportant");
        u = persManager.getUser(0,"TestoRide");
        persManager.updateUser(u.ID(),100);
        userID = u.ID();

        persManager.createEBike(10000,10000);
        b = persManager.getAllEBikes(10000,10000,false).get(0);
        eBikeID = b.ID();
    }

    private void destroyUserEBikeAndRide(){
        persManager.deleteUser(userID);
        persManager.deleteEBike(eBikeID);
        persManager.deleteRide(rideID);
    }


}

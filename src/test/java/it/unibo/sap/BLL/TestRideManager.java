package it.unibo.sap.BLL;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.BLL.Logic.Pair;
import sap.ass01.layers.BLL.Logic.RideManager;
import sap.ass01.layers.BLL.Logic.RideManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManager;
import sap.ass01.layers.BLL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.Ride;
import sap.ass01.layers.DAL.Schemas.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRideManager {

    final PersistenceManager persManager;
    final RideManager rideManager;
    int userID, eBikeID, rideID;

    public TestRideManager() {
        persManager = new PersistenceManagerImpl();
        rideManager = new RideManagerImpl(persManager);

    }

    @Test
    public void testRideCreationDeletion() {

        initializeUserAndEBike();
        boolean b = rideManager.startRide(userID,eBikeID);
        assertTrue(b);

        List<Ride> rs,rs2,rs3;
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

        rideID = r.getID();

        b = rideManager.endRide(userID,eBikeID);
        assertTrue(b);

        r = persManager.getRide(r.getID(),0);
        assertNotNull(r.getEndDate());

        destroyUserEBikeAndRide();
    }

    @Test
    public void testRideUpdate() throws InterruptedException {

        initializeUserAndEBike();

        boolean b = rideManager.startRide(userID,eBikeID);
        assertTrue(b);

        Ride r = persManager.getRide(0,userID);
        assertNotNull(r);
        rideID = r.getID();
        Thread.sleep(10000);
        Pair<Integer,Integer> p = rideManager.updateRide(userID,eBikeID,9900,10000);
        assertEquals(50,p.second());
        assertEquals(99,p.first());

        destroyUserEBikeAndRide();
    }

    private void initializeUserAndEBike(){
        User u;
        EBike b;

        persManager.createUser("TestoRide","NotImportant");
        u = persManager.getUser(0,"TestoRide");
        persManager.updateUser(u.getID(),100);
        userID = u.getID();

        persManager.createEBike(10000,10000);
        b = persManager.getAllEBikes(10000,10000,false).get(0);
        eBikeID = b.getID();
    }

    private void destroyUserEBikeAndRide(){
        persManager.deleteUser(userID);
        persManager.deleteEBike(eBikeID);
        persManager.deleteRide(rideID);
    }


}

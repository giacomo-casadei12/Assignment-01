package it.unibo.sap.clean.domain;

import org.junit.Test;
import sap.ass01.clean.domain.BusinessLogicL.RideManager;
import sap.ass01.clean.domain.BusinessLogicL.RideManagerImpl;
import sap.ass01.clean.domain.entities.EBike;
import sap.ass01.clean.domain.entities.EBikeImpl;
import sap.ass01.clean.domain.entities.User;
import sap.ass01.clean.domain.entities.UserImpl;
import sap.ass01.clean.utils.EBikeState;
import sap.ass01.clean.utils.Pair;

import static org.junit.jupiter.api.Assertions.*;

public class TestDomainRideLogic {

    private final RideManager manager;

    public TestDomainRideLogic() {
        this.manager = new RideManagerImpl();
    }

    @Test
    public void testCreateRide() {
        User healthy = createDefaultUser(100);
        User broke = createDefaultUser(0);
        EBike full = createDefaultEBike(EBikeState.AVAILABLE);
        EBike broken = createDefaultEBike(EBikeState.MAINTENANCE);

        boolean success;

        success = this.manager.startRide(healthy, full);
        assertTrue(success);
        assertEquals(EBikeState.IN_USE, EBikeState.valueOf(full.state()));

        success = this.manager.startRide(broke, full);
        assertFalse(success);

        success = this.manager.startRide(healthy, broken);
        assertFalse(success);

        success = this.manager.startRide(broke, broken);
        assertFalse(success);
    }

    @Test
    public void testUpdateRide() {
        User healthy = createDefaultUser(100);
        EBike inUse = createDefaultEBike(EBikeState.IN_USE);
        EBike inUseReserve = createDefaultEBike(EBikeState.IN_USE);

        Pair<Integer, Integer> p = this.manager.updateRide(healthy, inUse, 12, 10, 1000);

        assertNotNull(p);
        assertNotNull(p.first());
        assertNotNull(p.second());

        assertEquals(99, healthy.credit());
        assertEquals(99, inUse.battery());

        p = this.manager.updateRide(healthy, inUse, 210, 10, 1000);

        assertNotNull(p);
        assertNotNull(p.first());
        assertNotNull(p.second());

        assertEquals(98, healthy.credit());
        assertEquals(0, inUse.battery());
        assertEquals(EBikeState.OUT_OF_CHARGE, EBikeState.valueOf(inUse.state()));

        p = this.manager.updateRide(healthy, inUseReserve, 12, 10, 98000);

        assertNotNull(p);
        assertNotNull(p.first());
        assertNotNull(p.second());

        assertEquals(0, healthy.credit());
        assertEquals(99, inUseReserve.battery());
        assertEquals(EBikeState.AVAILABLE, EBikeState.valueOf(inUseReserve.state()));
    }

    @Test
    public void testEndRide() {
        User healthy = createDefaultUser(100);
        EBike inUse = createDefaultEBike(EBikeState.IN_USE);
        EBike outOfCharge = createDefaultEBike(EBikeState.IN_USE);

        boolean success = this.manager.endRide(healthy, inUse);
        assertTrue(success);
        assertEquals(EBikeState.AVAILABLE, EBikeState.valueOf(inUse.state()));

        this.manager.updateRide(healthy,outOfCharge, 210, 10, 1000);
        assertEquals(EBikeState.OUT_OF_CHARGE, EBikeState.valueOf(outOfCharge.state()));
        success = this.manager.endRide(healthy, outOfCharge);
        assertTrue(success);
        assertEquals(EBikeState.OUT_OF_CHARGE, EBikeState.valueOf(outOfCharge.state()));

    }

    private User createDefaultUser(int credit) {
        User u = new UserImpl();
        u.setID(1);
        u.setCredit(credit);
        u.setName("Test");
        u.setIsAdmin(false);
        return u;
    }

    private EBike createDefaultEBike(EBikeState state) {
        EBike bike = new EBikeImpl();
        bike.setID(1);
        bike.setState(state.toString());
        bike.setBattery(100);
        bike.setPositionX(10);
        bike.setPositionY(10);
        return bike;
    }

}

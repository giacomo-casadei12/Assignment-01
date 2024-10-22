package it.unibo.sap.layered.PersistenceL;

import org.junit.Test;
import sap.ass01.layers.BusinessLogicL.Persistence.PersistenceManager;
import sap.ass01.layers.BusinessLogicL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.BusinessLogicL.Entities.EBike;
import sap.ass01.layers.BusinessLogicL.Entities.User;

import sap.ass01.layers.utils.EBikeState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPersistenceManager {

    public static final String TEST_USERNAME = "Testabile";
    public static final String TEST_PASSWORD = "Testatone";
    final PersistenceManager persManager;

    public TestPersistenceManager() {
        persManager = new PersistenceManagerImpl();
    }

    @Test
    public void userCreationToDeletion() {
        boolean res;
        User u;
        List<User> us;

        res = persManager.login(TEST_USERNAME, TEST_PASSWORD);
        assertFalse(res);

        res = persManager.createUser(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(res);

        res = persManager.login(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(res);

        u = persManager.getUser(0, TEST_USERNAME);
        assertNotNull(u);
        assertEquals(TEST_USERNAME, u.userName());
        assertEquals(0, u.credit());

        us = persManager.getAllUsers();
        assertNotNull(us);
        assertTrue(us.contains(u));

        res = persManager.updateUser(u.id(), 75);
        assertTrue(res);
        u = persManager.getUser(u.id(),"");
        assertNotNull(u);
        assertEquals(TEST_USERNAME, u.userName());
        assertEquals(75, u.credit());

        res = persManager.deleteUser(u.id());
        assertTrue(res);
        u = persManager.getUser(u.id(),"");
        assertNull(u);

    }

    @Test
    public void bikeCreationToDeletion() {

        boolean res;
        EBike b;
        List<EBike> bs;
        List<EBike> bs2;

        res = persManager.createEBike(2,8);
        assertTrue(res);

        bs = persManager.getAllEBikes(2,8,false);
        assertNotNull(bs);

        bs2 = persManager.getAllEBikes(0,0,true);
        assertNotNull(bs2);

        List<EBike> result = bs.stream()
                .distinct()
                .filter(bs2::contains)
                .toList();
        assertFalse(result.stream().filter(bb ->
                bb.positionX() == 2 && bb.positionY() == 8).toList().isEmpty());

        b = result.stream().filter(bb ->
                bb.positionX() == 2 && bb.positionY() == 8).toList().get(0);

        b = persManager.getEBike(b.id());
        assertNotNull(b);
        assertTrue(result.contains(b));

        res = persManager.updateEBike(b.id(), 75, EBikeState.IN_USE, 10, 10);
        assertTrue(res);
        b = persManager.getEBike(b.id());
        assertNotNull(b);
        assertEquals(EBikeState.IN_USE.toString(), b.state());
        assertEquals(75, b.battery());

        result = persManager.getAllEBikes(0,0,true);
        assertNotNull(result);
        assertFalse(result.contains(b));

        res = persManager.deleteEBike(b.id());
        assertTrue(res);
        b = persManager.getEBike(b.id());
        assertNull(b);

    }

}

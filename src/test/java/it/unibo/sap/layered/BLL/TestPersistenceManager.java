package it.unibo.sap.layered.BLL;

import org.junit.Test;
import sap.ass01.layers.PersistenceL.Persistence.PersistenceManager;
import sap.ass01.layers.PersistenceL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.PersistenceL.Entities.EBike;
import sap.ass01.layers.PersistenceL.Entities.User;

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

        res = persManager.updateUser(u.ID(), 75);
        assertTrue(res);
        u = persManager.getUser(u.ID(),"");
        assertNotNull(u);
        assertEquals(TEST_USERNAME, u.userName());
        assertEquals(75, u.credit());

        res = persManager.deleteUser(u.ID());
        assertTrue(res);
        u = persManager.getUser(u.ID(),"");
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

        b = persManager.getEBike(b.ID());
        assertNotNull(b);
        assertTrue(result.contains(b));

        res = persManager.updateEBike(b.ID(), 75, EBikeState.IN_USE, 10, 10);
        assertTrue(res);
        b = persManager.getEBike(b.ID());
        assertNotNull(b);
        assertEquals(EBikeState.IN_USE.toString(), b.state());
        assertEquals(75, b.battery());

        result = persManager.getAllEBikes(0,0,true);
        assertNotNull(result);
        assertFalse(result.contains(b));

        res = persManager.deleteEBike(b.ID());
        assertTrue(res);
        b = persManager.getEBike(b.ID());
        assertNull(b);

    }

}

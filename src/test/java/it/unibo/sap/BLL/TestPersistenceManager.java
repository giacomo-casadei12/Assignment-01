package it.unibo.sap.BLL;

import sap.ass01.layers.BLL.Persistence.PersistenceManager;
import sap.ass01.layers.BLL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.DAL.Schemas.User;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPersistenceManager {

    final PersistenceManager persManager;

    public TestPersistenceManager() {
        persManager = new PersistenceManagerImpl();
    }

    @Test
    public void userCreationToDeletion() {
        boolean res;
        User u;
        List<User> us;

        res = persManager.login("Testabile","Testatone");
        assertFalse(res);

        res = persManager.createUser("Testabile", "Testatone");
        assertTrue(res);

        res = persManager.login("Testabile","Testatone");
        assertTrue(res);

        u = persManager.getUser(0,"Testabile");
        assertNotNull(u);
        assertEquals("Testabile", u.getName());
        assertEquals(0, u.getCredit());

        us = persManager.getAllUsers();
        assertNotNull(us);
        assertTrue(us.contains(u));

        res = persManager.updateUser(u.getID(), 75);
        assertTrue(res);
        u = persManager.getUser(u.getID(),"");
        assertNotNull(u);
        assertEquals("Testabile", u.getName());
        assertEquals(75, u.getCredit());

        res = persManager.deleteUser(u.getID());
        assertTrue(res);
        u = persManager.getUser(u.getID(),"");
        assertNull(u);

    }

    @Test
    public void bikeCreationToDeletion() {

        boolean res;
        EBike b;
        List<EBike> bs, bs2;

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
                bb.getPositionX() == 2 && bb.getPositionY() == 8).toList().isEmpty());

        b = result.stream().filter(bb ->
                bb.getPositionX() == 2 && bb.getPositionY() == 8).toList().get(0);

        b = persManager.getEBike(b.getID());
        assertNotNull(b);
        assertTrue(result.contains(b));

        res = persManager.updateEBike(b.getID(), 75, sap.ass01.layers.DAL.Schemas.EBikeState.IN_USE, 10, 10);
        assertTrue(res);
        b = persManager.getEBike(b.getID());
        assertNotNull(b);
        assertEquals(sap.ass01.layers.DAL.Schemas.EBikeState.IN_USE, b.getState());
        assertEquals(75, b.getBattery());

        result = persManager.getAllEBikes(0,0,true);
        assertNotNull(result);
        assertFalse(result.contains(b));

        res = persManager.deleteEBike(b.getID());
        assertTrue(res);
        b = persManager.getEBike(b.getID());
        assertNull(b);

    }

}

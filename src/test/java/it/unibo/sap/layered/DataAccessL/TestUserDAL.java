package it.unibo.sap.layered.DataAccessL;

import org.junit.Test;
import sap.ass01.layers.DataAccessL.DB.UserDA;
import sap.ass01.layers.DataAccessL.DB.UserDB;
import sap.ass01.layers.DataAccessL.Schemas.MutableUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserDAL {

    final UserDA userDA;

    public TestUserDAL() {
        userDA = new UserDB();
    }

    //there is always at least the admin
    @Test
    public void getAtLeastOneUser() {
        List<MutableUser> x = userDA.getAllUsers();
        assertFalse(x.isEmpty());
        assertTrue(x.get(0).admin());
    }

    @Test
    public void getFirstUser() {
        MutableUser x = userDA.getUserById(1);
        assertNotNull(x);
        assertEquals(1, x.ID());
    }

    @Test
    public void loginUser() {
        boolean x = userDA.login("GiacomoCasadei","password");
        assertFalse(x);
        x = userDA.login("GiacomoC","passwordStorta");
        assertFalse(x);
        x = userDA.login("GiacomoCasadei","passwordStorta");
        assertFalse(x);
        x = userDA.login("GiacomoC","password");
        assertTrue(x);
    }


    @Test
    public void createUpdateDeleteUser() {
        boolean b = userDA.createUser("Giangurgulo","Pulcinella");
        assertTrue(b);
        MutableUser u = userDA.getUserByName("Giangurgulo");
        assertNotNull(u);
        assertEquals("Giangurgulo", u.userName());
        int id = u.ID();
        b = userDA.updateUser(id,200);
        assertTrue(b);
        u = userDA.getUserById(id);
        assertNotNull(u);
        assertEquals(200,u.credit());
        b = userDA.deleteUser(id);
        assertTrue(b);
        u = userDA.getUserById(id);
        assertNull(u);
    }

}

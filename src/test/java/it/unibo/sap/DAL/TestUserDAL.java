package it.unibo.sap.DAL;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.DAL.DB.UserDA;
import sap.ass01.layers.DAL.DB.UserDB;
import sap.ass01.layers.DAL.Schemas.User;

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
        List<User> x = userDA.getAllUsers();
        assertFalse(x.isEmpty());
        assertTrue(x.get(0).isAdmin());
    }

    @Test
    public void getFirstUser() {
        User x = userDA.getUserById(1);
        assertNotNull(x);
        assertEquals(1, x.getID());
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
        User u = userDA.getUserByName("Giangurgulo");
        assertNotNull(u);
        assertEquals("Giangurgulo", u.getName());
        int id = u.getID();
        b = userDA.updateUser(id,200);
        assertTrue(b);
        u = userDA.getUserById(id);
        assertNotNull(u);
        assertEquals(200,u.getCredit());
        b = userDA.deleteUser(id);
        assertTrue(b);
        u = userDA.getUserById(id);
        assertNull(u);
    }

}

package it.unibo.sap;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.DAL.UserDA;
import sap.ass01.layers.DAL.UserDB;
import sap.ass01.layers.DAL.UserSchema;

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
        List<UserSchema> x = userDA.getAllUsers();
        assertFalse(x.isEmpty());
        assertTrue(x.get(0).isAdmin());
    }

    @Test
    public void getFirstUser() {
        UserSchema x = userDA.getUserById(1);
        assertNotNull(x);
        assertEquals(1, x.getID());
    }


    @Test
    public void createUser() {
        boolean x = userDA.createUser("Giangurgulo","Pulcinella");
        assertTrue(x);
    }

    @Test
    public void getUserByName() {
        UserSchema x = userDA.getUserByName("Giangurgulo");
        assertNotNull(x);
        assertEquals("Giangurgulo", x.getName());
    }


    @Test
    public void updateUser() {
        boolean x = userDA.updateUser(3,200);
        assertTrue(x);
        UserSchema y = userDA.getUserById(3);
        assertNotNull(y);
        assertEquals(200,y.getCredit());
    }

    @Test
    public void deleteUser() {
        boolean x = userDA.deleteUser(3);
        assertTrue(x);
        UserSchema y = userDA.getUserById(3);
        assertNull(y);
    }
}

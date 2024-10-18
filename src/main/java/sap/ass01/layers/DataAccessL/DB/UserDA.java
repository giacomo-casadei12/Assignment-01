package sap.ass01.layers.DataAccessL.DB;

import sap.ass01.layers.DataAccessL.Schemas.MutableUser;

import java.util.List;

public interface UserDA {
    List<MutableUser> getAllUsers();
    MutableUser getUserByName(String userName);
    MutableUser getUserById(int id);
    boolean login(String userName, String password);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);
}

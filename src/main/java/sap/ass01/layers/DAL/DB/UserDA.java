package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.User;

import java.util.List;

public interface UserDA {
    List<User> getAllUsers();
    User getUserByName(String userName);
    User getUserById(int id);
    boolean login(String userName, String password);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);
}

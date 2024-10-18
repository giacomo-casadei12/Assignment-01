package sap.ass01.layers.PersistenceL.Persistence;

import sap.ass01.layers.PersistenceL.Entities.User;

import java.util.List;

public interface UserPersistence {

    List<User> getAllUsers();
    User getUser(int id, String userName);
    boolean login(String userName, String password);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);

}

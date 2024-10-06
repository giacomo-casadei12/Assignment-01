package sap.ass01.layers.DAL;

import java.util.List;

public interface UserDA {
    List<UserSchema> getAllUsers();
    UserSchema getUserByName(String userName);
    UserSchema getUserById(int id);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);
}

package sap.ass01.layers.DAL.DB;

import sap.ass01.layers.DAL.Schemas.UserSchema;

import java.util.List;

public interface UserDA {
    List<UserSchema> getAllUsers();
    UserSchema getUserByName(String userName);
    UserSchema getUserById(int id);
    boolean login(String userName, String password);
    boolean createUser(String userName, String password);
    boolean updateUser(int id, int credit);
    boolean deleteUser(int id);
}

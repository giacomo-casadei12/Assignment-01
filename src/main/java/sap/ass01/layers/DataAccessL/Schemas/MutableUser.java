package sap.ass01.layers.DataAccessL.Schemas;

import sap.ass01.layers.PersistenceL.Entities.User;

public interface MutableUser extends User {
    void setID(int id);
    void setName(String username);
    void setCredit(int credit);
    void setIsAdmin(boolean isAdmin);
}

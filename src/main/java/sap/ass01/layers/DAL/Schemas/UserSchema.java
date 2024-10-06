package sap.ass01.layers.DAL.Schemas;

public interface UserSchema {
    int getID();
    String getName();
    String getPassword();
    int getCredit();
    Boolean isAdmin();
}


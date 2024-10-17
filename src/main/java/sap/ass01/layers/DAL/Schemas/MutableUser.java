package sap.ass01.layers.DAL.Schemas;

public interface MutableUser extends User {
    void setID(int id);
    void setName(String username);
    void setCredit(int credit);
    void setIsAdmin(boolean isAdmin);
}

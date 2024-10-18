package sap.ass01.layers.DataAccessL.Schemas;

public interface MutableUser {
    void setID(int id);
    void setName(String username);
    void setCredit(int credit);
    void setIsAdmin(boolean isAdmin);
    int ID();
    String userName();
    int credit();
    boolean admin();
}

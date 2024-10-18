package sap.ass01.layers.DataAccessL.Schemas;

public interface MutableRide {
    void setID(int id);
    void setStartDate(String startDate);
    void setEndDate(String endDate);
    void setUserID(int userID);
    void setEBikeID(int bikeID);
    int ID();
    String startDate();
    String endDate();
    int userID();
    int eBikeID();
}

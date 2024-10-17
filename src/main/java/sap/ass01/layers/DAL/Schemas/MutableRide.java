package sap.ass01.layers.DAL.Schemas;

public interface MutableRide extends Ride {
    void setID(int id);
    void setStartDate(String startDate);
    void setEndDate(String endDate);
    void setUserID(int userID);
    void setEBikeID(int bikeID);
}

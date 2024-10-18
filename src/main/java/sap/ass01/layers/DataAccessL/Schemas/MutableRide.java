package sap.ass01.layers.DataAccessL.Schemas;

import sap.ass01.layers.PersistenceL.Entities.Ride;

public interface MutableRide extends Ride {
    void setID(int id);
    void setStartDate(String startDate);
    void setEndDate(String endDate);
    void setUserID(int userID);
    void setEBikeID(int bikeID);
}

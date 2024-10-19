package sap.ass01.clean.domain.entities;

import java.util.Objects;

/**
 * The implementation of the MutableEBike interface.
 */
public class RideImpl implements Ride {

    private int ID;
    private String startDate;
    private String endDate;
    private int userID;
    private int eBikeID;

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public void setEBikeID(int bikeID) {
        this.eBikeID = bikeID;
    }

    @Override
    public int ID() {
        return this.ID;
    }

    @Override
    public String startDate() {
        return this.startDate;
    }

    @Override
    public String endDate() {
        return this.endDate;
    }

    @Override
    public int userID() {
        return this.userID;
    }

    @Override
    public int eBikeID() {
        return this.eBikeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideImpl that = (RideImpl) o;
        return ID == that.ID && userID == that.userID && eBikeID == that.eBikeID && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, startDate, endDate, userID, eBikeID);
    }
}
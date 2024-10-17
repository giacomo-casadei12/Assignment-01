package sap.ass01.layers.DAL.Schemas;

public class MutableRideImpl implements MutableRide {

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
}

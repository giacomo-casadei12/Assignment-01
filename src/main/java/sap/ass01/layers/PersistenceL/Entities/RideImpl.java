package sap.ass01.layers.PersistenceL.Entities;

import java.util.Objects;

public record RideImpl(int ID, String startDate, String endDate, int userID, int eBikeID) implements Ride {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideImpl ride = (RideImpl) o;
        return ID == ride.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public void setID(int id) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setStartDate(String startDate) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setEndDate(String endDate) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setUserID(int userID) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setEBikeID(int bikeID) {
        throw new UnsupportedOperationException("This is an immutable class");
    }
}

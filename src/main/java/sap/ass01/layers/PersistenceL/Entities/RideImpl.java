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

}

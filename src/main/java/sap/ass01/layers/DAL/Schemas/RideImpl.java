package sap.ass01.layers.DAL.Schemas;

import java.util.Date;
import java.util.Objects;

public class RideImpl implements Ride {

    final int id;
    final Date startDate;
    final Date endDate;
    final int userID;
    final int eBikeID;

    public RideImpl(int id, Date startDate, Date endDate, int userID, int eBikeID) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userID = userID;
        this.eBikeID = eBikeID;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public Date getStartDate() {
        return this.startDate;
    }

    @Override
    public Date getEndDate() {
        return this.endDate;
    }

    @Override
    public int getUserID() {
        return this.userID;
    }

    @Override
    public int getEBikeID() {
        return this.eBikeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideImpl ride = (RideImpl) o;
        return id == ride.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

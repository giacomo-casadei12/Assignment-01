package sap.ass01.layers.BusinessLogicL.Entities;

import java.util.Objects;

public record EBikeImpl(int ID, int battery, String state, int positionX, int positionY) implements EBike {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EBikeImpl eBike = (EBikeImpl) o;
        return ID() == eBike.ID();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID());
    }

    @Override
    public void setID(int id) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setBattery(int battery) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setState(String state) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setPositionX(int x) {
        throw new UnsupportedOperationException("This is an immutable class");
    }

    @Override
    public void setPositionY(int y) {
        throw new UnsupportedOperationException("This is an immutable class");
    }
}

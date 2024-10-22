package sap.ass01.layers.BusinessLogicL.Entities;

import java.util.Objects;

public record EBikeImpl(int id, int battery, String state, int positionX, int positionY) implements EBike {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EBikeImpl eBike = (EBikeImpl) o;
        return id() == eBike.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public void setId(int id) {
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

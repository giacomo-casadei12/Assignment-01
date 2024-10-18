package sap.ass01.layers.PersistenceL.Entities;

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
}

package sap.ass01.layers.DAL.Schemas;

import java.util.Objects;

public class EBikeImpl implements EBike {

    final int ID;
    final int battery;
    final String state;
    final int positionX;
    final int positionY;

    public EBikeImpl(int ID, int battery, String state, int positionX, int positionY) {
        this.ID = ID;
        this.battery = battery;
        this.state = state;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getBattery() {
        return this.battery;
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public int getPositionX() {
        return this.positionX;
    }

    @Override
    public int getPositionY() {
        return this.positionY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EBikeImpl eBike = (EBikeImpl) o;
        return getID() == eBike.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getID());
    }
}

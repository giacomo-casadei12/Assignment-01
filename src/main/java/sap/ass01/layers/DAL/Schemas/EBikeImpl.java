package sap.ass01.layers.DAL.Schemas;

public class EBikeImpl implements EBike {

    final int ID;
    final int battery;
    final EBikeState state;
    final int positionX;
    final int positionY;

    public EBikeImpl(int ID, int battery, EBikeState state, int positionX, int positionY) {
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
    public EBikeState getState() {
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
}

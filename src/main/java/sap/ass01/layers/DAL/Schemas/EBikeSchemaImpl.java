package sap.ass01.layers.DAL.Schemas;

public class EBikeSchemaImpl implements EBikeSchema {

    final int ID;
    final int battery;
    final int state;
    final int positionX;
    final int positionY;

    public EBikeSchemaImpl(int ID, int battery, int state, int positionX, int positionY) {
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
    public int getState() {
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

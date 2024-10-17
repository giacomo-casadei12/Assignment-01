package sap.ass01.layers.DAL.Schemas;

public class MutableEBikeImpl implements MutableEBike {

    private int ID;
    private int battery;
    private String state;
    private int positionX;
    private int positionY;

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public void setBattery(int battery) {
        this.battery = battery;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void setPositionX(int x) {
        this.positionX = x;
    }

    @Override
    public void setPositionY(int y) {
        this.positionY = y;
    }

    @Override
    public int ID() {
        return this.ID;
    }

    @Override
    public int battery() {
        return this.battery;
    }

    @Override
    public String state() {
        return this.state;
    }

    @Override
    public int positionX() {
        return this.positionX;
    }

    @Override
    public int positionY() {
        return this.positionY;
    }
}

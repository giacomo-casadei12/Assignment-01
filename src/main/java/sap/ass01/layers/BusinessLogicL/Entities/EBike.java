package sap.ass01.layers.BusinessLogicL.Entities;

import sap.ass01.layers.DataAccessL.Schemas.MutableEBike;

/**
 *  The Immutable representation of Electric Bike
 *  used in Persistence Layer and above.
 */
public interface EBike extends MutableEBike {

    @Override
    int id();

    @Override
    int battery();

    @Override
    String state();

    @Override
    int positionX();

    @Override
    int positionY();
}

package sap.ass01.layers.BusinessLogicL.Entities;

import sap.ass01.layers.DataAccessL.Schemas.MutableRide;

/**
 * The Immutable representation of Ride used in Persistence Layer and above.
 */
public interface Ride extends MutableRide {

    @Override
    int id();

    @Override
    String startDate();

    @Override
    String endDate();

    @Override
    int userID();

    @Override
    int eBikeID();

}

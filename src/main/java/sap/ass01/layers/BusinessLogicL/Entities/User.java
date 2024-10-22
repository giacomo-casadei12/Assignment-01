package sap.ass01.layers.BusinessLogicL.Entities;

import sap.ass01.layers.DataAccessL.Schemas.MutableUser;

/**
 * The Immutable representation of User used in Persistence Layer and above.
 */
public interface User extends MutableUser {

    @Override
    int ID();

    @Override
    String userName();

    @Override
    int credit();

    @Override
    boolean admin();

}


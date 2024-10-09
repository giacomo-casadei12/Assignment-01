package sap.ass01.layers.DAL.Schemas;

import java.util.Date;

public interface Ride {

    int getID();
    Date getStartDate();
    Date getEndDate();
    int getUserID();
    int getEBikeID();
}

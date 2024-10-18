package sap.ass01.layers.PersistenceL.Persistence;

import sap.ass01.layers.PersistenceL.Entities.EBike;
import sap.ass01.layers.utils.EBikeState;

import java.util.List;

public interface EBikePersistence {

    List<EBike> getAllEBikes(int positionX, int positionY, boolean available);
    EBike getEBike(int id);
    boolean createEBike(int positionX, int positionY);
    boolean updateEBike(int id, int battery, EBikeState state, int positionX, int positionY);
    boolean updateEbikePosition(int id, int positionX, int positionY);
    boolean deleteEBike(int id);

}

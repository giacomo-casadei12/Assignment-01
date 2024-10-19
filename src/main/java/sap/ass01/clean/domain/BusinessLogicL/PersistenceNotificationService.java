package sap.ass01.clean.domain.BusinessLogicL;

import sap.ass01.clean.domain.entities.EBike;
import sap.ass01.clean.domain.entities.User;

public interface PersistenceNotificationService {

    void notifyUpdateUser(User user);

    void notifyUpdateEBike(EBike bike);

    void notifyEndRide(User user, EBike bike);

}

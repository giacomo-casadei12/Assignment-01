package sap.ass01.clean.infrastructure;

import sap.ass01.clean.domain.BusinessLogicL.PersistenceNotificationService;
import sap.ass01.clean.domain.BusinessLogicL.RideManager;
import sap.ass01.clean.domain.BusinessLogicL.RideManagerImpl;
import sap.ass01.clean.domain.ports.AppManager;
import sap.ass01.clean.domain.ports.AppManagerImpl;
import sap.ass01.clean.domain.ports.dataAccessPorts.EBikeDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.RideDA;
import sap.ass01.clean.domain.ports.dataAccessPorts.UserDA;
import sap.ass01.clean.infrastructure.DataAccessL.EBikeDB;
import sap.ass01.clean.infrastructure.DataAccessL.RideDB;
import sap.ass01.clean.infrastructure.DataAccessL.UserDB;
import sap.ass01.clean.infrastructure.Web.WebController;

/**
 * Main class for EBikeCesena server.
 */
public class Main {

    /**
     * The entry point of server application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        RideManager r = new RideManagerImpl();
        RideDA rideDA = new RideDB();
        EBikeDA bikeDA = new EBikeDB();
        UserDA userDA = new UserDB();
        AppManager am = new AppManagerImpl(r, rideDA, bikeDA, userDA);
        new WebController(am);
        r.attachPersistenceNotificationService((PersistenceNotificationService) am);
    }

}

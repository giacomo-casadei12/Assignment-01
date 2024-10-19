package sap.ass01.clean;

import sap.ass01.clean.domain.BusinessLogicL.PersistenceNotificationService;
import sap.ass01.clean.domain.BusinessLogicL.RideManager;
import sap.ass01.clean.domain.BusinessLogicL.RideManagerImpl;
import sap.ass01.clean.domain.ports.AppManager;
import sap.ass01.clean.domain.ports.AppManagerImpl;
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
        AppManager am = new AppManagerImpl(r);
        new WebController(am);
        r.attachPersistenceNotificationService((PersistenceNotificationService) am);
    }

}

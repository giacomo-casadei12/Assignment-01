package sap.ass01.layers.BLL;

import sap.ass01.layers.BLL.Logic.RideManager;
import sap.ass01.layers.BLL.Logic.RideManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManagerImpl;
import sap.ass01.layers.BLL.Persistence.PersistenceManager;

public class WebController {

    final private RideManager iManager;
    final private PersistenceManager pManager;

    public WebController() {
        this.pManager = new PersistenceManagerImpl();
        this.iManager = new RideManagerImpl(this.pManager);
    }
}

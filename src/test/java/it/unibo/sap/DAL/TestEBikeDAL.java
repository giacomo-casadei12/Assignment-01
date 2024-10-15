package it.unibo.sap.DAL;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.DAL.DB.EBikeDA;
import sap.ass01.layers.DAL.DB.EBikeDB;
import sap.ass01.layers.DAL.Schemas.EBike;
import sap.ass01.layers.utils.EBikeState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestEBikeDAL {

    final EBikeDA eBikeDA;

    public TestEBikeDAL() {
        eBikeDA = new EBikeDB();
    }

    //there's should always be at least one bike
    @Test
    public void getAllEBikes() {
        List<EBike> x = eBikeDA.getAllEBikes();
        assertFalse(x.isEmpty());
    }

    @Test
    public void createUpdateDeleteEBike() {
        boolean b = eBikeDA.createEBike(22,22);
        assertTrue(b);
        List<EBike> bis = eBikeDA.getAllEBikesNearby(30,30);
        assertFalse(bis.isEmpty());
        assertTrue(bis.stream().anyMatch(eBike -> eBike.getPositionX() == 22 && eBike.getPositionY() == 22));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = bis.stream().
                                                                     filter(eBike -> eBike.getPositionX() == 22 && eBike.getPositionY() == 22).
                                                                     findFirst().
                                                                     get().getID();
        b = eBikeDA.updateEBike(id,50, EBikeState.IN_USE,50,50);
        assertTrue(b);
        EBike bi = eBikeDA.getEBikeById(id);
        assertNotNull(bi);
        assertEquals(50,bi.getBattery());
        assertEquals(1,EBikeState.valueOf(bi.getState()).ordinal());
        assertEquals(50,bi.getPositionX());
        assertEquals(50,bi.getPositionY());
        b = eBikeDA.deleteEBike(id);
        assertTrue(b);
        bi = eBikeDA.getEBikeById(id);
        assertNull(bi);
    }

}

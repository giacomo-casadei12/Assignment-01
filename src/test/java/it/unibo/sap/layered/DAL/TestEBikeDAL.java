package it.unibo.sap.layered.DAL;

import org.junit.Test;
import sap.ass01.layers.DataAccessL.DB.EBikeDA;
import sap.ass01.layers.DataAccessL.DB.EBikeDB;
import sap.ass01.layers.DataAccessL.Schemas.MutableEBike;
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
        List<MutableEBike> x = eBikeDA.getAllEBikes();
        assertFalse(x.isEmpty());
    }

    @Test
    public void createUpdateDeleteEBike() {
        boolean b = eBikeDA.createEBike(22,22);
        assertTrue(b);
        List<MutableEBike> bis = eBikeDA.getAllEBikesNearby(30,30);
        assertFalse(bis.isEmpty());
        assertTrue(bis.stream().anyMatch(eBike -> eBike.positionX() == 22 && eBike.positionY() == 22));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = bis.stream().
                                                                     filter(eBike -> eBike.positionX() == 22 && eBike.positionY() == 22).
                                                                     findFirst().
                                                                     get().ID();
        b = eBikeDA.updateEBike(id,50, EBikeState.IN_USE,50,50);
        assertTrue(b);
        MutableEBike bi = eBikeDA.getEBikeById(id);
        assertNotNull(bi);
        assertEquals(50,bi.battery());
        assertEquals(1,EBikeState.valueOf(bi.state()).ordinal());
        assertEquals(50,bi.positionX());
        assertEquals(50,bi.positionY());
        b = eBikeDA.deleteEBike(id);
        assertTrue(b);
        bi = eBikeDA.getEBikeById(id);
        assertNull(bi);
    }

}

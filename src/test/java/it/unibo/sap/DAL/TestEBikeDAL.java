package it.unibo.sap.DAL;

import org.junit.jupiter.api.Test;
import sap.ass01.layers.DAL.DB.EBikeDA;
import sap.ass01.layers.DAL.DB.EBikeDB;
import sap.ass01.layers.DAL.Schemas.EBikeSchema;

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
        List<EBikeSchema> x = eBikeDA.getAllEBikes();
        assertFalse(x.isEmpty());
    }

    @Test
    public void createUpdateDeleteEBike() {
        boolean b = eBikeDA.createEBike(22,22);
        assertTrue(b);
        List<EBikeSchema> bis = eBikeDA.getAllEBikesNearby(30,30);
        assertNotNull(bis);
        assertTrue(bis.stream().anyMatch(eBikeSchema -> eBikeSchema.getPositionX() == 22 && eBikeSchema.getPositionY() == 22));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = bis.stream().
                                                                     filter(eBikeSchema -> eBikeSchema.getPositionX() == 22 && eBikeSchema.getPositionY() == 22).
                                                                     findFirst().
                                                                     get().getID();
        b = eBikeDA.updateEBike(id,50,1,50,50);
        assertTrue(b);
        EBikeSchema bi = eBikeDA.getEBikeById(id);
        assertNotNull(bi);
        assertEquals(50,bi.getBattery());
        assertEquals(1,bi.getState());
        assertEquals(50,bi.getPositionX());
        assertEquals(50,bi.getPositionY());
        b = eBikeDA.deleteEBike(id);
        assertTrue(b);
        bi = eBikeDA.getEBikeById(id);
        assertNull(bi);
    }

}

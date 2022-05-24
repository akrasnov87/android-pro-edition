package ru.mobnius.core.data.packager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetaSizeUnitTest {

    @Test
    public void toJsonString(){
        MetaSize ms = new MetaSize(950, MetaSize.PROCESSED, "NML");
        assertEquals("NML950.........3", ms.toJsonString());
        ms.metaSize = 1000;
        ms.status = MetaSize.CREATED;
        assertEquals("NML1000........0", ms.toJsonString());
    }
}

package com.mobwal.pro;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import com.mobwal.pro.models.DemoPlaceItem;

public class DemoManagerTest {


    @Test
    public void getObjects() throws IOException {
        String url = "http://api.opentripmap.com/0.1/ru/places/radius?radius=10000&lon=38.364285&lat=59.855685&format=geojson&apikey=" + DemoManager.API_KEY;
        DemoPlaceItem[] items = DemoManager.getObjects(new URL(url));
        assertTrue(items.length > 0);
    }
}
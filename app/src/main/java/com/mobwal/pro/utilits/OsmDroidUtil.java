package com.mobwal.pro.utilits;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.mobwal.pro.R;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.Collection;

public class OsmDroidUtil {

    /**
     * Включить компас на карте
     * @param context контекст
     * @param view карта
     */
    public static void enableCompass(@NonNull Context context, @NonNull MapView view) {
        CompassOverlay compassOverlay = new CompassOverlay(context, view);
        compassOverlay.enableCompass();
        view.getOverlays().add(compassOverlay);
    }

    /**
     * Упаковка массива точек для "обхода"
     * @param points коллекция точек
     * @return упаковка
     */
    public static BoundingBox toBoxing(@NonNull Collection<GeoPoint> points) {
        return toBoxing(points, 10);
    }

    /**
     * Упаковка массива точек для "обхода"
     * @param points коллекция точек
     * @param border рамки
     * @return упаковка
     */
    public static BoundingBox toBoxing(@NonNull Collection<GeoPoint> points, int border) {

        double nord = 0, sud = 0, ovest = 0, est = 0;

        int idx = 0;
        for (GeoPoint point: points) {
            if(point == null) {
                idx++;
                continue;
            }

            double lat = point.getLatitude();
            double lon = point.getLongitude();

            if ((idx == 0) || (lat > nord)) nord = lat;
            if ((idx == 0) || (lat < sud)) sud = lat;
            if ((idx == 0) || (lon < ovest)) ovest = lon;
            if ((idx == 0) || (lon > est)) est = lon;

            idx++;
        }

        return new BoundingBox(nord + border, est + border, sud - border, ovest - border);
    }
}

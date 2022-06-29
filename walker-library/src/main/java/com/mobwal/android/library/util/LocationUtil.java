package com.mobwal.android.library.util;

import android.location.Location;

import java.util.Locale;

public class LocationUtil {
    public static String toString(Location location, int decimals) {
        return String.format(Locale.getDefault(), "%." + decimals + "f ; %." + decimals + "f", location.getLatitude(), location.getLongitude());
    }

    public static String toString(Location location) {
        return toString(location, 4);
    }
}

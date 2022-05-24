package ru.mobnius.core.utils;

import android.location.Location;
import android.location.LocationManager;

import java.util.Date;
import java.util.Locale;

import ru.mobnius.core.data.configuration.PreferencesManager;

public class LocationUtil {
    /**
     * Создание объекта Location
     * @param n_longitude долгота
     * @param n_latitude широта
     * @return объект Location
     */
    public static Location getLocation(double n_longitude, double n_latitude) {
        Location location = new Location("CODE");
        location.setLatitude(n_latitude);
        location.setLongitude(n_longitude);
        location.setTime(new Date().getTime());
        return location;
    }

    public static String toString(Location location, int decimals) {
        return String.format(new Locale("ru", "RU"), "%." + decimals + "f ; %." + decimals + "f", location.getLatitude(), location.getLongitude());
    }

    public static String toString(Location location) {
        return toString(location, 4);
    }

    /**
     * получение провайдера местоположения
     * @param locationManager
     * @param providerName имя провайдера
     * @return найденный провайдер
     */
    public static String getProviderName(LocationManager locationManager, String providerName) {
        if(!locationManager.isProviderEnabled(providerName)) {
            providerName = LocationManager.NETWORK_PROVIDER;
            if(!locationManager.isProviderEnabled(providerName)) {
                providerName = LocationManager.PASSIVE_PROVIDER;
                if(!locationManager.isProviderEnabled(providerName)) {
                    if (locationManager.getAllProviders().size() > 0) {
                        providerName = locationManager.getAllProviders().get(0);
                    } else {
                        return null;
                    }
                }
            }
        }

        return providerName;
    }
}
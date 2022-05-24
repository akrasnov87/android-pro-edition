package ru.mobnius.core.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import java.util.Objects;

import ru.mobnius.core.data.audit.AuditManager;
import ru.mobnius.core.data.audit.OnAuditListeners;
import ru.mobnius.core.data.logger.Logger;

public class LocationChecker {
    public static final int LOCATION_OFF = 0;
    public static final int LOCATION_ON_LOW_ACCURACY = 1;

    public static void start(OnLocationAvailable checker) {
        final Context context = (Context) checker;

        try {
            PackageManager pm = context.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null){
                return;
            }
            if (hasGps && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                checker.onLocationAvailable(LOCATION_OFF);
            } else {
                if(!hasGps) {
                    AuditManager.getInstance().write("", "NOT_GPS", OnAuditListeners.Level.HIGH);
                } else {
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        checker.onLocationAvailable(LOCATION_ON_LOW_ACCURACY);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }

    public interface OnLocationAvailable {
        void onLocationAvailable(int mode);
    }
}

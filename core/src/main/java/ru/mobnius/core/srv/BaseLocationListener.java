package ru.mobnius.core.srv;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import ru.mobnius.core.utils.NetworkInfoUtil;

public abstract class BaseLocationListener extends BaseListener
        implements LocationListener {

    public static final String TRACK_ONLINE = "online";
    public static final String TRACK_OFFLINE = "offline";

    public BaseLocationListener(Context context, long userId) {
        super(context, userId);
    }

    public abstract void onLocationChanged(Location location, String networkStatus);

    @Override
    public void onLocationChanged(Location location) {
        String networkStatus;

        if(NetworkInfoUtil.isNetworkAvailable(mContext)) {
            networkStatus = TRACK_ONLINE;
        } else {
            networkStatus = TRACK_OFFLINE;
        }
        onLocationChanged(location, networkStatus);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

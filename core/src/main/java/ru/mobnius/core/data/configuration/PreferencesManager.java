package ru.mobnius.core.data.configuration;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.camera.CameraUtil;

public class PreferencesManager extends AbstractPreferencesManager {
    public final static String SECURITY = "SECURITY";
    public final static String ABOUT = "ABOUT";
    public final static String SERVICE = "SERVICE";

    public final static String SERVER_APP_VERSION = "SERVER_APP_VERSION";
    public final static String MBL_ZIP = "MBL_ZIP";
    //public final static String SYNC_PROTOCOL = "v1";
    public final static String SYNC_PROTOCOL_v2 = "v2";
    public final static String MAILER_PROTOCOL = "v1";
    public final static String APP_VERSION = "MBL_APP_VERSION";
    public static final String DEBUG = "MBL_DEBUG";
    public static final String GENERATED_ERROR = "MBL_GENERATED_ERROR";
    public static final String PIN = "MBL_PIN";
    public static final String MBL_GEO_CHECK = "MBL_GEO_CHECK";
    public static final String MBL_LOG = "MBL_LOG";
    public static final String MBL_LOCATION = "MBL_LOCATION";
    public static final String MBL_DISTANCE = "MBL_DISTANCE";
    public static final String MBL_LOG_LOW = "LOW";
    public static final String MBL_GENERATED_NOTIFICATION = "MBL_GENERATED_NOTIFICATION";
    public final static String MBL_RESET = "MBL_RESET";

    public static final String MBL_BG_SYNC_INTERVAL = "MBL_BG_SYNC_INTERVAL";
    public static final String MBL_TRACK_INTERVAL = "MBL_TRACK_INTERVAL";
    public static final String MBL_TELEMETRY_INTERVAL = "MBL_TELEMETRY_INTERVAL";
    public static final String MBL_BASE_URL = "MBL_BASE_URL";
    public static final String MBL_IMAGE_FORMAT = "MBL_IMAGE_FORMAT";

    public static final String MBL_VIDEO_DURATION = "MBL_VIDEO_DURATION";
    public static final String MBL_VIDEO_QUALITY = "MBL_VIDEO_QUALITY";
    public static final String MBL_TRACK_LOCATION = "MBL_TRACK_LOCATION";
    public static final String MBL_POINT_FILTER = "MBL_POINT_FILTER";
    public static final String MBL_WATERMARK = "MBL_WATERMARK";
    public static final String MBL_REQUIRE_IMAGE = "MBL_REQUIRE_IMAGE";
    public final static String MBL_PIN = "MBL_PIN";

    private static PreferencesManager preferencesManager;

    public static PreferencesManager getInstance(){
        return preferencesManager;
    }
    public static void createInstance(Context context, String preferenceName) {
        preferencesManager = new PreferencesManager(context, preferenceName);
    }

    public PreferencesManager(Context context, String preferenceName){
        super(context, preferenceName);
    }

    public boolean isDebug() {
        return getDefaultBooleanValue(DEBUG, false);
    }

    public void setDebug(boolean value) {
        getSharedPreferences().edit().putBoolean(DEBUG, value).apply();
    }

    public boolean getZip() {
        return getDefaultBooleanValue(MBL_ZIP, false);
    }

    public boolean isPin() {
        return getDefaultBooleanValue(MBL_PIN, false);
    }

    public boolean isPinAuth() {
        return getDefaultBooleanValue(PIN, false);
    }

    public void setPinAuth(boolean value) {
        getSharedPreferences().edit().putBoolean(PIN, value).apply();
    }

    public int getSyncInterval() {
        return getDefaultIntValue(MBL_BG_SYNC_INTERVAL, 5 * 60 * 1000);
    }

    public int getTrackingInterval() {
        return getDefaultIntValue(MBL_TRACK_INTERVAL, 60 * 60 * 1000);
    }


    public int getTelemetryInterval() {
        return getDefaultIntValue(MBL_TELEMETRY_INTERVAL, 60 * 60 * 1000);
    }

    public boolean isGeoCheck() { return getDefaultBooleanValue(MBL_GEO_CHECK, true); }

    public boolean isWaterMark() { return getDefaultBooleanValue(MBL_WATERMARK, false); }
    public boolean isRequireImage() { return getDefaultBooleanValue(MBL_REQUIRE_IMAGE, false); }

    public String getLog() {
        return getDefaultStringValue(MBL_LOG, "FULL");
    }

    public String getLocation() {
        return getDefaultStringValue(MBL_LOCATION, "").length() == 0 ? LocationManager.NETWORK_PROVIDER : getDefaultStringValue(MBL_LOCATION, "network");
    }

    public String getTrackLocation() {
        return getDefaultStringValue(MBL_TRACK_LOCATION, LocationManager.NETWORK_PROVIDER);
    }

    public String getImageFormat() {
        return getDefaultStringValue(MBL_IMAGE_FORMAT, CameraUtil.JPEG_IMAGE_FORMAT);
    }

    public Bitmap.CompressFormat getBitmapFormat() {
        String imageFormat = getImageFormat();
        switch (imageFormat) {
            case "webp":
                return Bitmap.CompressFormat.WEBP;

            default:
            case "jpeg":
                return Bitmap.CompressFormat.JPEG;
        }
    }

    public int getDistance() {
        return getDefaultIntValue(MBL_DISTANCE, -1) == -1 ? 100 : getDefaultIntValue(MBL_DISTANCE, 100);
    }

    public int getVideoDuraction() {
        return getDefaultIntValue(MBL_VIDEO_DURATION, 30);
    }

    public String getVideoQuality() {
        return getDefaultStringValue(MBL_VIDEO_QUALITY, "LOW");
    }

    public boolean isPointsFiltered(){ return getDefaultBooleanValue(MBL_POINT_FILTER, false);}

    public void setPointsFiltered(boolean value){ getSharedPreferences().edit().putBoolean(MBL_POINT_FILTER, value).apply();}
}

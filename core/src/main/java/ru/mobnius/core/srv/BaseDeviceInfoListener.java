package ru.mobnius.core.srv;

import android.content.Context;
import android.os.Build;

import java.util.Date;

import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.HardwareUtil;
import ru.mobnius.core.utils.VersionUtil;

public abstract class BaseDeviceInfoListener extends BaseListener {
    public BaseDeviceInfoListener(Context context, long userId) {
        super(context, userId);
    }

    public MobileDevice getMobileDevice() {
        MobileDevice mobileDevices = new MobileDevice();
        mobileDevices.b_debug = PreferencesManager.getInstance().isDebug();
        mobileDevices.c_version = VersionUtil.getVersionName(mContext);
        mobileDevices.c_architecture = System.getProperty("os.arch");
        mobileDevices.d_date = DateUtil.convertDateToString(new Date());
        mobileDevices.c_imei = HardwareUtil.getNumber(mContext);
        mobileDevices.c_phone_model = Build.MODEL;
        mobileDevices.c_sdk = String.valueOf(Build.VERSION.SDK_INT);
        mobileDevices.c_os = Build.VERSION.RELEASE;
        mobileDevices.fn_user = getUserId();

        return mobileDevices;
    }

    public abstract void run();

}

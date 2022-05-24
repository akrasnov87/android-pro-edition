package ru.mobnius.core.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import ru.mobnius.core.data.configuration.DefaultPreferencesManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.logger.Logger;


public class DeviceUtil {
    public static final int BATTERY_SAVE_MODE_REQUEST_CODE = 1455;

    private static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity")),
            new Intent().setComponent(new ComponentName("com.transsion.phonemanager", "com.itel.autobootmanager.activity.AutoBootMgrActivity"))
    };

    public static void checkDevices(Activity activity) {
        if (DefaultPreferencesManager.getInstance().isNotifiedAboutBatterySave()) {
            return;
        }
        for (Intent intent : POWERMANAGER_INTENTS)
            if (activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setPositiveButton("Перейти", (dialogInterface, i) -> {
                    try {
                        for (Intent intent1 : POWERMANAGER_INTENTS)
                            if (activity.getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                activity.startActivityForResult(intent1, BATTERY_SAVE_MODE_REQUEST_CODE);
                                break;
                            }
                    }
                    catch (Exception e) {
                            Logger.error(e);
                        }
                    DefaultPreferencesManager.getInstance().setNotifiedAboutBatterySave(true);
                });
                AlertDialog alert = adb.create();
                alert.setTitle("ВНИМАНИЕ");
                alert.setMessage("Обнаружено что на вашем устройстве " + android.os.Build.MANUFACTURER.toUpperCase() + " присутствует режим автоматического управления приложениями, " +
                        "который не позволяет нашему приложению работать корректно. Необходимо перейти в настройки и активировать " +
                        "режим 'Работа в фоновом режиме' либо установить режим 'Без ограничений'");
                alert.setCancelable(true);
                alert.show();
                break;
            }
    }
}

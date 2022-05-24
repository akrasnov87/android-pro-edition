package ru.mobnius.core.utils;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import ru.mobnius.core.R;
import ru.mobnius.core.data.GlobalSettings;

public class ThemeUtil {
    public static void changeColor(AppCompatActivity activity) {
        if(GlobalSettings.ENVIRONMENT.equals("dev")) {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorFloating));
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorFloating));
        }
        if(GlobalSettings.ENVIRONMENT.equals("test")) {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorSecondary));
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorSecondary));
        }
        if(GlobalSettings.ENVIRONMENT.equals("release")) {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }

        if(GlobalSettings.ENVIRONMENT.equals("demo")) {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorSuccessDark));
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorSuccessDark));
        }
    }
}

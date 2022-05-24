package ru.mobnius.core.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.SwitchPreference;
import java.util.Objects;
import ru.mobnius.core.R;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.BasePreferenceFragmentCompact;
import ru.mobnius.core.ui.CoreActivity;

public class ServiceActivity extends CoreActivity {

    private static void setPrefFragment(AppCompatActivity context) {
        context.getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new AboutPrefFragment()).commit();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, ServiceActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_container);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Настройки предоставленные сервером");
        setPrefFragment(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.SETTING_SERVICE;
    }

    public static class AboutPrefFragment extends BasePreferenceFragmentCompact {
        private SwitchPreference spGeoCheck;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.service_pref);

            findPreference(PreferencesManager.MBL_BASE_URL).setSummary(GlobalSettings.getConnectUrl());
            findPreference(PreferencesManager.MBL_BG_SYNC_INTERVAL).setSummary(String.format("Интервал синхронизации фоновых данных: %s мин.", PreferencesManager.getInstance().getSyncInterval() / 60000));
            findPreference(PreferencesManager.MBL_TRACK_INTERVAL).setSummary(String.format("Интервал получения гео-данных: %s мин.", PreferencesManager.getInstance().getTrackingInterval() / 60000));
            findPreference(PreferencesManager.MBL_TELEMETRY_INTERVAL).setSummary(String.format("Интервал сбора показаний мобильного устройства: %s мин.", PreferencesManager.getInstance().getTelemetryInterval() / 60000));
            findPreference(PreferencesManager.MBL_LOG).setSummary(String.format("Режим логирования действий: %s", PreferencesManager.getInstance().getLog()));
            findPreference(PreferencesManager.MBL_LOCATION).setSummary(String.format("Режим получения координат: %s", PreferencesManager.getInstance().getLocation()));
            findPreference(PreferencesManager.MBL_TRACK_LOCATION).setSummary(String.format("Режим фонового получения координат: %s", PreferencesManager.getInstance().getTrackLocation()));
            findPreference(PreferencesManager.MBL_DISTANCE).setSummary(String.format("Минимальная дистанция для обновления координат: %s м.", PreferencesManager.getInstance().getDistance()));
            findPreference(PreferencesManager.MBL_VIDEO_DURATION).setSummary(String.format("Продолжительность: %s сек.", PreferencesManager.getInstance().getVideoDuraction()));
            findPreference(PreferencesManager.MBL_VIDEO_QUALITY).setSummary(String.format("Качество: %s", PreferencesManager.getInstance().getVideoQuality()));
            findPreference(PreferencesManager.MBL_ZIP).setSummary(String.format("Сжатие данных при обмене информацией с сервером: %s", PreferencesManager.getInstance().getZip() ? "Да" : "Нет"));

            this.spGeoCheck = findPreference(PreferencesManager.MBL_GEO_CHECK);
        }

        @Override
        public void onResume() {
            super.onResume();
            this.spGeoCheck.setSummary(PreferencesManager.getInstance().isGeoCheck() ? "включена" : "отключена");
            this.spGeoCheck.setChecked(PreferencesManager.getInstance().isGeoCheck());
        }

        @Override
        public int getExceptionCode() {
            return IExceptionCode.SETTING_SERVICE;
        }
    }
}

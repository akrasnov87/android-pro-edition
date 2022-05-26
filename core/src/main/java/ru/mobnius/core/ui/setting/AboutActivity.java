package ru.mobnius.core.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import java.io.IOException;
import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.NotificationManager;
import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.BasePreferenceFragmentCompact;
import ru.mobnius.core.ui.CoreActivity;
import ru.mobnius.core.ui.fragment.AboutUpdateDialogFragment;
import ru.mobnius.core.utils.VersionUtil;

public class AboutActivity extends CoreActivity {
    private static void setPrefFragment(AppCompatActivity context) {
        context.getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new AboutPrefFragment()).commit();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_container);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(R.string.settings);
        setPrefFragment(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    public int getExceptionCode() {
        return IExceptionCode.SETTING_ABOUT;
    }

    public static class AboutPrefFragment extends BasePreferenceFragmentCompact
            implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private int clickToVersion = 0;

        private ServerAppVersionAsyncTask mServerAppVersionAsyncTask;
        private Preference pCreateError;
        public Preference pCreateNotification;
        public Preference pServerVersion;
        public Preference pVersion;
        private SwitchPreference spDebug;
        private String mBaseUrl;

        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.about_pref);

            pServerVersion = findPreference(PreferencesManager.SERVER_APP_VERSION);

            pVersion = findPreference(PreferencesManager.APP_VERSION);
            Objects.requireNonNull(pVersion).setOnPreferenceClickListener(this);

            findPreference(PreferencesManager.SERVICE).setOnPreferenceClickListener(this);

            spDebug = findPreference(PreferencesManager.DEBUG);
            Objects.requireNonNull(spDebug).setEnabled(PreferencesManager.getInstance().isDebug());
            spDebug.setOnPreferenceChangeListener(this);

            pCreateError = findPreference(PreferencesManager.GENERATED_ERROR);
            Objects.requireNonNull(pCreateError).setVisible(PreferencesManager.getInstance().isDebug());
            pCreateError.setOnPreferenceClickListener(this);

            pCreateNotification = findPreference(PreferencesManager.MBL_GENERATED_NOTIFICATION);
            Objects.requireNonNull(pCreateNotification).setOnPreferenceClickListener(this);
            pCreateNotification.setVisible(PreferencesManager.getInstance().isDebug());

            mBaseUrl = GlobalSettings.getConnectUrl();
        }

        public void onResume() {
            super.onResume();
            pVersion.setSummary(VersionUtil.getVersionName(requireActivity()));

            spDebug.setSummary(String.format("Режим отладки: %s", PreferencesManager.getInstance().isDebug() ? "включен" : "отключен"));
            spDebug.setChecked(PreferencesManager.getInstance().isDebug());

            mServerAppVersionAsyncTask = new ServerAppVersionAsyncTask();
            mServerAppVersionAsyncTask.execute(mBaseUrl);
        }

        public int getExceptionCode() {
            return IExceptionCode.SETTING_ABOUT;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case PreferencesManager.APP_VERSION:
                    clickToVersion++;
                    if (clickToVersion >= 6) {
                        PreferencesManager.getInstance().setDebug(true);
                        spDebug.setChecked(true);
                        spDebug.setEnabled(true);
                        pCreateError.setVisible(true);
                        pCreateNotification.setVisible(true);

                        Toast.makeText(getActivity(), "Режим отладки активирован.", Toast.LENGTH_SHORT).show();
                        clickToVersion = 0;
                    }
                    break;

                case PreferencesManager.GENERATED_ERROR:
                    //noinspection ResultOfMethodCallIgnored
                    Integer.parseInt("Проверка обработки ошибок");
                    break;

                case PreferencesManager.MBL_GENERATED_NOTIFICATION:
                    NotificationManager notificationManager = new NotificationManager(mBaseUrl, Authorization.getInstance().getUser().getCredentials().getToken());
                    notificationManager.sendMessage("Тестовое сообщение", 1, null);
                    break;

                case PreferencesManager.SERVICE:
                    startActivity(ServiceActivity.getIntent(getContext()));
                    break;
            }
            return false;
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (PreferencesManager.DEBUG.equals(preference.getKey())) {
                boolean debugValue = Boolean.parseBoolean(String.valueOf(newValue));
                spDebug.setEnabled(debugValue);
                spDebug.setSummary(String.format("Режим отладки: %s", debugValue ? "включен" : "отключен"));
                spDebug.setEnabled(debugValue);

                PreferencesManager.getInstance().setDebug(debugValue);
                pCreateError.setVisible(debugValue);
                pCreateNotification.setVisible(debugValue);
            }
            return true;
        }

        public void onDestroy() {
            super.onDestroy();
            if(mServerAppVersionAsyncTask != null) {
                mServerAppVersionAsyncTask.cancel(true);
                mServerAppVersionAsyncTask = null;
            }
        }

        @SuppressLint("StaticFieldLeak")
        private class ServerAppVersionAsyncTask extends AsyncTask<String, Void, String> {
            public String doInBackground(String... strings) {
                mBaseUrl = strings[0];
                try {
                    return RequestManager.version(AboutPrefFragment.this.requireActivity(), strings[0]);
                } catch (IOException e) {
                    return "0.0.0.0";
                }
            }

            public void onPostExecute(final String s) {
                super.onPostExecute(s);
                if (!isAdded()) {
                    return;
                }
                Context context = AboutPrefFragment.this.requireActivity();
                if (pServerVersion == null) {
                    return;
                }
                if (s.equals("0.0.0.0")) {
                    pServerVersion.setVisible(false);
                    if (pVersion != null) {
                        pVersion.setSummary("Установлена последняя версия " + VersionUtil.getVersionName(context));
                    }
                } else if (VersionUtil.isUpgradeVersion(context, s, PreferencesManager.getInstance().isDebug())) {
                    pServerVersion.setVisible(true);
                    pServerVersion.setSummary("Доступна новая версия " + s);
                    pServerVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (VersionUtil.verifyInstallerId(requireActivity())) {
                                final String appPackageName = requireActivity().getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            } else {
                                AboutUpdateDialogFragment aboutUpdateDialogFragment = new AboutUpdateDialogFragment();
                                aboutUpdateDialogFragment.bind(s, 0);
                                aboutUpdateDialogFragment.show(requireActivity().getSupportFragmentManager(), "about-update");
                            }
                            return true;
                        }
                    });

                    if (pVersion != null) {
                        pVersion.setSummary(VersionUtil.getVersionName(context));
                    }
                } else {
                    pServerVersion.setVisible(false);
                    if (pVersion != null) {
                        pVersion.setSummary("Установлена последняя версия " + VersionUtil.getVersionName(context));
                    }
                }
            }
        }
    }
}

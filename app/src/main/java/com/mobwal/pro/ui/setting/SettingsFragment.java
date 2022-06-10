package com.mobwal.pro.ui.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.PrefManager;
import com.mobwal.pro.CustomLayoutManager;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.utilits.ActivityUtil;
import com.mobwal.android.library.util.VersionUtil;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private int clickToVersion = 0;

    private PrefManager mPrefManager;

    private Preference mVersionPreference;
    private SwitchPreferenceCompat mErrorReportingPreference;
    private Preference mDebugModePreference;
    private Preference mPinPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_pref, rootKey);

        mPrefManager = new PrefManager(requireContext());

        mVersionPreference = findPreference("app_version");
        Objects.requireNonNull(mVersionPreference).setOnPreferenceClickListener(this);

        Preference resetSettingsPreference = findPreference("reset_settings");
        Objects.requireNonNull(resetSettingsPreference).setOnPreferenceClickListener(this);

        mErrorReportingPreference = findPreference("error_reporting");
        Objects.requireNonNull(mErrorReportingPreference).setOnPreferenceChangeListener(this);

        mDebugModePreference = findPreference("debug");
        Objects.requireNonNull(mDebugModePreference).setOnPreferenceClickListener(this);

        mPinPreference = findPreference("pin");
        Objects.requireNonNull(mPinPreference).setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogManager.getInstance().info("Настройки");
        setHasOptionsMenu(true);

        Objects.requireNonNull(mVersionPreference).setSummary(VersionUtil.getVersionName(requireActivity()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setup() {
        boolean debug = mPrefManager.get("debug", false);
        mDebugModePreference.setVisible(debug);

        boolean pinPrefValue = mPrefManager.get("pin", false);
        Objects.requireNonNull(mPinPreference).setSummary(pinPrefValue ? R.string.pin_settings_summary_on: R.string.pin_settings_summary_off);

        boolean errorReportingPrefValue = mPrefManager.get("error_reporting", false);
        mErrorReportingPreference.setChecked(errorReportingPrefValue);
    }

    @Override
    public void onResume() {
        super.onResume();

        setup();

        clickToVersion = 0;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.getKey().equals("error_reporting")) {
            boolean errorReportingPrefValue = Boolean.parseBoolean(String.valueOf(newValue));
            mPrefManager.put("error_reporting", errorReportingPrefValue);
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals("reset_settings")) {

            AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
            adb.setPositiveButton(R.string.yes, (dialog, which) -> {
                mPrefManager.clearAll();
                Toast.makeText(getActivity(), R.string.reset_setting_success, Toast.LENGTH_SHORT).show();
                setup();
            });

            adb.setNegativeButton(getResources().getString(R.string.no), null);

            AlertDialog alert = adb.create();
            alert.setTitle(R.string.reset_settings);
            alert.setMessage(getString(R.string.reset_settings_confirm));
            alert.show();
        }

        if (preference.getKey().equals("app_version")) {
            clickToVersion++;
            if (clickToVersion >= 6) {
                Toast.makeText(getActivity(), R.string.debug_mode_summary_on, Toast.LENGTH_SHORT).show();
                clickToVersion = 0;
                mDebugModePreference.setVisible(true);

                mPrefManager.put("debug", true);
            }
        } else {
            clickToVersion = 0;
        }

        if(preference.getKey().equals("debug")) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_debug_settings);
        }

        if(preference.getKey().equals("pin")) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_pin_settings);
        }

        return false;
    }
}
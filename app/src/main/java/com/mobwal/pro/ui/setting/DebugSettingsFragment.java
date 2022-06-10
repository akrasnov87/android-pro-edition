package com.mobwal.pro.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.PrefManager;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.utilits.ActivityUtil;

/**
 * Фрагмент для отображения параметров предназначенных для разработчика
 */
public class DebugSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    private PrefManager mPrefManager;

    private SwitchPreferenceCompat mDebugModePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.debug_pref, rootKey);

        mPrefManager = new PrefManager(requireContext());

        mDebugModePreference = findPreference("debug");
        Objects.requireNonNull(mDebugModePreference).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("Настройки. Режим отладки.");
        setHasOptionsMenu(true);

        boolean debug = mPrefManager.get("debug", false);
        mDebugModePreference.setChecked(debug);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.getKey().equals("debug")) {
            boolean debugPrefValue = Boolean.parseBoolean(String.valueOf(newValue));
            mPrefManager.put("debug", debugPrefValue);
        }

        return true;
    }
}
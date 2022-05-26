package com.mobwal.pro.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.utilits.PrefUtil;

public class PinSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;

    private SwitchPreferenceCompat mPinPreference;
    private EditTextPreference mPinCodePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pin_pref, rootKey);

        mSharedPreferences = requireContext().getSharedPreferences(Names.PREFERENCE_NAME, Context.MODE_PRIVATE);

        mPinPreference = findPreference("pin");
        Objects.requireNonNull(mPinPreference).setOnPreferenceChangeListener(this);

        mPinCodePreference = findPreference("pin_code");
        Objects.requireNonNull(mPinCodePreference).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WalkerApplication.Log("Настройки. Безопасность.");
        setHasOptionsMenu(true);

        updatePinCodeSummary();

        boolean pin = mSharedPreferences.getBoolean("pin", false);
        mPinPreference.setChecked(pin);

        mPinCodePreference.setText(mSharedPreferences.getString("pin_code", ""));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if(preference.getKey().equals("pin")) {
            boolean pinPrefValue = Boolean.parseBoolean(String.valueOf(newValue));
            mSharedPreferences.edit().putBoolean("pin", pinPrefValue).apply();

            if(!pinPrefValue) {
                mSharedPreferences.edit().putString("pin_code", "").apply();
                updatePinCodeSummary();
            }
        }

        if(preference.getKey().equals("pin_code")) {
            String pinCodeValue = String.valueOf(newValue);
            mSharedPreferences.edit().putString("pin_code", pinCodeValue).apply();
            updatePinCodeSummary();
        }

        return true;
    }

    /**
     * обновление описания блока у кода
     */
    private void updatePinCodeSummary() {
        String text = mSharedPreferences.getString("pin_code", "");
        if(TextUtils.isEmpty(text)) {
            mPinCodePreference.setSummary(R.string.pin_code_empty);
        } else {
            mPinCodePreference.setSummary(text.replaceAll("\\.", "*"));
        }
    }
}
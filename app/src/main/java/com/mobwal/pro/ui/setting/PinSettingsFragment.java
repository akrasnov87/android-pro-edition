package com.mobwal.pro.ui.setting;

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

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.PrefManager;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;

public class PinSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {


    private SwitchPreferenceCompat mPinPreference;
    private EditTextPreference mPinCodePreference;

    private PrefManager mPrefManager;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pin_pref, rootKey);
        mPrefManager = new PrefManager(requireContext());

        mPinPreference = findPreference("pin");
        Objects.requireNonNull(mPinPreference).setOnPreferenceChangeListener(this);

        mPinCodePreference = findPreference("pin_code");
        Objects.requireNonNull(mPinCodePreference).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.getInstance().info("Настройки. Безопасность.");
        setHasOptionsMenu(true);

        updatePinCodeSummary();

        boolean pin = mPrefManager.get("pin", false);
        mPinPreference.setChecked(pin);

        mPinCodePreference.setText(mPrefManager.get("pin_code", ""));
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

            mPrefManager.put("pin", pinPrefValue);

            if(!pinPrefValue) {
                mPrefManager.put("pin_code", "");
                updatePinCodeSummary();
            }
        }

        if(preference.getKey().equals("pin_code")) {
            String pinCodeValue = String.valueOf(newValue);
            mPrefManager.put("pin_code", pinCodeValue);
            updatePinCodeSummary();
        }

        return true;
    }

    /**
     * обновление описания блока у кода
     */
    private void updatePinCodeSummary() {
        String text = mPrefManager.get("pin_code", "");
        if(TextUtils.isEmpty(text)) {
            mPinCodePreference.setSummary(R.string.pin_code_empty);
        } else {
            mPinCodePreference.setSummary(text.replaceAll("\\.", "*"));
        }
    }
}
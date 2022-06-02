package ru.mobnius.core.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import java.util.Objects;

import ru.mobnius.core.R;
import ru.mobnius.core.data.app.OnCoreApplicationListeners;
import com.mobwal.android.library.authorization.Authorization;
import com.mobwal.android.library.authorization.AuthorizationCache;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.CoreActivity;
import ru.mobnius.core.ui.BasePreferenceFragmentCompact;
import ru.mobnius.core.ui.fragment.PinCodeFragment;

public class SecurityActivity extends CoreActivity {

    private static void setPrefFragment(AppCompatActivity context) {
        context.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.single_fragment_container, new SecurityPrefFragment())
                .commit();
    }

    public static void setPinCodeFragment(AppCompatActivity context, String pin, String title) {
        BasicUser user = Authorization.getInstance().getLastAuthUser();
        PinCodeFragment fragment = PinCodeFragment.newInstance(pin, user.getCredentials().login);
        context.getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, fragment).addToBackStack(null).commit();
        Objects.requireNonNull(context.getSupportActionBar()).setSubtitle(title);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, SecurityActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_container);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Безопасность");
        getSupportActionBar().setSubtitle(R.string.settings);

        setPrefFragment(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.SECURITY_PREF;
    }

    public static class SecurityPrefFragment extends BasePreferenceFragmentCompact
            implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener  {

        private final String pinSummary = "Авторизация по пину: %s";
        private SwitchPreference spPin;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.security_pref);

            spPin = findPreference(PreferencesManager.PIN);
            Objects.requireNonNull(spPin).setOnPreferenceChangeListener(this);

            Preference pReset = findPreference(PreferencesManager.MBL_RESET);
            Objects.requireNonNull(pReset).setOnPreferenceClickListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();

            spPin.setSummary(String.format(pinSummary, PreferencesManager.getInstance().isPinAuth() ? "включена" : "отключена"));
            spPin.setChecked(PreferencesManager.getInstance().isPinAuth());

            BasicUser user = Authorization.getInstance().getLastAuthUser();
            spPin.setEnabled(user != null);

            if(!PreferencesManager.getInstance().isPin()) {
                spPin.setEnabled(false);
            }
        }

        @Override
        public int getExceptionCode() {
            return IExceptionCode.SECURITY_PREF;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (PreferencesManager.PIN.equals(preference.getKey())) {
                boolean pinValue = Boolean.parseBoolean(String.valueOf(newValue));
                spPin.setSummary(String.format(pinSummary, pinValue ? "включена" : "отключена"));

                if (pinValue) {
                    setPinCodeFragment((AppCompatActivity) requireActivity(), null, "Установка пин-кода");
                } else {
                    BasicUser user = Authorization.getInstance().getLastAuthUser();
                    //new AuthorizationCache(requireActivity()).update(user.getCredentials().login, "", new Date());
                    PreferencesManager.getInstance().setPinAuth(false);
                }
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (PreferencesManager.MBL_RESET.equals(preference.getKey())) {
                confirmResetDialog((dialog, which) -> ((OnCoreApplicationListeners) getActivity().getApplication()).onResetSetting());
            }
            return false;
        }

        protected void confirmResetDialog(DialogInterface.OnClickListener listener) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            dialog.setTitle("Сообщение");
            dialog.setMessage("После сброса потребуется повторная авторизация. Вы действительно хотите сбросить настройки?");
            dialog.setPositiveButton("Да", listener);
            dialog.setNegativeButton("Нет", null);
            dialog.create().show();
        }
    }
}

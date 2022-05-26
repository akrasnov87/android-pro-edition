package com.mobwal.pro.ui.security;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.pro.MainActivity;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentLoginBinding;
import com.mobwal.pro.ui.BaseFragment;

import java.util.List;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.Meta;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.authorization.AuthorizationMeta;
import ru.mobnius.core.data.configuration.ConfigurationSetting;
import ru.mobnius.core.data.configuration.ConfigurationSettingUtil;
import ru.mobnius.core.data.configuration.DefaultPreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.ui.BaseLoginActivity;
import ru.mobnius.core.utils.NetworkInfoUtil;
import ru.mobnius.core.utils.NewThread;

/**
 * Экран авторизации по логину и паролю
 */
public class LoginFragment extends BaseFragment {

    private Authorization mAuthorization;
    private BasicUser mBasicUser;

    private FragmentLoginBinding mBinding;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        WalkerApplication.Log("Безопасность. Лигин и пароль.");

        mAuthorization = Authorization.getInstance();
        mBasicUser = mAuthorization.getLastAuthUser();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mBinding.securityOk.setOnClickListener(v -> onAuthorizing());
        mBinding.securityName.setOnClickListener(this::onNameAnimation);
        mBinding.securityPass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //пароль введен и можно передавать данные на сервере
                onAuthorizing();
                return true;
            }
            return false;
        });

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showActionBar(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        showActionBar(true);
    }

    // Privates

    /**
     * Начать процесс авторизации
     */
    void onAuthorizing() {
        String login = mBinding.securityLogin.getText().toString();
        String password = mBinding.securityPass.getText().toString();

        if (!login.isEmpty() && !password.isEmpty()) {
            enabledForms(false);

            if (NetworkInfoUtil.isNetworkAvailable(requireContext())) {
                onSignOnline(login, password);
            } else {
                onSignOffline(login, password);
            }
        } else {
            toast(getString(R.string.auth_empty));
        }
    }

    /**
     * Активность формы
     * @param enable признак включения
     */
    void enabledForms(boolean enable) {
        mBinding.securityLogin.setEnabled(enable);
        mBinding.securityPass.setEnabled(enable);
        mBinding.securityOk.setVisibility(enable ? View.VISIBLE : View.GONE);
        mBinding.securityWait.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    /**
     * Авторизация успешно пройдена
     * @param mode режим авторизации Authorization.ONLINE | Authorization.OFFLINE
     */
    void onAuthorizationSuccess(int mode) {
        WalkerApplication.Debug("Авторизация выполнена в режиме: " + (mode == Authorization.ONLINE ? "ONLINE" : "OFFLINE"));

        requireActivity().finish();
        startActivity(MainActivity.getIntent(getContext()));
    }

    void onAuthorizationFailed(String message) {
        if (!message.isEmpty()) {
            toast(message);
        }

        enabledForms(true);
    }

    void toast(String message) {
        if(!isAdded()) {
            return;
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    void onSignOnline(final String login, final String password) {
        mAuthorization.onSignIn(requireActivity(), login, password, Authorization.ONLINE, meta -> {
            AuthorizationMeta authorizationMeta = (AuthorizationMeta) meta;

            switch (authorizationMeta.getStatus()) {
                case Meta.NOT_AUTHORIZATION:
                    onAuthorizationFailed(meta.getMessage());
                    break;
                case Meta.OK:
                    if (mAuthorization.isInspector()) {
                        toast(authorizationMeta.getMessage());
                        onAuthorizationSuccess(Authorization.ONLINE);
                    } else {
                        onAuthorizationFailed(getString(ru.mobnius.core.R.string.accessDenied));
                    }
                    break;

                default:
                    onAuthorizationFailed(getString(ru.mobnius.core.R.string.serverNotAvailable));
                    break;
            }
        });
    }

    void onSignOffline(String login, String password) {
        mBasicUser = mAuthorization.getAuthUser(login);
        if (mBasicUser == null) {
            onAuthorizationFailed(getString(ru.mobnius.core.R.string.offlineDenied));
            return;
        }
        mAuthorization.onSignIn((BaseLoginActivity) requireActivity(), login, password, Authorization.OFFLINE, meta -> {

            AuthorizationMeta authorizationMeta = (AuthorizationMeta) meta;
            switch (authorizationMeta.getStatus()) {
                case Meta.NOT_AUTHORIZATION:
                    onAuthorizationFailed(meta.getMessage());
                    break;
                case Meta.OK:
                    if (mAuthorization.isInspector()) {
                        toast(authorizationMeta.getMessage());
                        onAuthorizationSuccess(Authorization.OFFLINE);
                    } else {
                        onAuthorizationFailed(getString(ru.mobnius.core.R.string.accessDenied));
                    }
                    break;

                default:
                    onAuthorizationFailed(getString(ru.mobnius.core.R.string.serverNotAvailable));
                    break;
            }
        });
    }

    /**
     * Анимация на названии
     * @param v представление
     */
    void onNameAnimation(View v) {
        if(v.getId() == R.id.security_name) {
            ObjectAnimator colorAnim = ObjectAnimator.ofInt(mBinding.securityNameBefore, "textColor",
                    Color.WHITE, Color.BLACK);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(1000);
            colorAnim.start();

            colorAnim = ObjectAnimator.ofInt(mBinding.securityNameAfter, "textColor",
                    Color.WHITE, Color.BLACK);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(1000);
            colorAnim.start();

            colorAnim = ObjectAnimator.ofInt(mBinding.securityNameTag, "textColor",
                    Color.WHITE, Color.parseColor("#ff9e9e9e"));
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(2000);
            colorAnim.start();
        }
    }
}
package com.mobwal.pro.ui.security;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
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

import com.mobwal.android.library.authorization.AuthorizationListeners;
import com.mobwal.android.library.util.NetworkInfoUtil;
import com.mobwal.pro.MainActivity;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentLoginBinding;
import com.mobwal.pro.ui.BaseFragment;

import com.mobwal.android.library.data.Meta;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.authorization.AuthorizationMeta;

import com.mobwal.android.library.authorization.credential.BasicUser;

/**
 * Экран авторизации по логину и паролю
 */
public class LoginFragment extends BaseFragment
    implements AuthorizationListeners {

    private BasicAuthorizationSingleton mAuthorization;
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

        mAuthorization = BasicAuthorizationSingleton.getInstance();
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
        WalkerApplication.Debug("Авторизация выполнена в режиме: " + (mode == AuthorizationListeners.ONLINE ? "ONLINE" : "OFFLINE"));

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
        //mAuthorization.onSignIn(requireActivity(), login, password, AuthorizationListeners.ONLINE, this);
    }

    void onSignOffline(String login, String password) {
        /*mBasicUser = mAuthorization.getOfflineAuthUser(login);
        if (mBasicUser == null) {
            onAuthorizationFailed(getString(R.string.offlineDenied));
            return;
        }
        mAuthorization.onSignIn((BaseLoginActivity) requireActivity(), login, password, AuthorizationListeners.OFFLINE, meta -> {

            AuthorizationMeta authorizationMeta = (AuthorizationMeta) meta;
            switch (authorizationMeta.getStatus()) {
                case Meta.NOT_AUTHORIZATION:
                    onAuthorizationFailed(meta.getMessage());
                    break;
                case Meta.OK:
                    if (mAuthorization.isUser()) {
                        toast(authorizationMeta.getMessage());
                        onAuthorizationSuccess(BasicAuthorizationSingleton.OFFLINE);
                    } else {
                        onAuthorizationFailed(getString(ru.mobnius.core.R.string.accessDenied));
                    }
                    break;

                default:
                    onAuthorizationFailed(getString(ru.mobnius.core.R.string.serverNotAvailable));
                    break;
            }
        });*/
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

    @Override
    public AuthorizationMeta authorization(@NonNull Context context, @NonNull String login, @NonNull String password) {
        return null;
    }

    @Override
    public AuthorizationMeta convertResponseToMeta(@NonNull String response) {
        return null;
    }

    @Override
    public void onResponseAuthorizationResult(AuthorizationMeta meta) {
        /*switch (meta.getStatus()) {
            case Meta.NOT_AUTHORIZATION:
                onAuthorizationFailed(meta.getMessage());
                break;
            case Meta.OK:
                if (mAuthorization.isUser()) {
                    toast(meta.getMessage());
                    onAuthorizationSuccess(AuthorizationListeners.ONLINE);
                } else {
                    onAuthorizationFailed(getString(R.string.accessDenied));
                }
                break;

            default:
                onAuthorizationFailed(getString(R.string.serverNotAvailable));
                break;
        }*/
    }

    @Override
    public String getLastAuthUserName() {
        return null;
    }
}
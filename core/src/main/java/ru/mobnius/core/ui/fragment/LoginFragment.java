package ru.mobnius.core.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.task.ConfigurationAsyncTask;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.Meta;
import ru.mobnius.core.data.Version;
import ru.mobnius.core.data.app.Application;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.authorization.AuthorizationMeta;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.data.network.OnNetworkChangeListeners;
import ru.mobnius.core.data.network.ServerExistsAsyncTask;
import ru.mobnius.core.data.socket.SocketManager;
import ru.mobnius.core.ui.BaseFragment;
import ru.mobnius.core.ui.BaseLoginActivity;
import ru.mobnius.core.ui.OnLoginListeners;
import ru.mobnius.core.utils.AuthUtil;
import ru.mobnius.core.utils.NetworkInfoUtil;
import ru.mobnius.core.utils.VersionUtil;

public class LoginFragment extends BaseFragment
        implements TextWatcher, OnNetworkChangeListeners {

    private Authorization mAuthorization;

    private TextView tvNetwork;
    private TextView tvServer;
    private EditText etLogin;
    private ImageButton ibLoginClear;
    private ImageButton ibPasswordClear;
    private ImageButton ibShowPassword;
    private EditText etPassword;
    private Button btnSignIn;
    private BasicUser mBasicUser;
    private ProgressBar mProgressBar;

    private ConfigurationAsyncTask mConfigurationAsyncTask;
    private ServerExistsAsyncTask mServerExistsAsyncTask;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        loginFragment.setArguments(args);
        return loginFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuthorization = Authorization.getInstance();
        mBasicUser = mAuthorization.getLastAuthUser();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isSocketConnect = false;
        SocketManager socketManager = SocketManager.getInstance();
        if (socketManager != null)
            isSocketConnect = socketManager.isConnected();

        mServerExistsAsyncTask = new ServerExistsAsyncTask(this, NetworkInfoUtil.isNetworkAvailable(requireContext()), isSocketConnect);
        mServerExistsAsyncTask.execute();

        if (mAuthorization.isAutoSignIn()) {
            String login = mBasicUser.getCredentials().login;
            PreferencesManager.createInstance(getContext(), login);
            if (PreferencesManager.getInstance().isDebug()) {
                singIn(mBasicUser.getCredentials().login, mBasicUser.getCredentials().password);
            } else {
                etLogin.setText(mBasicUser.getCredentials().login);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        etLogin = v.findViewById(R.id.auth_login);
        etPassword = v.findViewById(R.id.auth_password);
        tvNetwork = v.findViewById(R.id.auth_no_internet);
        tvServer = v.findViewById(R.id.auth_no_server);

        ibLoginClear = v.findViewById(R.id.auth_login_clear);
        ibLoginClear.setOnClickListener(v1 -> etLogin.setText(""));

        ibPasswordClear = v.findViewById(R.id.auth_password_clear);
        ibPasswordClear.setOnClickListener(v12 -> etPassword.setText(""));

        ibShowPassword = v.findViewById(R.id.auth_password_show);
        ibShowPassword.setOnClickListener(v13 -> {
            if (etPassword.getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                ibShowPassword.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visibility_outlined_24dp, requireContext().getTheme()));
            } else {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                ibShowPassword.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_visibility_off_outlined_24dp, requireContext().getTheme()));
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnSignIn = v.findViewById(R.id.auth_sign_in);
        btnSignIn.setOnClickListener(v14 -> singIn(etLogin.getText().toString().trim(), etPassword.getText().toString().trim()));

        TextView tvVersion = v.findViewById(R.id.auth_version);
        tvVersion.setOnClickListener(v15 -> onVersionClick());
        tvVersion.setText(getString(R.string.versionShort, getVersion()));

        mProgressBar = v.findViewById(R.id.auth_progress);

        etLogin.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etLogin.setOnFocusChangeListener((v16, hasFocus) -> {
            String str = ((EditText) v16).getText().toString();
            changeVisibility(ibLoginClear, hasFocus, str);
        });

        etPassword.setOnFocusChangeListener((v17, hasFocus) -> {
            String str = ((EditText) v17).getText().toString();
            changeVisibility(ibPasswordClear, hasFocus, str);
            changeVisibility(ibShowPassword, hasFocus, str);
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Application) requireActivity().getApplication()).addNetworkChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Application) requireActivity().getApplication()).removeNetworkChangeListener(this);
    }

    private void onVersionClick() {
        String versionName = VersionUtil.getVersionName(requireContext());
        String status = "неизвестен";
        switch (new Version().getVersionParts(versionName)[2]) {
            case 0:
                status = getString(R.string.alphaText);
                break;
            case 1:
                status = getString(R.string.betaText);
                break;
            case 2:
                status = getString(R.string.releaseCandidateText);
                break;
            case 3:
                status = getString(R.string.productionText);
                break;
        }
        toast(AuthUtil.getVersionToast(getString(R.string.versionToast), versionName, status));
    }

    /**
     * Получение версии приложения
     *
     * @return версия
     */
    private String getVersion() {
        String versionName = VersionUtil.getVersionName(requireContext());
        if (new Version().getVersionParts(versionName)[2] == Version.PRODUCTION) {
            return VersionUtil.getShortVersionName(getContext());
        } else {
            return VersionUtil.getVersionName(requireContext());
        }
    }

    private void toast(String message) {
        if(!isAdded()){
            return;
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void onAuthorized() {
        mConfigurationAsyncTask = new ConfigurationAsyncTask(GlobalSettings.getConnectUrl(), configRefreshed -> {
            if (isAdded()) {
                ((Application) requireActivity().getApplication()).onAuthorized(BaseLoginActivity.LOGIN);

                ((OnLoginListeners) requireActivity()).onAuthorized();
            }
            /*Intent intent = new Intent(getContext(), RouteListActivity.class);
            startActivity(intent);
            requireActivity().finish();*/
        });
        mConfigurationAsyncTask.execute();
    }

    private void failAuthorized(String message) {
        if (!message.isEmpty()) {
            toast(message);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void onSignOnline(final String login, final String password) {
        mAuthorization.onSignIn((BaseLoginActivity) requireActivity(), login, password, Authorization.ONLINE, meta -> {
            AuthorizationMeta authorizationMeta = (AuthorizationMeta) meta;

            switch (authorizationMeta.getStatus()) {
                case Meta.NOT_AUTHORIZATION:
                    failAuthorized(meta.getMessage());
                    break;
                case Meta.OK:
                    if (mAuthorization.isInspector()) {
                        toast(authorizationMeta.getMessage());
                        onAuthorized();
                    } else {
                        failAuthorized(getString(R.string.accessDenied));
                    }
                    break;

                default:
                    failAuthorized(getString(R.string.serverNotAvailable));
                    break;
            }
        });
    }

    private void onSignOffline(String login, String password) {
        mBasicUser = mAuthorization.getAuthUser(login);
        if (mBasicUser == null) {
            failAuthorized(getString(R.string.offlineDenied));
            return;
        }
        mAuthorization.onSignIn((BaseLoginActivity) requireActivity(), login, password, Authorization.OFFLINE, meta -> {

            AuthorizationMeta authorizationMeta = (AuthorizationMeta) meta;
            switch (authorizationMeta.getStatus()) {
                case Meta.NOT_AUTHORIZATION:
                    failAuthorized(meta.getMessage());
                    break;
                case Meta.OK:
                    if (mAuthorization.isInspector()) {
                        toast(authorizationMeta.getMessage());
                        onAuthorized();
                    } else {
                        failAuthorized(getString(R.string.accessDenied));
                    }
                    break;

                default:
                    failAuthorized(getString(R.string.serverNotAvailable));
                    break;
            }
        });
    }

    private void singIn(String login, String password) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (NetworkInfoUtil.isNetworkAvailable(requireContext())) {
            onSignOnline(login, password);
        } else {
            onSignOffline(login, password);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        if (etLogin.isFocused()) {
            String msg = AuthUtil.minLength(login);
            etLogin.setError(msg.isEmpty() ? null : msg);
            ibLoginClear.setVisibility(login.isEmpty() ? View.GONE : View.VISIBLE);
        }

        if (etPassword.isFocused()) {
            String msg = AuthUtil.minLength(password);
            etPassword.setError(msg.isEmpty() ? null : msg);
            ibPasswordClear.setVisibility(password.isEmpty() ? View.GONE : View.VISIBLE);
            ibShowPassword.setVisibility(password.isEmpty() ? View.GONE : View.VISIBLE);
        }

        btnSignIn.setEnabled(AuthUtil.isButtonEnable(login, password));
    }

    private void changeVisibility(ImageButton img, boolean visible, String empty) {
        if (visible && !empty.equals("")) {
            img.setVisibility(View.VISIBLE);
        }
        if (!visible) {
            img.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkChange(boolean isOnline, boolean isSocketConnect, boolean isServerExists) {
        tvNetwork.setVisibility(isOnline ? View.GONE : View.VISIBLE);
        tvServer.setVisibility(isServerExists ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.LOGIN;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConfigurationAsyncTask != null) {
            mConfigurationAsyncTask.cancel(true);
            mConfigurationAsyncTask = null;
        }
        if (mServerExistsAsyncTask != null) {
            mServerExistsAsyncTask.cancel(true);
            mServerExistsAsyncTask = null;
        }
    }
}

package ru.mobnius.core.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import ru.mobnius.core.NamesCore;
import ru.mobnius.core.R;
import ru.mobnius.core.adapter.task.ConfigurationAsyncTask;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.app.Application;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.authorization.AuthorizationCache;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.CoreActivity;
import ru.mobnius.core.ui.BaseFragment;
import ru.mobnius.core.ui.BaseLoginActivity;
import ru.mobnius.core.ui.OnLoginListeners;
import ru.mobnius.core.ui.component.PinCodeLinLay;
import ru.mobnius.core.utils.NetworkInfoUtil;

import static ru.mobnius.core.ui.component.PinCodeLinLay.PIN_CODE_LENGTH;

public class PinCodeFragment extends BaseFragment
        implements View.OnClickListener, PinCodeLinLay.PinChangeListener, PinCodeLinLay.CheckPin, PinCodeLinLay.FocusChange {

    private PinCodeLinLay pclPinPoints;
    private ProgressBar mProgressBar;
    private String pinDigits;
    private String tempPin;

    private int confirmPin;

    private List<Button> mButtonList;
    private ImageButton ibClear;
    private LinearLayout llDigit;
    private TextView tvForgotPin;

    private AuthorizationCache cache;

    private ConfigurationAsyncTask mConfigurationAsyncTask;

    public static PinCodeFragment newInstance(String pin, String login) {
        PinCodeFragment pinCodeFragment;
        if (pin != null) {
            pinCodeFragment = new PinCodeFragment();
            Bundle args = new Bundle();
            args.putString(NamesCore.PIN, pin);
            args.putString(NamesCore.LOGIN, login);
            pinCodeFragment.setArguments(args);
        } else {
            pinCodeFragment = new PinCodeFragment();
            Bundle args = new Bundle();
            args.putString(NamesCore.LOGIN, login);
            pinCodeFragment.setArguments(args);
        }
        return pinCodeFragment;
    }

    private boolean hasPin() {
        return requireArguments().containsKey(NamesCore.PIN);
    }

    private String getPin() {
        return requireArguments().getString(NamesCore.PIN);
    }

    private String getLogin() {
        return requireArguments().getString(NamesCore.LOGIN);
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.PIN_CODE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasPin()) {
            confirmPin = 0;
        }

        cache = new AuthorizationCache(getContext());
        pinDigits = "";
        mButtonList = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pin_code, container, false);
        pclPinPoints = v.findViewById(R.id.pinFragment_pclTop);
        pclPinPoints.setPinChangeListener(this);
        pclPinPoints.setCheckPinListener(this);
        pclPinPoints.setFocusChangeListener(this);
        llDigit = v.findViewById(R.id.pinFragment_llDigits);

        tvForgotPin = v.findViewById(R.id.pin_forgot);

        ibClear = v.findViewById(R.id.pin_clear);
        ImageButton ibFingerPrint = v.findViewById(R.id.pinFragment_ibFingerPrint);

        Button btnOne = v.findViewById(R.id.pinFragment_btnOne);
        Button btnTwo = v.findViewById(R.id.pinFragment_btnTwo);
        Button btnThree = v.findViewById(R.id.pinFragment_btnThree);
        Button btnFour = v.findViewById(R.id.pinFragment_btnFour);
        Button btnFive = v.findViewById(R.id.pinFragment_btnFive);
        Button btnSix = v.findViewById(R.id.pinFragment_btnSix);
        Button btnSeven = v.findViewById(R.id.pinFragment_btnSeven);
        Button btnEight = v.findViewById(R.id.pinFragment_btnEight);
        Button btnNine = v.findViewById(R.id.pinFragment_btnNine);
        Button btnZero = v.findViewById(R.id.pinFragment_btnZero);

        mButtonList.add(btnOne);
        mButtonList.add(btnTwo);
        mButtonList.add(btnThree);
        mButtonList.add(btnFour);
        mButtonList.add(btnFive);
        mButtonList.add(btnSix);
        mButtonList.add(btnSeven);
        mButtonList.add(btnEight);
        mButtonList.add(btnNine);
        mButtonList.add(btnZero);
        mButtonList.add(btnOne);
        ibClear.setOnClickListener(this);

        if (!hasPin()) {
            tvForgotPin.setVisibility(View.GONE);
        }
        tvForgotPin.setOnClickListener(this);
        for (Button button : mButtonList) {
            button.setOnClickListener(this);
        }

        if (Authorization.getInstance().isAutoSignIn() && hasPin() && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            fingerPrintActivate(ibFingerPrint);
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pin_forgot) {
            new AlertDialog.Builder(getContext()).setTitle("Сброс пин-кода")
                    .setMessage("Сбросить пин-код и авторизоаться через логин и пароль?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreferencesManager.createInstance(requireContext(), getLogin());
                            PreferencesManager.getInstance().setPinAuth(false);
                            //cache.update(getLogin(), "", new Date());
                            Authorization.getInstance().reset();

                            ((OnLoginListeners)requireActivity()).onAuthorize();
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .create().show();

        } else {
            if (v.getId() == R.id.pin_clear) {
                pclPinPoints.onPinClear();
            } else {
                Button btn = (Button) v;
                pclPinPoints.onPinEnter();
                if (pinDigits.length() < PIN_CODE_LENGTH) {
                    pinDigits = pinDigits + btn.getText().toString();
                }
                pclPinPoints.setPinnedPoints();
            }
        }
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onClear() {
        pclPinPoints.setUnPinnedPoint();
        if (!pinDigits.isEmpty()) {
            pinDigits = pinDigits.substring(0, pinDigits.length() - 1);
        }
    }

    @Override
    public void onPinComplete() {
        for (Button button : mButtonList) {
            button.setClickable(false);
        }
        ibClear.setClickable(false);

        if (hasPin()) {
            if (getPin().equals(pinDigits)) {
                onAuthorize();
                return;
            } else {
                Toast.makeText(getContext(), "Неправильный пин", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (confirmPin == 0) {
                confirmPin++;
                tempPin = pinDigits;
                Toast.makeText(getContext(), "Подтвердите пин-код", Toast.LENGTH_SHORT).show();
            } else {
                if (pinDigits.equals(tempPin)) {
                    PreferencesManager.getInstance().setPinAuth(true);

                    //cache.update(getLogin(), pinDigits, new Date());
                    Toast.makeText(getContext(), "Вход по пин-коду активирован", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Пин-коды не совпадают, порпобуйте еще раз", Toast.LENGTH_SHORT).show();
                    confirmPin = 0;
                }
            }
        }
        pinDigits = "";
    }

    @Override
    public void onRunnableComplete() {
        for (Button button : mButtonList) {
            button.setClickable(true);
            ibClear.setClickable(true);
        }
    }

    private void onAuthorize() {
        BasicUser user = cache.read(getLogin());
        Authorization.getInstance().setUser(user);
        //cache.update(user.getCredentials().login, getPin(), new Date());

        if(NetworkInfoUtil.isNetworkAvailable(requireContext())) {
            ((CoreActivity)requireActivity()).startProgress();
            llDigit.setVisibility(View.GONE);
            tvForgotPin.setVisibility(View.GONE);
            mConfigurationAsyncTask = new ConfigurationAsyncTask(GlobalSettings.getConnectUrl(), new ConfigurationAsyncTask.OnConfigurationLoadedListener() {
                @Override
                public void onConfigurationLoaded(boolean configRefreshed) {
                    ((Application)requireActivity().getApplication()).onAuthorized(BaseLoginActivity.PIN);
                    ((OnLoginListeners)requireActivity()).onAuthorized();
                }
            });
            mConfigurationAsyncTask.execute();
        } else {
            ((Application)requireActivity().getApplication()).onAuthorized(BaseLoginActivity.PIN);
            ((OnLoginListeners)requireActivity()).onAuthorized();
        }
    }

    private void fingerPrintActivate(ImageButton imageButton) {
        BiometricManager manager = BiometricManager.from(requireActivity());
        if (manager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            imageButton.setVisibility(View.VISIBLE);
            Executor executor = ContextCompat.getMainExecutor(requireContext());
            final BiometricPrompt biometricPrompt = new BiometricPrompt(requireActivity(),
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);

                    if(errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        requireActivity().finishAffinity();
                        requireActivity().finish();
                        requireActivity().onBackPressed();
                    }
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    onAuthorize();
                    Toast.makeText(getContext(), "Вы авторизовались", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getContext(), "Несовпадение отпечатка",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Авторизация по отпечатку пальца")
                    .setSubtitle("Приложите палец к сенсору")
                    .setNegativeButtonText("Отмена")
                    .build();


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mConfigurationAsyncTask != null) {
            mConfigurationAsyncTask.cancel(true);
            mConfigurationAsyncTask = null;
        }
    }
}

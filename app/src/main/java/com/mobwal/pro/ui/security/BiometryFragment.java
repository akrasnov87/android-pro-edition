package com.mobwal.pro.ui.security;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.util.LogUtilSingleton;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.databinding.FragmentBiometryBinding;
import com.mobwal.pro.ui.BaseFragment;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;

/**
 * Авторизация по ПИН-коду
 */
public class BiometryFragment extends BaseFragment
        implements View.OnClickListener, TextWatcher {

    private FragmentBiometryBinding mBinding;
    private String pinCode;
    private PrefManager mPrefManager;

    public BiometryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LogUtilSingleton.getInstance().writeText("Безопасность. Пин-код.");
        mPrefManager = new PrefManager(requireContext());
        pinCode = mPrefManager.get("pin_code", "");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentBiometryBinding.inflate(inflater, container, false);
        mBinding.securityPass.addTextChangedListener(this);

        mBinding.securityOk.setOnClickListener(this);
        mBinding.securityReset.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.reset_pin);

            builder.setCancelable(false);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                LogUtilSingleton.getInstance().debug("Пользователь принудительно сбрасывает ПИН-код.");

                mPrefManager.put("pin", false);
                mPrefManager.put("pin_code", "");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_security);
                navController.navigate(R.id.nav_login);
            });
            builder.setNegativeButton(R.string.no, null);

            AlertDialog alert = builder.create();
            alert.show();
        });

        mBinding.securityName.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showActionBar(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBinding.securityPass.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mBinding.securityPass.setError(null);

        int passLength = pinCode.length();
        if(passLength > 0 && mBinding.securityPass.getText().toString().length() > passLength) {
            mBinding.securityPass.setText("");
            mBinding.securityPass.setError(getString(R.string.pin_code_fail));
            return;
        }

        if(mBinding.securityPass.getText().toString().equals(pinCode)) {
            // авторизация произведена
            onAuthorized();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.security_ok) {
            if (mBinding.securityPass.getText().toString().equals(pinCode)) {
                // авторизация произведена
                onAuthorized();
            } else {
                mBinding.securityPass.setText("");
                mBinding.securityPass.setError(getString(R.string.pin_code_fail));
            }
        }

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

    private void onAuthorized() {
        LogUtilSingleton.getInstance().debug("Авторизация по ПИН-коду выполнена");

        mBinding.securityPass.setError(null);

        // авторизация не требуется
        BasicAuthorizationSingleton authorization = BasicAuthorizationSingleton.getInstance();
        authorization.setUser(authorization.getLastAuthUser());

        WalkerApplication.authorized(requireActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        showActionBar(true);
    }
}
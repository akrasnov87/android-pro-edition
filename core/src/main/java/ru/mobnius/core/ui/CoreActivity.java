package ru.mobnius.core.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ru.mobnius.core.R;
import ru.mobnius.core.ui.component.ErrorFieldView;
import ru.mobnius.core.ui.fragment.HelpDialogFragment;
import ru.mobnius.core.utils.ThemeUtil;

/**
 * Базовое activity для приложения
 */
public abstract class CoreActivity
        extends ExceptionInterceptActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private final boolean mIsBackToExist;
    private ProgressBar mProgressBar;
    private int mWaitProgress = 0;
    private ErrorFieldView mErrorFieldView;

    private boolean isBackPressed;

    public CoreActivity() {
        super();
        mIsBackToExist = false;
    }

    public CoreActivity(boolean isBackToExist) {
        super();
        mIsBackToExist = isBackToExist;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mErrorFieldView = getErrorMessage();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.changeColor(this);
        // предназначено для привязки перехвата ошибок
        onExceptionIntercept();
        View view = findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(this);
        mProgressBar.setVisibility(View.GONE);

        if(view instanceof ViewGroup) {
            ((ViewGroup)view).addView(mProgressBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public void startProgress() {
        mWaitProgress++;
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        mWaitProgress--;
        if(mWaitProgress <= 0) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    protected ErrorFieldView getErrorMessage() {
        return findViewById(R.id.error);
    }

    protected void setErrorMessage(String message) {
        if (mErrorFieldView != null) {
            mErrorFieldView.setMessage(message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        isBackPressed = false;
        if (isBackToExist()) {

            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                finish();

                super.onBackPressed();
                isBackPressed = true;
                return;
            }

            doubleBackToExitPressedOnce = true;

            Toast.makeText(this, getString(R.string.sign_out_message), Toast.LENGTH_LONG).show();

            int TOAST_DURATION = 2750;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, TOAST_DURATION);
        } else {

            super.onBackPressed();
            isBackPressed = true;
        }
    }

    private boolean isBackToExist() {
        return mIsBackToExist;
    }

    protected void alert(String message) {
        alert(message, "OK", null);
    }

    protected void alert(String message, String buttonText, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonText, listener).show();
    }

    public void showHelp(String title, String text, String date) {
        HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
        helpDialogFragment.bind(title, text, date);
        helpDialogFragment.show(getSupportFragmentManager(), "help");
    }

    public boolean isBackPressed() {
        return isBackPressed;
    }

    public void setBackPressed(boolean backPressed) {
        isBackPressed = backPressed;
    }
}
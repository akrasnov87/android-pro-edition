package ru.mobnius.core.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ru.mobnius.core.R;
import com.mobwal.android.library.authorization.Authorization;
import com.mobwal.android.library.authorization.AuthorizationCache;

import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.fragment.LoginFragment;
import ru.mobnius.core.ui.setting.SecurityActivity;

public abstract class BaseLoginActivity extends CoreActivity
    implements OnLoginListeners {

    public final static int LOGIN = 0;
    public final static int PIN = 1;

    private static void setLoginFragment(AppCompatActivity context) {
        context.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.single_fragment_container, LoginFragment.newInstance())
                .commit();
        Objects.requireNonNull(context.getSupportActionBar()).setSubtitle(null);
    }

    public BaseLoginActivity() {
        super(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BasicUser basicUser = Authorization.getInstance().getLastAuthUser();
        String pin = "";
        if(basicUser != null) {
            AuthorizationCache cache = new AuthorizationCache(this);
            //pin = cache.readPin(basicUser.getCredentials().login);
        }
        if (!pin.isEmpty()) {
            SecurityActivity.setPinCodeFragment(this, pin, "Авторизация по пин-кода");
        } else {
            setLoginFragment(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_container);
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.LOGIN;
    }
}

package com.mobwal.pro;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.exception.ExceptionInterceptActivity;
import com.mobwal.android.library.util.LogUtil;
import com.mobwal.pro.databinding.ActivityMailBinding;

public class MailActivity
        extends ExceptionInterceptActivity {

    public static String EXCEPTION = "EXCEPTION";
    public static String SELF = "SELF";

    /**
     *
     * @param context контекст
     * @param mode MailActivity.EXCEPTION или MailActivity.SELF
     * @return Intent
     */
    public static Intent getIntent(@NonNull Context context, @NonNull String mode) {
        Intent intent = new Intent(context, MailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("mode", mode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMailBinding binding = ActivityMailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMail.toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_mail_send).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_mail);
        if(navHostFragment != null) {
            NavInflater inflater = navHostFragment.getNavController().getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.mail_navigation);

            navHostFragment.getNavController().setGraph(graph);

            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(BasicAuthorizationSingleton.getInstance().isAuthorized()) {
            LogUtil.debug(this, "Переход на главый экран");
            startActivity(MainActivity.getIntent(this));
        } else {
            LogUtil.debug(this, "Переход на экран авторизации");
            startActivity(SecurityActivity.getIntent(this));
        }
    }

    @Override
    public int getExceptionCode() {
        return Codes.MAIL_ACTIVITY;
    }
}
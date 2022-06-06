package com.mobwal.pro;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mobwal.android.library.exception.ExceptionInterceptActivity;
import com.mobwal.android.library.exception.FaceExceptionSingleton;
import com.mobwal.pro.databinding.ActivitySecurityBinding;

public class SecurityActivity
        extends ExceptionInterceptActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SecurityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySecurityBinding binding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarSecurity.toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_login).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_security);
        if (navHostFragment != null) {

            NavInflater inflater = navHostFragment.getNavController().getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.security_navigation);

            SharedPreferences sharedPreferences = getSharedPreferences(Names.PREFERENCE_NAME, MODE_PRIVATE);
            String pinCode = sharedPreferences.getString("pin_code", "");

            if (!pinCode.isEmpty()) {
                WalkerApplication.Debug("Вывод экрана авторизации по ПИН-коду.");

                graph.setStartDestination(R.id.nav_biometry);
                navHostFragment.getNavController().setGraph(graph);
            }

            navHostFragment.getNavController().setGraph(graph);

            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }

        if(FaceExceptionSingleton.getInstance(this).getCount() > 0) {
            //startActivity(MailActivity.getIntent(this, MailActivity.EXCEPTION));
        }
    }

    @Override
    public int getExceptionCode() {
        return Names.SECURITY_ACTIVITY;
    }
}
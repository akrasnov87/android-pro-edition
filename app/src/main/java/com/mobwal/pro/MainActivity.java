package com.mobwal.pro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.exception.ExceptionInterceptActivity;
import com.mobwal.pro.databinding.ActivityMainBinding;
import com.mobwal.pro.utilits.ActivityUtil;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;

public class MainActivity extends ExceptionInterceptActivity
     implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private PrefManager mPrefManager;
    private DrawerLayout mDrawerLayout;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogManager.getInstance().info("Главный экран.");

        if(!BasicAuthorizationSingleton.getInstance().isAuthorized()) {
            Toast.makeText(this, R.string.without_auth, Toast.LENGTH_SHORT).show();

            startActivity(SecurityActivity.getIntent(this));
            finish();
            return;
        }

        mPrefManager = new PrefManager(this);

        com.mobwal.pro.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        mDrawerLayout = binding.drawerLayout;

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_route)
                .setOpenableLayout(mDrawerLayout)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if(navHostFragment != null) {
            navController = navHostFragment.getNavController();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isWelcome = mPrefManager.get("welcome", false);

        if(!isWelcome) {
            LogManager.getInstance().info("Приветствие!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.error_reporting_full);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // ДА
                mPrefManager.put("error_reporting", true);
            });
            builder.setNegativeButton(R.string.no, null);

            AlertDialog alert = builder.create();
            alert.show();

            mPrefManager.put("welcome", true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean handled = false;
        if(item.getItemId() == R.id.nav_home_page) {
            ActivityUtil.openWebPage(this, Names.HOME_PAGE);
        } else if(item.getItemId() == R.id.nav_synchronization) {
            ActivityUtil.openSynchronization(this);
        } else if(item.getItemId() == R.id.nav_exit) {
            android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
            adb.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                WalkerApplication.exitToApp(this);
                finish();
            });
            adb.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
            android.app.AlertDialog alert = adb.create();
            alert.setTitle(getResources().getString(R.string.confirm_exit));
            alert.show();
        } else {
            handled = NavigationUI.onNavDestinationSelected(item, navController);
        }

        mDrawerLayout.closeDrawers();

        return handled;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public int getExceptionCode() {
        return Names.MAIN_ACTIVITY;
    }
}
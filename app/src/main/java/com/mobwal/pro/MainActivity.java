package com.mobwal.pro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mobwal.pro.databinding.ActivityMainBinding;
import com.mobwal.pro.utilits.ActivityUtil;
import com.mobwal.pro.utilits.PrefUtil;

import java.util.List;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.configuration.ConfigurationSetting;
import ru.mobnius.core.data.configuration.ConfigurationSettingUtil;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.utils.NewThread;

public class MainActivity extends AppCompatActivity
     implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_OPEN = 777;

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private SharedPreferences mSharedPreferences;
    private String pinCode;
    private DrawerLayout mDrawerLayout;
    private NewThread mConfigThread;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WalkerApplication.Debug("Главный экран.");

        if(!Authorization.getInstance().isAuthorized()) {
            Toast.makeText(this, R.string.without_auth, Toast.LENGTH_SHORT).show();

            startActivity(SecurityActivity.getIntent(this));
            finish();
            return;
        }

        //pinCode = PrefUtil.getPinCode(this);

        mSharedPreferences = getSharedPreferences(Names.PREFERENCE_NAME, Context.MODE_PRIVATE);

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
            NavInflater inflater = navHostFragment.getNavController().getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.mobile_navigation);

            /*if(isNeedAuthorized()) {
                WalkerApplication.Debug("Вывод экрана безопасности.");

                graph.setStartDestination(R.id.nav_biometry);
                navHostFragment.getNavController().setGraph(graph);
            }*/

            navController = navHostFragment.getNavController();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            navigationView.setNavigationItemSelectedListener(this);
        }

        mConfigThread = new NewThread(this) {
            @Override
            public void onBackgroundExecute() {
                if(Authorization.getInstance().isAuthorized()) {
                    BasicCredentials credentials = Authorization.getInstance().getUser().getCredentials();

                    try {
                        List<ConfigurationSetting> configurationSettings = ConfigurationSettingUtil.getSettings(GlobalSettings.getConnectUrl(), credentials);
                        if (configurationSettings != null) {
                            SharedPreferences sharedPreferences = getSharedPreferences(Names.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            for(ConfigurationSetting configurationSetting : configurationSettings) {
                                try {
                                    if (configurationSetting.type.equals(ConfigurationSetting.INTEGER)) {
                                        editor.putInt(configurationSetting.key, Integer.parseInt(configurationSetting.value));
                                    } else if (configurationSetting.type.equals(ConfigurationSetting.BOOLEAN)) {
                                        editor.putBoolean(configurationSetting.key, Boolean.parseBoolean(configurationSetting.value));
                                    } else {
                                        editor.putString(configurationSetting.key, configurationSetting.value);
                                    }
                                }catch (Exception e) {
                                    Logger.error("Ошибка применения настроек", e);
                                }
                            }
                            editor.apply();
                        }
                    } catch (Exception ignore) {
                        Logger.error("Ошибка чтения настроек", ignore);
                    }
                }
            }

            @Override
            public void onPostExecute() {

            }
        };

        mConfigThread.run();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isWelcome = mSharedPreferences.getBoolean("welcome", false);
        boolean isDemo = mSharedPreferences.getBoolean("demo", false);

        if(!isWelcome && isDemo) {
            WalkerApplication.Log("Приветствие!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.error_reporting_full);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                // ДА
                mSharedPreferences.edit().putBoolean("error_reporting", true).apply();
            });
            builder.setNegativeButton(R.string.no, null);

            AlertDialog alert = builder.create();
            alert.show();

            mSharedPreferences.edit().putBoolean("welcome", true).apply();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Требуется ли авторизация
     * @return true - требуется авторизация
     */
    private boolean isNeedAuthorized() {
        if(TextUtils.isEmpty(pinCode)) {
            return false;
        } else {
            return !WalkerApplication.getAuthorized(this);
        }
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
                WalkerApplication.ExitToApp(this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                startActivity(ImportActivity.getIntent(this, data.getData()));
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();

        if (mConfigThread != null) {
            mConfigThread.destroy();
            mConfigThread = null;
        }
    }
}
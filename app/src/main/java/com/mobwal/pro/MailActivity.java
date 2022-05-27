package com.mobwal.pro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mobwal.pro.databinding.ActivityMailBinding;
import com.mobwal.pro.databinding.ActivitySecurityBinding;

public class MailActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
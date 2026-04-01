package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch themeToggle = findViewById(R.id.switchTheme);
        SharedPreferences appData = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        // Load previous state
        themeToggle.setChecked(appData.getBoolean("isDark", false));

        // Listen for user clicking the switch
        themeToggle.setOnCheckedChangeListener((view, isChecked) -> {
            SharedPreferences.Editor dataEditor = appData.edit();
            dataEditor.putBoolean("isDark", isChecked);
            dataEditor.apply();

            // Change the actual colors
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}
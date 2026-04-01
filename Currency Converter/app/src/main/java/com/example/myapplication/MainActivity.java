package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText inputAmountBox;
    private Spinner dropDown1, dropDown2;
    private TextView outputText;
    private Button actionButton;
    private HashMap<String, Double> conversionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Checking the saved theme preference on startup
        SharedPreferences myPrefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean darkActive = myPrefs.getBoolean("isDark", false);
        if (darkActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Linking variables to the XML layout
        inputAmountBox = findViewById(R.id.etAmount);
        dropDown1 = findViewById(R.id.spinnerFrom);
        dropDown2 = findViewById(R.id.spinnerTo);
        outputText = findViewById(R.id.tvResult);
        actionButton = findViewById(R.id.btnConvert);

        loadCurrencyData();

        actionButton.setOnClickListener(v -> doTheMath());
    }

    private void loadCurrencyData() {
        String[] options = {"USD", "INR", "EUR", "JPY"};

        conversionMap = new HashMap<>();
        conversionMap.put("USD", 1.0);
        conversionMap.put("INR", 93.08);
        conversionMap.put("EUR", 0.86);
        conversionMap.put("JPY", 158.53);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        dropDown1.setAdapter(myAdapter);
        dropDown2.setAdapter(myAdapter);
    }

    private void doTheMath() {
        String typedValue = inputAmountBox.getText().toString();

        // Prevent crashing if the box is empty
        if (typedValue.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double startValue = Double.parseDouble(typedValue);
        String currency1 = dropDown1.getSelectedItem().toString();
        String currency2 = dropDown2.getSelectedItem().toString();

        double val1 = conversionMap.get(currency1);
        double val2 = conversionMap.get(currency2);

        // Base math: convert to USD first, then to the target
        double usdBase = startValue / val1;
        double calculatedResult = usdBase * val2;

        outputText.setText(String.format("Result: %.2f %s", calculatedResult, currency2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
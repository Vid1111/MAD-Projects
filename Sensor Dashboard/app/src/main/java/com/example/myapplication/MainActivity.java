package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mySensorManager;
    private Sensor accelSensor, lightSensor, proxSensor;
    private TextView accelText, lightText, proxText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the XML views
        accelText = findViewById(R.id.tvAccel);
        lightText = findViewById(R.id.tvLight);
        proxText = findViewById(R.id.tvProx);

        // Initialize the hardware manager
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (mySensorManager != null) {
            accelSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            proxSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        // Error handling just if the phone is missing a sensor
        if (accelSensor == null) accelText.setText("Accelerometer not found.");
        if (lightSensor == null) lightText.setText("Light sensor not found.");
        if (proxSensor == null) proxText.setText("Proximity sensor not found.");
    }

    // Turn sensors on when the app is open
    @Override
    protected void onResume() {
        super.onResume();
        if (accelSensor != null) mySensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (lightSensor != null) mySensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (proxSensor != null) mySensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Turn sensors off when the app is closed
    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(this);
    }

    // Fires automatically every time a sensor detects a change
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelText.setText(String.format("X Axis: %.2f\nY Axis: %.2f\nZ Axis: %.2f", x, y, z));

        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float luxValue = event.values[0];
            lightText.setText("Brightness: " + luxValue + " lx");

        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            proxText.setText("Object Distance: " + distance + " cm");
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
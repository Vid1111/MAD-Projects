package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private GridView photoGrid;
    private Uri newImageUri; // Path where the camera will save the photo

    // 1. The camera result receiver
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Photo saved to Gallery!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // 2. The permission request receiver
    private final ActivityResultLauncher<String> requestCamPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoGrid = findViewById(R.id.myPhotoGrid);
        Button btnPickFolder = findViewById(R.id.btnPickFolder);
        Button btnOpenCam = findViewById(R.id.btnOpenCam);

        // Take Photo Logic
        btnOpenCam.setOnClickListener(v -> {
            // Check if we already have permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                // Ask the user for permission
                requestCamPermission.launch(Manifest.permission.CAMERA);
            }
        });
    }

    // Helper method to safely open the camera and where to save the file
    private void launchCamera() {
        // Tell Android to prepare a secure file path in the device's main media storage
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New App Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Taken from My Gallery App");

        newImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Launch the camera and pass the file path to it
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newImageUri);
        cameraLauncher.launch(cameraIntent);
    }
}
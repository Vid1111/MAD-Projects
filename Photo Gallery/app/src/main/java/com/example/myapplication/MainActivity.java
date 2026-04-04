package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Variables
    private GridView photoGrid;
    private Uri newImageUri;

    private ArrayList<Uri> imageList = new ArrayList<>();
    private ImageAdapter gridAdapter;

    // Camera result receiver
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

    // Permission request receiver
    private final ActivityResultLauncher<String> requestCamPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) launchCamera();
                else Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
            }
    );

    // Folder picker receiver
    private final ActivityResultLauncher<Intent> folderPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri treeUri = result.getData().getData();
                    loadImagesFromFolder(treeUri);
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

        // Connect the adapter to the grid
        gridAdapter = new ImageAdapter(this, imageList);
        photoGrid.setAdapter(gridAdapter);

        // Make Grid Images Clickable
        photoGrid.setOnItemClickListener((parent, view, position, id) -> {
            Uri clickedUri = imageList.get(position);
            Intent detailsIntent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            detailsIntent.putExtra("clicked_image_uri", clickedUri.toString());
            startActivity(detailsIntent);
        });

        // Take Photo Button
        btnOpenCam.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                requestCamPermission.launch(Manifest.permission.CAMERA);
            }
        });

        // Pick Folder Button
        btnPickFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            folderPickerLauncher.launch(intent);
        });
    }

    private void launchCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New App Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Taken from My Gallery App");
        newImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newImageUri);
        cameraLauncher.launch(cameraIntent);
    }

    // Helper method to read the selected folder and find all images
    private void loadImagesFromFolder(Uri treeUri) {
        imageList.clear();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

        if (pickedDir != null) {
            for (DocumentFile file : pickedDir.listFiles()) {
                // If the file is an image, add it to our list
                if (file.isFile() && file.getType() != null && file.getType().startsWith("image/")) {
                    imageList.add(file.getUri());
                }
            }
        }

        // Grid refresh itself with the new images
        gridAdapter.notifyDataSetChanged();

        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images found in this folder", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom Adapter to put Images into the GridView
    private class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Uri> imageUris;

        public ImageAdapter(Context context, ArrayList<Uri> imageUris) {
            this.context = context;
            this.imageUris = imageUris;
        }

        @Override
        public int getCount() { return imageUris.size(); }

        @Override
        public Object getItem(int position) { return imageUris.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // New ImageView for each item referenced by the Adapter
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            // Set the image from the Uri
            imageView.setImageURI(imageUris.get(position));
            return imageView;
        }
    }
}
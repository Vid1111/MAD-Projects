package com.example.myapplication;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {

    private DocumentFile currentFile; // Store the file globally so the delete button can access it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imageView = findViewById(R.id.detailsImageView);
        TextView tvName = findViewById(R.id.tvImageName);
        TextView tvPath = findViewById(R.id.tvImagePath);
        TextView tvSize = findViewById(R.id.tvImageSize);
        TextView tvDate = findViewById(R.id.tvImageDate);
        Button btnDelete = findViewById(R.id.btnDeleteImage);

        String passedUriString = getIntent().getStringExtra("clicked_image_uri");

        if (passedUriString != null) {
            Uri imageUri = Uri.parse(passedUriString);
            imageView.setImageURI(imageUri);

            currentFile = DocumentFile.fromSingleUri(this, imageUri);

            if (currentFile != null) {
                tvName.setText("Name: " + currentFile.getName());
                tvPath.setText("Path: " + imageUri.toString());

                long sizeInKB = currentFile.length() / 1024;
                tvSize.setText("Size: " + sizeInKB + " KB");

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                String dateString = sdf.format(new Date(currentFile.lastModified()));
                tvDate.setText("Date Taken: " + dateString);
            }
        }

        // Delete Logic
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to permanently delete this image?")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {
                        // If they click yes, try to delete the file
                        if (currentFile != null && currentFile.exists()) {
                            boolean isDeleted = currentFile.delete();

                            if (isDeleted) {
                                Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                finish(); // This instantly closes the details screen and returns them to the Gallery View!
                            } else {
                                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null) // Does nothing, just closes the popup
                    .show();
        });
    }
}

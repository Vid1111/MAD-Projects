package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imageView = findViewById(R.id.detailsImageView);
        TextView tvName = findViewById(R.id.tvImageName);
        TextView tvPath = findViewById(R.id.tvImagePath);
        TextView tvSize = findViewById(R.id.tvImageSize);
        TextView tvDate = findViewById(R.id.tvImageDate);

        // Catch the image data sent over from the MainActivity
        String passedUriString = getIntent().getStringExtra("clicked_image_uri");

        if (passedUriString != null) {
            Uri imageUri = Uri.parse(passedUriString);
            imageView.setImageURI(imageUri);

            // Use Android's DocumentFile library to easily extract the hidden metadata
            DocumentFile file = DocumentFile.fromSingleUri(this, imageUri);
            if (file != null) {
                tvName.setText("Name: " + file.getName());
                tvPath.setText("Path: " + imageUri.toString());

                // Math to convert bytes into readable Kilobytes (KB)
                long sizeInKB = file.length() / 1024;
                tvSize.setText("Size: " + sizeInKB + " KB");

                // Format the raw timestamp into a readable date
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                String dateString = sdf.format(new Date(file.lastModified()));
                tvDate.setText("Date Taken: " + dateString);
            }
        }
    }
}

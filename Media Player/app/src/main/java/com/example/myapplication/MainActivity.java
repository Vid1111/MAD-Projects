package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Variables for the audio player and the text label
    private MediaPlayer audioPlayer;
    private Uri audioFileUri;
    private TextView statusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the variables to our XML layout from Commit 1
        statusLabel = findViewById(R.id.tvStatus);
        Button btnOpenAudio = findViewById(R.id.btnOpenAudio);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnRestart = findViewById(R.id.btnRestart);

        // Open the Android file picker
        ActivityResultLauncher<Intent> audioPicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        audioFileUri = result.getData().getData();
                        setupAudio();
                    }
                }
        );

        // 1. Open File Button
        btnOpenAudio.setOnClickListener(v -> {
            Intent getAudio = new Intent(Intent.ACTION_GET_CONTENT);
            getAudio.setType("audio/*"); // Force it to only show audio files
            audioPicker.launch(getAudio);
        });

        // 2. Play Button
        btnPlay.setOnClickListener(v -> {
            if (audioPlayer != null && !audioPlayer.isPlaying()) {
                audioPlayer.start();
                statusLabel.setText("Playing audio...");
            } else if (audioPlayer == null) {
                Toast.makeText(this, "Pick an audio file first!", Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Pause Button
        btnPause.setOnClickListener(v -> {
            if (audioPlayer != null && audioPlayer.isPlaying()) {
                audioPlayer.pause();
                statusLabel.setText("Audio paused");
            }
        });

        // 4. Stop Button
        btnStop.setOnClickListener(v -> {
            if (audioPlayer != null) {
                audioPlayer.stop();
                statusLabel.setText("Audio stopped");
                setupAudio();
            }
        });

        // 5. Restart Button
        btnRestart.setOnClickListener(v -> {
            if (audioPlayer != null) {
                audioPlayer.seekTo(0);
                audioPlayer.start();
                statusLabel.setText("Audio restarted");
            }
        });
    }

    // Helper method to load the file into the player
    private void setupAudio() {
        if (audioFileUri == null) return;

        // If a song is already loaded, release it to save phone memory
        if (audioPlayer != null) {
            audioPlayer.release();
        }

        audioPlayer = MediaPlayer.create(this, audioFileUri);
        statusLabel.setText("Audio file loaded and ready");
    }

    // Free up memory when the user closes the app completely
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }
}
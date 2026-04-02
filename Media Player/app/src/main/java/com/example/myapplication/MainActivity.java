package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer audioPlayer;
    private Uri audioFileUri;
    private TextView statusLabel;
    private VideoView myVideoView;

    // Simple flag to tell the buttons what they should be controlling
    private boolean isVideoActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusLabel = findViewById(R.id.tvStatus);
        myVideoView = findViewById(R.id.videoView);

        Button btnOpenAudio = findViewById(R.id.btnOpenAudio);
        Button btnOpenVideo = findViewById(R.id.btnOpenVideo);
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnRestart = findViewById(R.id.btnRestart);

        // Audio File Picker
        ActivityResultLauncher<Intent> audioPicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        audioFileUri = result.getData().getData();
                        isVideoActive = false; // Switch back to audio mode
                        if(myVideoView.isPlaying()) myVideoView.pause();
                        setupAudio();
                    }
                }
        );

        // 1. Open Audio Button
        btnOpenAudio.setOnClickListener(v -> {
            Intent getAudio = new Intent(Intent.ACTION_GET_CONTENT);
            getAudio.setType("audio/*");
            audioPicker.launch(getAudio);
        });

        // 2. Open Video URL Button (Uses a basic popup dialog)
        btnOpenVideo.setOnClickListener(v -> {
            EditText urlInputBox = new EditText(this);
            urlInputBox.setHint("Paste MP4 link here");

            new AlertDialog.Builder(this)
                    .setTitle("Stream Video")
                    .setMessage("Enter direct video URL:")
                    .setView(urlInputBox)
                    .setPositiveButton("Load", (dialog, which) -> {
                        String link = urlInputBox.getText().toString();
                        if(!link.isEmpty()) {
                            loadVideoUrl(link);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 3. Play Button
        btnPlay.setOnClickListener(v -> {
            if (isVideoActive) {
                myVideoView.start();
                statusLabel.setText("Playing video...");
            } else {
                if (audioPlayer != null && !audioPlayer.isPlaying()) {
                    audioPlayer.start();
                    statusLabel.setText("Playing audio...");
                } else if (audioPlayer == null) {
                    Toast.makeText(this, "Load a file or URL first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 4. Pause Button
        btnPause.setOnClickListener(v -> {
            if (isVideoActive && myVideoView.isPlaying()) {
                myVideoView.pause();
                statusLabel.setText("Video paused");
            } else if (!isVideoActive && audioPlayer != null && audioPlayer.isPlaying()) {
                audioPlayer.pause();
                statusLabel.setText("Audio paused");
            }
        });

        // 5. Stop Button
        btnStop.setOnClickListener(v -> {
            if (isVideoActive) {
                myVideoView.stopPlayback();
                myVideoView.resume(); // Resets the video view to the start
                statusLabel.setText("Video stopped");
            } else {
                if (audioPlayer != null) {
                    audioPlayer.stop();
                    statusLabel.setText("Audio stopped");
                    setupAudio();
                }
            }
        });

        // 6. Restart Button
        btnRestart.setOnClickListener(v -> {
            if (isVideoActive) {
                myVideoView.seekTo(0);
                myVideoView.start();
                statusLabel.setText("Video restarted");
            } else {
                if (audioPlayer != null) {
                    audioPlayer.seekTo(0);
                    audioPlayer.start();
                    statusLabel.setText("Audio restarted");
                }
            }
        });
    }

    private void setupAudio() {
        if (audioFileUri == null) return;
        if (audioPlayer != null) {
            audioPlayer.release();
        }
        audioPlayer = MediaPlayer.create(this, audioFileUri);
        statusLabel.setText("Audio loaded and ready");
    }

    private void loadVideoUrl(String url) {
        isVideoActive = true;
        if(audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause(); // Stop audio if they switch to video
        }

        Uri videoUri = Uri.parse(url);
        myVideoView.setVideoURI(videoUri);
        statusLabel.setText("Video buffering...");

        // Let the user know when the video has finished loading from the internet
        myVideoView.setOnPreparedListener(mp -> statusLabel.setText("Video ready. Press Play!"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) audioPlayer.release();
    }
}
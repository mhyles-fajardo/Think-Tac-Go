package com.example.thinktacgo;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    private int categoryIndex = 0;
    private String[] categoryTitles = {"MATH-GALING!", "WORD FUN!", "HISTORY ADVENTURE!"};
    private int[] categoryImages = {R.drawable.math, R.drawable.word, R.drawable.histo};
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        TextView categoryTitle = findViewById(R.id.category_title);
        ImageView categoryImage = findViewById(R.id.category_image);

        categoryTitle.setText(categoryTitles[categoryIndex]);
        categoryImage.setImageResource(categoryImages[categoryIndex]);

        ImageView leftArrow = findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryIndex = (categoryIndex - 1 + categoryTitles.length) % categoryTitles.length;
                categoryTitle.setText(categoryTitles[categoryIndex]);
                categoryImage.setImageResource(categoryImages[categoryIndex]);
            }
        });

        ImageView rightArrow = findViewById(R.id.right_arrow);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryIndex = (categoryIndex + 1) % categoryTitles.length;
                categoryTitle.setText(categoryTitles[categoryIndex]);
                categoryImage.setImageResource(categoryImages[categoryIndex]);
            }
        });

        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, GameActivity.class);
                intent.putExtra("CATEGORY", categoryTitles[categoryIndex]);
                startActivity(intent);
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sugar_cookie);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Initialize the class-level audioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        ImageView mm = findViewById(R.id.mm);
        mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVolumePopup();
            }
        });

        ImageView qm = findViewById(R.id.qm);
        qm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup();
            }
        });
    }

    private void showVolumePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_volume, null);
        builder.setView(dialogView);

        SeekBar volumeSeekBar = dialogView.findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button okButton = dialogView.findViewById(R.id.okButton);
        AlertDialog alertDialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showInfoPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(dialogView);

        Button okButton = dialogView.findViewById(R.id.okButton);
        AlertDialog alertDialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
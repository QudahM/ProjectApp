package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Find buttons
        Button soundButton = findViewById(R.id.Sound);
        Button sightButton = findViewById(R.id.Sight);
        Button soundHardModeButton = findViewById(R.id.SoundHardMode);
        Button sightHardModeButton = findViewById(R.id.SightHardMode);

        // Set OnClickListener for normal modes
        soundButton.setOnClickListener(v -> navigateToDifficulty(1)); // 1 for audio game
        sightButton.setOnClickListener(v -> navigateToDifficulty(2)); // 2 for sight game

        // Set OnClickListener for Hard Modes
        soundHardModeButton.setOnClickListener(v -> navigateToHardMode(true)); // 1 for Sound Hard Mode
        sightHardModeButton.setOnClickListener(v -> navigateToHardMode(false)); // 2 for Sight Hard Mode
    }

    private void navigateToDifficulty(int gameMode) {
        Intent intent = new Intent(MainPage.this, DifficultySelectionActivity.class);
        intent.putExtra("game_mode", gameMode);
        startActivity(intent);
    }

    private void navigateToHardMode(boolean isSoundMode) {
        Intent intent = new Intent(MainPage.this, HardModeActivity.class);
        intent.putExtra("IS_SOUND_MODE", isSoundMode);  // Pass boolean for Sound/Sight mode
        startActivity(intent);
    }
}

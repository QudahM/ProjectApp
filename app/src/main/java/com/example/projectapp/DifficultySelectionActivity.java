package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
//import com.example.projectapp.AudioGameActivity;

public class DifficultySelectionActivity extends AppCompatActivity {

    private int gameMode; // 1 for Audio, 2 for Sight

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels_layout);

        // Retrieve the game mode passed from MainPage
        gameMode = getIntent().getIntExtra("game_mode", 1); // Default to audio if not provided

        // Initialize buttons
        Button level1 = findViewById(R.id.level1);
        Button level2 = findViewById(R.id.level2);
        Button level3 = findViewById(R.id.level3);

        // Set click listeners
        level1.setOnClickListener(v -> startGameWithDifficulty(1));
        level2.setOnClickListener(v -> startGameWithDifficulty(2));
        level3.setOnClickListener(v -> startGameWithDifficulty(3));
    }

    private void startGameWithDifficulty(int difficulty) {
        Intent intent;

        if (gameMode == 1) {
            // If gameMode is 1, start AudioGameActivity
            intent = new Intent(DifficultySelectionActivity.this, SoundTestActivity.class);
        } else {
            // Otherwise, start SightTestActivity
            intent = new Intent(DifficultySelectionActivity.this, SightTestActivity.class);
        }

        intent.putExtra("difficulty", difficulty); // Pass difficulty level
        startActivity(intent);
        finish();
    }
}

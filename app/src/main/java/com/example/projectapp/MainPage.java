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

        // Find the Play Buttons
        Button soundButton = findViewById(R.id.Sound);
        Button sightButton = findViewById(R.id.Sight);

        // Set OnClickListener for navigation to DifficultyActivity
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDifficulty(1); // 1 for audio game
            }
        });

        sightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDifficulty(2); // 2 for sight game
            }
        });
    }

    private void navigateToDifficulty(int gameMode) {
        Intent intent = new Intent(MainPage.this, DifficultySelectionActivity.class);
        intent.putExtra("game_mode", gameMode);
        startActivity(intent);
    }
}

package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.GameLogic.AudioGameActivity;

public class MainPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        // Find the Play Button
        Button soundButton = findViewById(R.id.Sound);
        Button sightButton = findViewById(R.id.Sight);

        // Set OnClickListener for navigation
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, AudioGameActivity.class);
                startActivity(intent);
            }
        });

        sightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, SightTestActivity.class);
                startActivity(intent);
            }
        });
    }
}

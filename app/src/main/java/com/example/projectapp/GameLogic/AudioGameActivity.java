package com.example.projectapp.GameLogic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;

import java.util.LinkedList;
import java.util.Queue;

public class AudioGameActivity extends AppCompatActivity {

    private Queue<String> soundQueue = new LinkedList<>();  // Queue for sound directions to be played
    private Queue<String> playerQueue = new LinkedList<>(); // Queue for directions the player should respond to
    private int missedDirections = 0;
    private final int MAX_MISSED = 6;
    private Handler handler = new Handler();
    private final String[] directions = {"TOP", "LEFT", "RIGHT", "BOTTOM"};
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_game);

        // Initialize Buttons
        Button btnTop = findViewById(R.id.btn_top);
        Button btnLeft = findViewById(R.id.btn_left);
        Button btnRight = findViewById(R.id.btn_right);
        Button btnBottom = findViewById(R.id.btn_bottom);

        // Start the game
        startGame();

        // Set OnClickListener for each button
        btnTop.setOnClickListener(v -> checkAnswer("TOP"));
        btnLeft.setOnClickListener(v -> checkAnswer("LEFT"));
        btnRight.setOnClickListener(v -> checkAnswer("RIGHT"));
        btnBottom.setOnClickListener(v -> checkAnswer("BOTTOM"));
    }

    private void startGame() {
        // Clear any previous queues
        soundQueue.clear();
        playerQueue.clear();

        // Shuffle and add 25 random directions to the soundQueue
        for (int i = 0; i < 25; i++) {
            // Randomly select a direction
            int randomIndex = (int) (Math.random() * directions.length);
            String randomDirection = directions[randomIndex];
            soundQueue.add(randomDirection);
        }

        // Start playing directions at 1-second intervals
        playNextDirection();
    }

    private void playNextDirection() {
        if (!soundQueue.isEmpty()) {
            // Pop the direction from the soundQueue and push it to the playerQueue
            String direction = soundQueue.poll();  // Pop from soundQueue
            playerQueue.add(direction);  // Add to playerQueue

            // Now, play the direction sound
            playDirectionSound(direction);

            // Simulate 1-second interval
            handler.postDelayed(this::playNextDirection, 1000);
        }
    }

    private void playDirectionSound(String direction) {
        int soundResId = getSoundResourceForDirection(direction);
        if (soundResId != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }
    }

    private int getSoundResourceForDirection(String direction) {
        switch (direction) {
            case "TOP":
                return R.raw.up;  // top_direction.mp3 placed in res/raw
            case "LEFT":
                return R.raw.left;  // left_direction.mp3 placed in res/raw
            case "RIGHT":
                return R.raw.right;  // right_direction.mp3 placed in res/raw
            case "BOTTOM":
                return R.raw.down;  // bottom_direction.mp3 placed in res/raw
            default:
                return 0;
        }
    }

    private void checkAnswer(String userAnswer) {
        if (!playerQueue.isEmpty()) {
            String expectedDirection = playerQueue.poll();  // Get and remove the direction from the player's queue
            if (userAnswer.equals(expectedDirection)) {
                correctAnswers++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                missedDirections = 0; // Reset missed directions count
            } else {
                missedDirections++;
                Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            }

            // Check if game is over due to missed directions
            if (missedDirections >= MAX_MISSED) {
                showFailureDialog();
            }
        }
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("You missed too many directions! Do you want to play again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .show();
    }

    private void restartGame() {
        missedDirections = 0;
        correctAnswers = 0;
        soundQueue.clear();
        playerQueue.clear();
        startGame();
    }
}

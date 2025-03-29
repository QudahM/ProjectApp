package com.example.projectapp.GameLogic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;

import java.util.LinkedList;
import java.util.Queue;

public class AudioGameActivity extends AppCompatActivity {

    private Queue<String> soundQueue = new LinkedList<>();  // Queue for sound directions
    private Queue<String> playerQueue = new LinkedList<>(); // Queue for expected player responses
    private int missedDirections = 0;
    private final int MAX_MISSED = 10;
    private Handler handler = new Handler();
    private final String[] directions = {"TOP", "LEFT", "RIGHT", "BOTTOM"};
    private int correctAnswers = 0;
    private long totalReactionTime = 0;
    private long startTime = 0;
    private long audioDelay = 1000; // Default 1 second delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_game);

        // Get difficulty level from intent
        int difficulty = getIntent().getIntExtra("difficulty", 1);
        setAudioDelay(difficulty);

        // Initialize buttons
        Button btnTop = findViewById(R.id.btn_top);
        Button btnLeft = findViewById(R.id.btn_left);
        Button btnRight = findViewById(R.id.btn_right);
        Button btnBottom = findViewById(R.id.btn_bottom);

        // Start the game
        startGame();

        // Set button click listeners
        btnTop.setOnClickListener(v -> checkAnswer("TOP"));
        btnLeft.setOnClickListener(v -> checkAnswer("LEFT"));
        btnRight.setOnClickListener(v -> checkAnswer("RIGHT"));
        btnBottom.setOnClickListener(v -> checkAnswer("BOTTOM"));
    }

    private void setAudioDelay(int difficulty) {
        switch (difficulty) {
            case 1:
                audioDelay = 3000; // 3 second
                break;
            case 2:
                audioDelay = 1500; // 1.5 seconds
                break;
            case 3:
                audioDelay = 750; // 0.75 seconds
                break;
            default:
                audioDelay = 1000; // Default to 1 second
        }
    }

    private void startGame() {
        soundQueue.clear();
        playerQueue.clear();

        // Add 10 random directions to the sound queue
        for (int i = 0; i < 10; i++) {
            int randomIndex = (int) (Math.random() * directions.length);
            soundQueue.add(directions[randomIndex]);
        }

        // Start playing directions
        playNextDirection();
    }

    private void playNextDirection() {
        if (!soundQueue.isEmpty()) {
            String direction = soundQueue.poll();  // Get next direction
            playerQueue.add(direction);  // Add to expected answers

            playDirectionSound(direction);

            // Start reaction time tracking
            startTime = System.currentTimeMillis();

            // Play next direction after the set delay
            handler.postDelayed(this::playNextDirection, audioDelay);
        } else {
            showGameResults(true);
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
                return R.raw.up;
            case "LEFT":
                return R.raw.left;
            case "RIGHT":
                return R.raw.right;
            case "BOTTOM":
                return R.raw.down;
            default:
                return 0;
        }
    }

    private void checkAnswer(String userAnswer) {
        if (!playerQueue.isEmpty()) {
            String expectedDirection = playerQueue.poll();
            long reactionTime = System.currentTimeMillis() - startTime;

            if (userAnswer.equals(expectedDirection)) {
                correctAnswers++;
                totalReactionTime += reactionTime;
                missedDirections = 0; // Reset missed count
            } else {
                missedDirections++;
            }

            if (missedDirections >= MAX_MISSED) {
                showGameResults(false);
            }
        }
    }

    private void showGameResults(boolean isSuccess) {
        long avgReactionTime = (correctAnswers > 0) ? totalReactionTime / correctAnswers : 0;
        String resultMessage = isSuccess
                ? "You finished with " + correctAnswers + "/10 correct answers!\nAverage Reaction Time: " + avgReactionTime + " ms"
                : "Game Over due to too many mistakes!";

        new AlertDialog.Builder(this)
                .setTitle(isSuccess ? "Game Over - Success" : "Game Over")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> showRestartDialog())
                .setCancelable(false)
                .show();
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Would you like to play again or go back to the home screen?")
                .setPositiveButton("Play Again", (dialog, which) -> restartGame())
                .setNegativeButton("Go to Home", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        missedDirections = 0;
        correctAnswers = 0;
        totalReactionTime = 0;
        soundQueue.clear();
        playerQueue.clear();
        startGame();
    }
}

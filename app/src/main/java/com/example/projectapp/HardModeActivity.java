package com.example.projectapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class HardModeActivity extends AppCompatActivity {

    private TextView scoreCounter, resultText;
    private ImageView directionImage;
    private Button btnUp, btnDown, btnLeft, btnRight;
    private int score = 0;
    private int cueCount = 0;
    private int counter;
    private String currentCorrectDirection;
    private boolean isSoundMode;
    private MediaPlayer mediaPlayer;
    private final String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
    private final int[] imageResources = {R.drawable.uparrow, R.drawable.downarrow, R.drawable.leftarrow, R.drawable.rightarrow};
    private final int[] soundResources = {R.raw.up, R.raw.down, R.raw.left, R.raw.right};
    private long totalReactionTime = 0;
    private int reactionCount = 0;
    private long gameStartTime;
    private long reactionStartTime;  // To store the start time for reaction calculation
    private long totalGameTime; // Store total game time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hard_mode_test);

        scoreCounter = findViewById(R.id.text_score_label);
        resultText = findViewById(R.id.resultText);
        directionImage = findViewById(R.id.directionImage);
        btnUp = findViewById(R.id.btn_top);
        btnDown = findViewById(R.id.btn_bottom);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);

        isSoundMode = getIntent().getBooleanExtra("IS_SOUND_MODE", true);

        // Show rules before the game starts
        showGameRulesDialog();

        // Set up the button listeners
        btnUp.setOnClickListener(view -> checkAnswer("UP"));
        btnDown.setOnClickListener(view -> checkAnswer("DOWN"));
        btnLeft.setOnClickListener(view -> checkAnswer("LEFT"));
        btnRight.setOnClickListener(view -> checkAnswer("RIGHT"));
    }

    private void showGameRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Rules")
                .setMessage(getGameRulesMessage())  // Call a method to get the appropriate message
                .setCancelable(false)
                .setPositiveButton("Start Game", (dialog, id) -> {
                    // Start the game after pressing OK
                    gameStartTime = SystemClock.elapsedRealtime();
                    setupGame();  // Start the first cue setup after the game starts
                });

        builder.create().show();
    }

    private String getGameRulesMessage() {
        if (isSoundMode) {
            return "In this game, you will receive sound cues. Press the corresponding button when you hear the sound.\n\n" +
                    "IGNORE the visual cues, and focus only on the sound cues. You will have 5 cues in total. Good luck!";
        } else {
            return "In this game, you will receive visual cues. Press the corresponding button when you see the arrow.\n\n" +
                    "IGNORE the sound cues, and focus only on the visual cues. You will have 5 cues in total. Good luck!";
        }
    }

    private void setupGame() {
        if (cueCount >= 5) {
            // Game ends after 5 cues
            showGameAnalyticsDialog();
            return;
        }

        int correctIndex = new Random().nextInt(4);
        currentCorrectDirection = directions[correctIndex];

        // Get a random delay between 0.3s, 0.5s, and 0.8s
        int delayTime = getRandomDelay();

        // Set a delay before showing the next cue
        new Handler().postDelayed(() -> {
            // Logic for either sound mode or visual mode
            if (isSoundMode) {
                // In Sound Mode, play the correct sound
                playSound(correctIndex);
                int incorrectIndex;
                do {
                    incorrectIndex = new Random().nextInt(4);
                } while (incorrectIndex == correctIndex);
                directionImage.setImageResource(imageResources[incorrectIndex]);
            } else {
                // In Sight Hard Mode, only show the correct visual cue, but play a distracting sound
                directionImage.setImageResource(imageResources[correctIndex]);
                playSound(new Random().nextInt(4));  // Play a random sound to confuse the user
                // Start tracking the reaction time immediately after the visual cue is shown
                reactionStartTime = SystemClock.elapsedRealtime();
            }

            cueCount++;  // Increase cue count after the cue is shown
        }, delayTime);  // Delay time before showing the cue
    }

    private void playSound(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResources[index]);
        mediaPlayer.start();

        // Start tracking the reaction time as soon as the sound starts
        reactionStartTime = SystemClock.elapsedRealtime();
    }

    private int getRandomDelay() {
        // Randomly choose a delay time from the specified values: 300ms, 500ms, or 800ms
        Random random = new Random();
        int[] delays = {300, 500, 800};
        return delays[random.nextInt(delays.length)];
    }

    private void checkAnswer(String userChoice) {
        long reactionTime = SystemClock.elapsedRealtime() - reactionStartTime; // Time taken for this answer

        if (userChoice.equals(currentCorrectDirection)) {

            counter++;
            directionImage.setImageResource(R.drawable.check);
            score++;  // Update score
            totalReactionTime += reactionTime;
            reactionCount++;
            scoreCounter.setText("Score: " + score+"/"+counter); // Update score counter
            resultText.setText("Correct! Reaction Time: " + reactionTime + " ms");
        } else {

            counter++;
            scoreCounter.setText("Score: " + score+"/"+counter);
            directionImage.setImageResource(R.drawable.cross);
            resultText.setText("Wrong! Reaction Time: " + reactionTime + " ms");
        }

        // After each answer, show the next cue with a delay
        setupGame();
    }

    private void showGameAnalyticsDialog() {
        // Store the total game time after the game ends
        totalGameTime = SystemClock.elapsedRealtime() - gameStartTime;

        // Calculate average reaction time
        long averageReactionTime = reactionCount > 0 ? totalReactionTime / reactionCount : 0;

            String resultMessage = true
                ? "You finished the game with " + score + "/"+ counter+" correct answers!\nAverage Reaction Time: " + averageReactionTime + " ms \nTotal time taken: "+ (totalGameTime / 1000) + " seconds"
                : "Game Over due to too many mistakes!";

        new AlertDialog.Builder(this)
               .setTitle(true ? "Game Over - Success" : "Game Over")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> showRestartDialog())
                .setCancelable(false)
                .show();

    }
    private void showRestartDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Would you like to play again or go choose a new Test or level?")
                .setPositiveButton("Play Again", (dialog, which) -> {
                    score = 0;
                    cueCount = 0;
                    totalReactionTime = 0;
                    reactionCount = 0;
                    totalGameTime = 0;
                    scoreCounter.setText(String.valueOf(score));
                    setupGame();
                })
                .setNegativeButton("Choose Another Test", (dialog, which) -> this.finish())
                .setCancelable(false)
                .show();
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

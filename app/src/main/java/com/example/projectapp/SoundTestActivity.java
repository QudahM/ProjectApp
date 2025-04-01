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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SoundTestActivity extends AppCompatActivity {
    private Button leftButton, rightButton, upButton, downButton;
    private TextView leveltext, score, resultText;
    private gamehelper helper;

    private int correctAnswers = 0, counter = 0, counterscore = 0, missedDirections = 0, level, questionsasnwered = 0;
    private long totalReactionTime = 0, delay;
    private long gameStartTime = 0, gameEndTime;

    private final String[] directions = {"UP", "LEFT", "RIGHT", "DOWN"};
    private Queue<String> soundQueue = new LinkedList<>();
    private Queue<String> playerQueue = new LinkedList<>();
    private Random random = new Random();
    private Handler handler;
    private String currentDirection = "";
    private long startTime;

    // New UI components for feedback
    private ImageView feedbackImage; // ImageView for feedback (checkmark/cross)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_test);

        // Initialize UI elements
        leftButton = findViewById(R.id.btn_left);
        rightButton = findViewById(R.id.btn_right);
        upButton = findViewById(R.id.btn_top);
        downButton = findViewById(R.id.btn_bottom);
        leveltext = findViewById(R.id.text_level);
        score = findViewById(R.id.text_score_label);
        resultText = findViewById(R.id.resultText);
        feedbackImage = findViewById(R.id.directionImage);  // Initialize the feedback ImageView

        handler = new Handler(getMainLooper());

        // Set initial values
        correctAnswers = 0;
        score.setText("Score: " + correctAnswers + "/" + counterscore);
        resultText.setText("Reaction Time: - ms");

        int difficulty = getIntent().getIntExtra("difficulty", 1);
        setdiff(difficulty);
        showGameRulesDialog();

        // Set button click listeners
        upButton.setOnClickListener(v -> checkAnswer("UP"));
        leftButton.setOnClickListener(v -> checkAnswer("LEFT"));
        rightButton.setOnClickListener(v -> checkAnswer("RIGHT"));
        downButton.setOnClickListener(v -> checkAnswer("DOWN"));

        helper = new gamehelper(this);
    }
    private void showGameRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Rules")
                .setMessage("In this game, you will receive sound cues. Press the corresponding button when you hear the sound.")
                .setCancelable(false)
                .setPositiveButton("Start Game", (dialog, id) -> {
                    startGame();
                });

        builder.create().show();
    }
    public void setdiff(int levels) {
        switch (levels) {
            case 1:
                level = 1;
                delay = 3000;
                break;
            case 2:
                level = 2;
                delay = 1500;
                break;
            case 3:
                level = 3;
                delay = 750;
                break;
            default:
                delay = 3000;
        }
        leveltext.setText("Level: " + level);
    }

    private void startGame() {
        soundQueue.clear();
        playerQueue.clear();

        // Generate random directions
        for (int i = 0; i < 15; i++) {
            int index = random.nextInt(directions.length);
            soundQueue.add(directions[index]);
        }

        handler.postDelayed(this::playNextDirection, delay);
    }

    private void playNextDirection() {
        if (!soundQueue.isEmpty()) {
            currentDirection = soundQueue.poll();
            playerQueue.add(currentDirection);
            startTime = SystemClock.elapsedRealtime(); // Start timing for this direction

            // Set game start time only when the first sound is played
            if (gameStartTime == 0) {
                gameStartTime = SystemClock.elapsedRealtime(); // Record the start time of the game
            }

            playDirectionSound(currentDirection);
        } else {
            // Game ends when all directions have been played
            gameEndTime = SystemClock.elapsedRealtime(); // Record the end time of the game
            helper.showGameResults(true, counter, questionsasnwered, totalReactionTime, getTotalGameTime(), this::startGame);
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
            case "UP":
                return R.raw.up;
            case "LEFT":
                return R.raw.left;
            case "RIGHT":
                return R.raw.right;
            case "DOWN":
                return R.raw.down;
            default:
                return 0;
        }
    }

    private void checkAnswer(String userAnswer) {
        if (!playerQueue.isEmpty()) {
            String expectedDirection = playerQueue.poll();
            long reactionTime = SystemClock.elapsedRealtime() - startTime; // Calculate reaction time after button press
            questionsasnwered++;

            // After a short delay, update the feedback image to either checkmark or cross
            handler.postDelayed(() -> {
                if (userAnswer.equals(expectedDirection)) {
                    updateFeedback("correct");
                    correctAnswers++;
                    counter++;
                    totalReactionTime += reactionTime;
                    counterscore++;
                    missedDirections = 0;
                } else {
                    updateFeedback("incorrect");
                    missedDirections++;
                    counterscore++;
                }

                score.setText("Score: " + correctAnswers + "/" + counterscore);

                // Format reaction time to 3 decimal places
                double seconds = reactionTime / 1000.0;
                String formattedTime = String.format("%.3f", seconds);
                resultText.setText("Reaction Time: " + formattedTime + " seconds");

                if (counterscore >= 5) {
                    level++;
                    counterscore = 0;
                    correctAnswers = 0;
                    leveltext.setText("Level: " + level);
                    score.setText("Score: " + correctAnswers + "/" + counterscore);
                    gameEndTime = SystemClock.elapsedRealtime(); // Record the end time of the game
                    helper.showGameResults(true, counter, questionsasnwered, totalReactionTime, getTotalGameTime(), this::startGame);
                } else {
                    handler.postDelayed(this::playNextDirection, delay);
                }
            }, 1000); // Delay for 1 second before showing correct/incorrect feedback
        }
    }

    // Method to update the feedback image (checkmark/cross)
    private void updateFeedback(String result) {
        if ("correct".equals(result)) {
            feedbackImage.setImageResource(R.drawable.check); // Show checkmark
        } else if ("incorrect".equals(result)) {
            feedbackImage.setImageResource(R.drawable.cross); // Show cross
        }

        feedbackImage.setVisibility(View.VISIBLE); // Make sure feedback is visible
    }

    private long getTotalGameTime() {
        // Ensure the game start time is always before the game end time
        if (gameEndTime >= gameStartTime) {
            return gameEndTime - gameStartTime;
        } else {
            return 0; // If the times are inverted, return 0
        }
    }
}

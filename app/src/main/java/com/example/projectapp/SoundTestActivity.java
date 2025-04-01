package com.example.projectapp;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class SoundTestActivity extends AppCompatActivity {
    private Button leftButton, rightButton, upButton, downButton;
    private TextView leveltext, score, resultText;
    private gamehelper helper;
    private ImageView feedbackImage;

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

    private SoundPool soundPool;
    private Map<String, Integer> soundMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_test);

        leftButton = findViewById(R.id.btn_left);
        rightButton = findViewById(R.id.btn_right);
        upButton = findViewById(R.id.btn_top);
        downButton = findViewById(R.id.btn_bottom);
        leveltext = findViewById(R.id.text_level);
        score = findViewById(R.id.text_score_label);
        resultText = findViewById(R.id.resultText);
        feedbackImage = findViewById(R.id.directionImage);

        handler = new Handler(getMainLooper());
        helper = new gamehelper(this);
        initializeSoundPool();

        score.setText("Score: " + correctAnswers + "/" + counterscore);
        resultText.setText("Reaction Time: - ms");

        int difficulty = getIntent().getIntExtra("difficulty", 1);
        setdiff(difficulty);
        showGameRulesDialog();

        upButton.setOnClickListener(v -> checkAnswer("UP"));
        leftButton.setOnClickListener(v -> checkAnswer("LEFT"));
        rightButton.setOnClickListener(v -> checkAnswer("RIGHT"));
        downButton.setOnClickListener(v -> checkAnswer("DOWN"));
    }

    private void initializeSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap.put("UP", soundPool.load(this, R.raw.up, 1));
        soundMap.put("LEFT", soundPool.load(this, R.raw.left, 1));
        soundMap.put("RIGHT", soundPool.load(this, R.raw.right, 1));
        soundMap.put("DOWN", soundPool.load(this, R.raw.down, 1));
    }

    private void showGameRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Rules")
                .setMessage("In this game, you will receive sound cues. Press the corresponding button when you hear the sound.")
                .setCancelable(false)
                .setPositiveButton("Start Game", (dialog, id) -> startGame());
        builder.create().show();
    }

    public void setdiff(int levels) {
        switch (levels) {
            case 1: delay = 3000; break;
            case 2: delay = 1500; break;
            case 3: delay = 750; break;
            default: delay = 3000;
        }
        leveltext.setText("Level: " + levels);
    }

    private void startGame() {
        soundQueue.clear();
        playerQueue.clear();

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
            startTime = SystemClock.elapsedRealtime();

            if (gameStartTime == 0) {
                gameStartTime = SystemClock.elapsedRealtime();
            }

            playDirectionSound(currentDirection);
        } else {
            gameEndTime = SystemClock.elapsedRealtime();
            helper.showGameResults(true, counter, questionsasnwered, totalReactionTime, getTotalGameTime(), this::startGame);
        }
    }

    private void playDirectionSound(String direction) {
        Integer soundId = soundMap.get(direction);
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    private void checkAnswer(String userAnswer) {
        if (!playerQueue.isEmpty()) {
            String expectedDirection = playerQueue.poll();
            long reactionTime = SystemClock.elapsedRealtime() - startTime;
            questionsasnwered++;

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
                resultText.setText("Reaction Time: " + reactionTime + " ms");

                if (counterscore >= 5) {
                    level++;
                    counterscore = 0;
                    correctAnswers = 0;
                    leveltext.setText("Level: " + level);
                    score.setText("Score: " + correctAnswers + "/" + counterscore);
                    gameEndTime = SystemClock.elapsedRealtime();
                    helper.showGameResults(true, counter, questionsasnwered, totalReactionTime, getTotalGameTime(), this::startGame);
                } else {
                    handler.postDelayed(this::playNextDirection, delay);
                }
            }, 1000);
        }
    }

    private void updateFeedback(String result) {
        feedbackImage.setImageResource(result.equals("correct") ? R.drawable.check : R.drawable.cross);
        feedbackImage.setVisibility(View.VISIBLE);
    }

    private long getTotalGameTime() {
        return Math.max(0, gameEndTime - gameStartTime);
    }
}
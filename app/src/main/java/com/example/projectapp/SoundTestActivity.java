package com.example.projectapp;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    private final String[] directions = {"UP", "LEFT", "RIGHT", "DOWN"};
    private Queue<String> soundQueue = new LinkedList<>();
    private Queue<String> playerQueue = new LinkedList<>();
    private Random random = new Random();
    private Handler handler;
    private String currentDirection = "";
    private long startTime,fulltime;

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
        handler = new Handler(getMainLooper());
        fulltime=System.currentTimeMillis();
        correctAnswers = 0;
        score.setText("Score: " + correctAnswers + "/" + counterscore);
        resultText.setText("Reaction Time: - ms");
        int difficulty = getIntent().getIntExtra("difficulty", 1);
        setdiff(difficulty);
        startGame();
        upButton.setOnClickListener(v -> checkAnswer("UP"));
        leftButton.setOnClickListener(v -> checkAnswer("LEFT"));
        rightButton.setOnClickListener(v -> checkAnswer("RIGHT"));
        downButton.setOnClickListener(v -> checkAnswer("DOWN"));
        helper = new gamehelper(this);
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
            startTime = System.currentTimeMillis();
            playDirectionSound(currentDirection);
        } else {

            helper.showGameResults(true, counter, questionsasnwered, totalReactionTime,fulltime, this::startGame);
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
            long reactionTime = System.currentTimeMillis() - startTime;
            questionsasnwered++;

            if (userAnswer.equals(expectedDirection)) {
                correctAnswers++;
                counter++;
                totalReactionTime += reactionTime;
                counterscore++;
                missedDirections = 0;
            } else {
                missedDirections++;
                counterscore++;
            }

            score.setText("Score: " + correctAnswers + "/" + counterscore);

            double seconds = reactionTime / 1000.0;
            String formattedTime = String.format("%.3f", seconds);
            resultText.setText("Reaction Time: " + formattedTime + " seconds");

            if (counterscore >= 5) {
                level++;
                counterscore = 0;
                correctAnswers = 0;
                leveltext.setText("Level: " + level);
                score.setText("Score: " + correctAnswers + "/" + counterscore);

                helper.showGameResults(true,counter,questionsasnwered,totalReactionTime,fulltime, this::startGame);
            } else {
                handler.postDelayed(this::playNextDirection, delay);
            }
        }
    }
}
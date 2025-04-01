package com.example.projectapp;
import android.content.Intent;
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

import com.example.projectapp.R;
import java.util.Random;


public class SightTestActivity extends AppCompatActivity {
    private Button leftButton, rightButton, upButton, downButton;
    private TextView leveltext,score,resultText;
    private ImageView directionImage;
    private long startTime,fulltime,starter;
    private gamehelper helper;
    private int correctAnswers,counter,counterscore,missedDirections,level,questionsasnwered;;
    private long totalReactionTime=0,delay;

    private final String[] directions = {"UP", "LEFT", "RIGHT", "DOWN"};
    private String currentDirection = "";
    private Random random = new Random();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight_test);
        leftButton = findViewById(R.id.btn_left);
        rightButton = findViewById(R.id.btn_right);
        upButton = findViewById(R.id.btn_top);
        downButton = findViewById(R.id.btn_bottom);
        leveltext= findViewById(R.id.text_level);
        directionImage=findViewById(R.id.directionImage);
        score=findViewById(R.id.text_score_label);
        resultText=findViewById(R.id.resultText);
        handler = new Handler(getMainLooper());

        leveltext.setText("Level: "+level);
        correctAnswers=0;
        score.setText("Score: "+correctAnswers+"/"+counterscore);
        int difficulty = getIntent().getIntExtra("difficulty", 1);
        setdiff(difficulty);
        directionImage.setImageResource(R.drawable.wait);

        showGameRulesDialog();
        upButton.setOnClickListener(v -> checkAnswer("UP"));
        leftButton.setOnClickListener(v -> checkAnswer("LEFT"));
        rightButton.setOnClickListener(v -> checkAnswer("RIGHT"));
        downButton.setOnClickListener(v -> checkAnswer("DOWN"));
        helper=new gamehelper(this);
    }
    private void showGameRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Rules")
                .setMessage("In this game, you will receive visual cues. Press the corresponding button when you see the arrow.")
                .setCancelable(false)
                .setPositiveButton("Start Game", (dialog, id) -> {
                    starter=SystemClock.elapsedRealtime();
                    startGame();
                });

        builder.create().show();
    }


    public void setdiff(int levels){
        switch (levels) {
            case 1:
                level=1;
                delay = 3000;
                break;
            case 2:
                level=2;
                delay = 1500;
                break;
            case 3:
                level=3;
                delay = 750;
                break;
            default:
                delay = 3000;
        }
        leveltext.setText("Level: " + level);
    }
    private void startGame(){
        directionImage.setImageResource(R.drawable.wait);
        handler.postDelayed(() -> {
            currentDirection = directions[random.nextInt(directions.length)];

            setdirection(currentDirection);
            directionImage.setVisibility(View.VISIBLE);
            startTime = SystemClock.elapsedRealtime();
        }, delay);
        directionImage.setImageResource(R.drawable.wait);
    }

    private void setdirection(String currentDirection) {
        switch (currentDirection) {
            case "LEFT":
                directionImage.setImageResource(R.drawable.leftarrow);
                break;
            case "RIGHT":
                directionImage.setImageResource(R.drawable.rightarrow);
                break;
            case "UP":
                directionImage.setImageResource(R.drawable.uparrow);
                break;
            case "DOWN":
                directionImage.setImageResource(R.drawable.downarrow);
                break;
        }
    }


    private void checkAnswer(String userAnswer){

        questionsasnwered++;
        if (userAnswer.equals(currentDirection)) {
            directionImage.setImageResource(R.drawable.check);
            correctAnswers++;
            long reactionTime = SystemClock.elapsedRealtime() - startTime;
            totalReactionTime += reactionTime;
            counterscore++;
            missedDirections = 0;
            score.setText("Score: "+correctAnswers+"/"+counterscore);

            counter++;
            resultText.setText("Reaction Time: " + reactionTime + " ms");
            if (counterscore >= 5) {
                counterscore=0;
                correctAnswers = 0;
                leveltext.setText("Level: "+level);
                score.setText("Score: "+correctAnswers+"/"+counterscore);
                fulltime = SystemClock.elapsedRealtime()-starter;
                helper.showGameResults(true,counter,questionsasnwered,totalReactionTime,fulltime, this::startGame);
            }else{
                handler.postDelayed(() -> startGame(), 1000);
            }

        } else {
            if (counterscore >= 5) {
                counterscore=0;
                correctAnswers = 0;
                leveltext.setText("Level: "+level);
                score.setText("Score: "+correctAnswers+"/"+counterscore);
                fulltime = SystemClock.elapsedRealtime()-starter;
                helper.showGameResults(true,counter,questionsasnwered,totalReactionTime,fulltime, this::startGame);
            }else{
                handler.postDelayed(() -> startGame(), 1000);
            }
            long reactionTime = SystemClock.elapsedRealtime()  - startTime;
            totalReactionTime += reactionTime;
            resultText.setText("Reaction Time: " + reactionTime + " ms");
            directionImage.setImageResource(R.drawable.cross);
            missedDirections++;
            counterscore++;
            score.setText("Score: "+correctAnswers+"/"+counterscore);
            handler.postDelayed(() -> startGame(), 1000);

        }
    }

}
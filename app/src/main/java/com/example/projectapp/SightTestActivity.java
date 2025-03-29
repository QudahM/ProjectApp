package com.example.projectapp;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectapp.R;
import java.util.Random;


public class SightTestActivity extends AppCompatActivity {
    private Button leftButton, rightButton, upButton, downButton;
    private TextView leveltext,score,resultText;
    private ImageView directionImage;
    private long startTime,endTime;
    private gamehelper helper;
    private int correctAnswers,counter;
    private long totalReactionTime;
    private int missedDirections,level;
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
        level=1;
        leveltext.setText("Level: "+level);
        correctAnswers=0;
        score.setText(""+correctAnswers);
        directionImage.setImageResource(R.drawable.wait);
        startGame();
        upButton.setOnClickListener(v -> checkAnswer("UP"));
        leftButton.setOnClickListener(v -> checkAnswer("LEFT"));
        rightButton.setOnClickListener(v -> checkAnswer("RIGHT"));
        downButton.setOnClickListener(v -> checkAnswer("DOWN"));
        helper=new gamehelper(this);
    }
    private void startGame(){
        directionImage.setImageResource(R.drawable.wait);
        long delay;
        if (level == 1) {
            delay = 3000;
        } else if (level == 2) {
            delay = 2000;
        } else {
            delay = 1000;
        }
        handler.postDelayed(() -> {
            currentDirection = directions[random.nextInt(directions.length)];
            startTime = System.currentTimeMillis();
            setdirection(currentDirection);
            directionImage.setVisibility(View.VISIBLE);
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


        if (userAnswer.equals(currentDirection)) {
            directionImage.setImageResource(R.drawable.check);
            correctAnswers++;
            counter++;
            long reactionTime = System.currentTimeMillis() - startTime;
            totalReactionTime += reactionTime;
            missedDirections = 0;
            score.setText("Score: "+correctAnswers);

            double seconds = reactionTime / 1000.0;
            String formattedTime = String.format("%.3f", seconds);

            resultText.setText("Reaction Time: " + formattedTime + " seconds");
            if (correctAnswers >= 5) {
                level++;
                correctAnswers = 0;

                leveltext.setText("Level: "+level);
                score.setText("Score: "+correctAnswers);
                handler.postDelayed(() -> startGame(), 1000);
            }
            if (level==4){
                helper.showGameResults(true,counter,totalReactionTime,this::startGame);
                level=1;
                correctAnswers = 0;
                leveltext.setText("Level: "+level);
                score.setText("Score: "+correctAnswers);

            }
            handler.postDelayed(() -> startGame(), 1000);
        } else {
            directionImage.setImageResource(R.drawable.cross);
            missedDirections++;
            handler.postDelayed(() -> startGame(), 1000);
        }
    }

}
package com.example.projectapp;

import android.app.AlertDialog;
import android.app.Activity;

public class gamehelper {
    private final Activity activity;

    public gamehelper(Activity activity) {
        this.activity = activity;
    }

    public void showGameResults(boolean isSuccess, int correctAnswers,int answered, long totalReactionTime,long totaltime, Runnable restartCallback) {

        long seconds = (totaltime / 1000) % 60;
        long averageReactionTime = (correctAnswers > 0) ? totalReactionTime / correctAnswers : 0;
        String resultMessage = isSuccess
                ? "You finished the game with " + correctAnswers + "/"+ answered+" correct answers!\nAverage Reaction Time: " + averageReactionTime + " ms \nTotal time taken: "+ seconds + " seconds"
                : "Game Over due to too many mistakes!";

        new AlertDialog.Builder(activity)
                .setTitle(isSuccess ? "Game Over - Success" : "Game Over")
                .setMessage(resultMessage)
                .setPositiveButton("OK", (dialog, which) -> showRestartDialog(restartCallback))
                .setCancelable(false)
                .show();
    }

    private void showRestartDialog(Runnable restartCallback) {
        new AlertDialog.Builder(activity)
                .setTitle("Game Over")
                .setMessage("Would you like to play again or go back to the home screen?")
                .setPositiveButton("Play Again", (dialog, which) -> restartCallback.run())
                .setNegativeButton("Go to Home", (dialog, which) -> activity.finish())
                .setCancelable(false)
                .show();
    }
}

package com.gamehub.Snake;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import com.gamehub.R;

import org.w3c.dom.Text;

public class SnakeGameOver extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_gameover);

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highscoreLabel);

        Bundle extras = getIntent().getExtras();
        int endScore = extras.getInt("score", 0);
        int highScore = extras.getInt("highScore", 0);
        scoreLabel.setText("Score: "+ endScore);
        highScoreLabel.setText("Highscore: "+ highScore);
    }

    public void newGame(View view){
        finish();
        startActivity(new Intent(getApplicationContext(),SnakeActivity.class));
    }

    public void exit(View view){
        finish();
    }
}

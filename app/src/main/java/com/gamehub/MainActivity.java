package com.gamehub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gamehub.Snake.SnakeActivity;
import com.gamehub.TicTacToe.TicTacToeActivity;
import com.gamehub.TwentyFortyEight.Twenty48Activity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTTTClick(View view) {
        startActivity(new Intent(this, TicTacToeActivity.class));
    }

    public void onSnakeClick(View view) {
        startActivity(new Intent(this, SnakeActivity.class));
    }

    public void on2048Click (View view) {
        startActivity(new Intent(this, Twenty48Activity.class));
    }
}

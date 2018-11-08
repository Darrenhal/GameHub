package com.gamehub.Snake;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.gamehub.R;

public class SnakeActivity extends Activity {

    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        snakeEngine = new SnakeEngine(this,this, size);
        setContentView(snakeEngine);


    }

     protected void onResume(){
        super.onResume();
        snakeEngine.resume();
    }

    protected void onPause(){
        super.onPause();
        snakeEngine.onPause();
    }
}

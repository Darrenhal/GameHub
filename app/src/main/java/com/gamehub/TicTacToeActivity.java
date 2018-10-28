package com.gamehub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TicTacToeActivity extends Activity {

    private TicTacToe ttt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        ttt = new TicTacToe(this);
        setContentView(ttt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttt.resume();
    }

   @Override
    protected void onPause() {
        super.onPause();
        ttt.pause();
    }

}

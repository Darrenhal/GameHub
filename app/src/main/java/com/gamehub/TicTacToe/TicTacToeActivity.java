package com.gamehub.TicTacToe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gamehub.R;

public class TicTacToeActivity extends Activity {

    private TicTacToe_Engine ttt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.ttt = new TicTacToe_Engine(this, this);
        setContentView(R.layout.activity_tic_tac_toe);

    }

    @Override
    public void onResume() {
        super.onResume();
        ttt.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ttt.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttt.onDestroy();
    }

    public void reset(View v) {
        ttt.resetGame();
    }

    public void buttonClick(View v) {
        ttt.buttonClick(v);
    }

}

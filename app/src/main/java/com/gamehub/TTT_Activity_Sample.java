package com.gamehub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TTT_Activity_Sample extends Activity {

    private Thread_Drawing_Sample ttt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        ttt = new Thread_Drawing_Sample(this);
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

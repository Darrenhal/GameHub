package com.gamehub.TwentyFortyEight;

import android.app.Activity;
import android.os.Bundle;

public class Twenty48Activity extends Activity {

    private Twenty48_Engine twenty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.twenty = new Twenty48_Engine(this);

        setContentView(this.twenty);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.twenty.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.twenty.pause();
    }
}

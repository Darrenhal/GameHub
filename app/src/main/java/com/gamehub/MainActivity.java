package com.gamehub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonTest(View view) {
        long millis = System.currentTimeMillis();
        TextView tView = findViewById(R.id.textView);
        tView.setText("Hello World: " + millis);
    }

    public void onTTTClick(View view) {

        this.view = view;
    }
}

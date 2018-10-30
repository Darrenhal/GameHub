package com.gamehub.TwentyFortyEight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Twenty48_Engine extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private long nextFrame;
    private int high_score;
    private int score;

    private Thread thread;
    private SurfaceHolder surfaceHolder;
    private CoordinationHolder coordHolder;

    public Twenty48_Engine(Context context) {
        super(context);

        this.score = 0;
        this.surfaceHolder = getHolder();
        this.coordHolder = new CoordinationHolder(this.getHeight(), this.getWidth());


        // ToDo High Score handling
        // ToDo for now it will be hardcoded
        // ToDO change to retrieve high score from database
        this.high_score = 0;

        this.nextFrame = System.currentTimeMillis() + 100;
    }

    private void update() {
        if (this.score > this.high_score) {
            this.high_score = this.score;
        }
    }

    private void draw() {
        if  (this.surfaceHolder.getSurface().isValid()) {
            Paint paint = new Paint();
            Canvas canvas = this.surfaceHolder.lockCanvas();
            if (canvas != null) {

                paint.setColor(Color.argb(255, 100, 100, 100));
                canvas.drawColor(paint.getColor());
            }
            this.surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (this.isPlaying) {
            if (this.nextFrame <= System.currentTimeMillis()) {
                this.update();
                this.draw();
                this.nextFrame = System.currentTimeMillis() + 1000;
            }
        }
    }

    public void pause() {
        this.isPlaying = false;
    }

    public void resume() {
        this.isPlaying = true;
        this.thread = new Thread(this);
        this.thread.start();
    }
}

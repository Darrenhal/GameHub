package com.gamehub.TwentyFortyEight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Twenty48_Engine extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private long nextFrame;
    private int high_score;
    private int score;

    private Thread thread;
    private Paint paint;
    private SurfaceHolder surfaceHolder;

    public Twenty48_Engine(Context context) {
        super(context);

        this.score = 0;
        this.surfaceHolder = getHolder();
        this.paint = new Paint();


        // ToDo High Score handling
        // ToDo for now it will be hardcoded
        // ToDO change to retrieve high score from database
        this.high_score = 0;

        this.nextFrame = System.currentTimeMillis() + 40;
    }

    private void update() {
        if (this.score > this.high_score) {
            this.high_score = this.score;
        }
    }

    private void draw() {
        if  (this.surfaceHolder.getSurface().isValid()) {
            Canvas canvas = this.surfaceHolder.lockCanvas();
            if (canvas != null) {
                drawBoard(canvas);
                drawScoreBoard(canvas);
            }
            this.surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private Canvas drawScoreBoard(Canvas c) {
        Canvas canvas = c;
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        int titleX = width / 8;
        int titleY = (int) (height / 4.5);

        int scoreX;
        int scoreY;


        paint.setColor(Color.BLACK);
        paint.setTextSize(128f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("0x800", titleX, titleY, paint);

        return canvas;
    }

    private Canvas drawBoard(Canvas c) {
        Canvas canvas = c;
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        int boardWidth = (int) (width * 0.8);
        int boardHeight = boardWidth;
        int borderDistance = (width - boardWidth) / 2;
        int tileLength = (int) (boardHeight / 4.5);
        int tileOffset = (boardHeight - (tileLength * 4)) / 5;

        paint.setColor(Color.argb(255, 100, 100, 100));
        // paints the board
        canvas.drawRect(borderDistance, (height - boardHeight - borderDistance), (width - borderDistance), (height - borderDistance), paint);
        // paints the tiles
        paint.setColor(Color.argb(255, 192, 192, 192));
        int boardX = (width - boardWidth - borderDistance);
        int boardY = (height - boardHeight - borderDistance);
        int tileStartX;
        int tileStartY;
        int tileEndX;
        int tileEndY;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4 ; j++) {
                tileStartX = boardX + (tileOffset * (j + 1)) + (tileLength * j);
                tileStartY = boardY + (tileOffset * (i + 1)) + (tileLength * i);
                tileEndX = boardX + (tileOffset * (j + 1)) + (tileLength * (j + 1));
                tileEndY = boardY + (tileOffset * (i + 1)) + (tileLength * (i + 1));
                canvas.drawRect(tileStartX, tileStartY, tileEndX, tileEndY, paint);
            }
        }
        return canvas;
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

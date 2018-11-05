package com.gamehub.TwentyFortyEight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
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

    // detect swipes
    private float downX, downY, upX, upY;
    private boolean isBtnDown, isBtnUp;

    // store coordinates for labels
    private int[][] tileArray;

    public Twenty48_Engine(Context context) {
        super(context);
        Log.d("Sys out", "########## GO ##########");

        this.score = 0;
        this.surfaceHolder = getHolder();
        this.paint = new Paint();
        this.tileArray = new int[16][];

        downX = downY = upX = upY = 0.0f;
        isBtnDown = isBtnUp = false;

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
        if (isBtnDown && isBtnUp) {
            isBtnDown = isBtnUp = false;
            detectSwipe();
        }
    }

    private void detectSwipe() {
        if (downX <= upX) {
            // East
            if (downY <= upY) {
                // South
                if ((upX - downX) <= (upY - downY)) {
                    // SE -> south
                    performSwipe("down");
                } else {
                    // SE -> east
                    performSwipe("right");
                }
            } else {
                // North
                if ((upX - downX) <= (downY - upY)) {
                    // NE -> north
                    performSwipe("up");
                } else {
                    // NE -> east
                    performSwipe("right");
                }
            }
        } else {
            // West
            if (downY <= upY) {
                // South
                if ((downX - upX) <= (upY - downY)) {
                    // SW -> south
                    performSwipe("down");
                } else {
                    // SW -> west
                    performSwipe("left");
                }
            } else {
                // North
                if ((downX - upX) <= (downY - upY)) {
                    // NW -> north
                    performSwipe("up");
                } else {
                    // NW -> west
                    performSwipe("left");
                }
            }
        }
    }

    private void performSwipe(String s) {
        Log.d("Sys out", s);
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

        int titleX = (int) (width * 0.1);
        int titleY = (int) (height / 4.5);

        int scoreX = (int) (width * 0.51);
        int scoreY = height / 8;
        int scoreEndX = (int) (width * 0.7);
        int scoreEndY = height / 5;
        int bestX = (int) (width * 0.71);
        int bestY = height / 8;
        int bestEndX = (int) (width * 0.9);
        int bestEndY = height / 5;

        // paints the score elements
        paint.setColor(Color.argb(255, 216, 216, 216));
        canvas.drawRect(scoreX, scoreY, scoreEndX, scoreEndY, paint);
        canvas.drawRect(bestX, bestY, bestEndX, bestEndY, paint);
        // paints the game name next to the scores
        paint.setColor(Color.BLACK);
        paint.setTextSize(40f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Score", scoreX, scoreY-7, paint);
        canvas.drawText("Best", bestX, bestY-7, paint);

        paint.setTextSize(128f);
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
                tileArray[(i * 4) + j] = new int[] {tileStartX, tileStartY, tileEndX, tileEndY};
            }
        }
        return canvas;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isBtnDown = true;
                return true;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                isBtnUp = true;
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void run() {
        while (this.isPlaying) {
            if (this.nextFrame <= System.currentTimeMillis()) {
                this.update();
                this.draw();
                // 20 frames per second
                // UNSAFE, but close to
                this.nextFrame = System.currentTimeMillis() + 50;
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

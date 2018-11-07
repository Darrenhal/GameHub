package com.gamehub.TwentyFortyEight;

import android.annotation.SuppressLint;
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

    // logic variables to run the game
    private boolean isInitialized;
    private boolean isPlaying;
    private long nextFrame;
    private int high_score;
    private int score;
    private Paint paint;
    private SurfaceHolder surfaceHolder;

    // variables to handle swipe detection
    private float downX, downY, upX, upY;
    private boolean isBtnDown, isBtnUp;

    // arrays to track board and game information
    private Twenty48_Coordinate[] tileCoordinateArray;
    private Twenty48_UiElement[] uiElementArray;
    private Twenty48_Tile[] tileArray;
    private boolean[] isTileSetArray;

    public Twenty48_Engine(Context context) {
        super(context);
        Log.d("Syso", "########## GO ##########");

        score = 0;
        high_score = 0; // hardcoded for now
        nextFrame = 0;  // first frame will be drawn immediately after thread start
        downX = downY = upX = upY = 0.0f;
        isBtnDown = isBtnUp = false;
        isInitialized = false;

        surfaceHolder = getHolder();
        paint = new Paint();

        tileCoordinateArray = new Twenty48_Coordinate[16];
        tileArray = new Twenty48_Tile[16];
        isTileSetArray = new boolean[16];
        uiElementArray = new Twenty48_UiElement[4];

        for(int i = 0; i < 16; i++) {
            isTileSetArray[i] = false;
        }
    }

    private void initialize() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            int height = canvas.getHeight();
            int width = canvas.getWidth();

            // saving the coordinates for the score board
            Twenty48_UiElement title = new Twenty48_UiElement((int) (width * 0.1), (int) (height / 4.5), 0, 0);
            Twenty48_UiElement score = new Twenty48_UiElement((int) (width * 0.51), (height / 8), (int) (width * 0.7), (height / 5));
            Twenty48_UiElement highScore = new Twenty48_UiElement((int) (width * 0.71), (height / 8), (int) (width * 0.9), (height / 5));
            uiElementArray[1] = title;
            uiElementArray[2] = score;
            uiElementArray[3] = highScore;

            // get coordinates for each tile on the board
            int boardWidth = (int) (width * 0.8);
            int border = ((width - boardWidth) / 2);
            int tileLength = (int) (boardWidth / 4.5);
            int tileOffset = ((boardWidth - (tileLength * 4)) / 5);
            int boardY = (height - boardWidth - border);
            int tileStartX, tileStartY, tileEndX, tileEndY;
            // save board coordinates as uiElement
            uiElementArray[0] = new Twenty48_UiElement(border, boardY, (border + boardWidth), (boardY + boardWidth));

            // generate board coordinates and save them
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4 ; j++) {
                    tileStartX = border + (tileOffset * (j + 1)) + (tileLength * j);
                    tileStartY = boardY + (tileOffset * (i + 1)) + (tileLength * i);
                    tileEndX = border + (tileOffset * (j + 1)) + (tileLength * (j + 1));
                    tileEndY = boardY + (tileOffset * (i + 1)) + (tileLength * (i + 1));
                    tileCoordinateArray[(i * 4) + j] = new Twenty48_Coordinate(tileStartX, tileStartY, tileEndX, tileEndY);
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
            spawnTile();
            isInitialized = true;
        }
    }

    private void spawnTile() {
        int rmdValue;
        if ((Math.random() * 100) < 60) {
            rmdValue = 2;
        } else {
            rmdValue = 4;
        }
        int rmdPosition;
        // checks for unset tile
        // -> loops until random unset position (if many) is found
        // -> creates tile on random position and returns
        // if no unset tile is found, endGame will be called
        for (int i = 0; i < isTileSetArray.length; i++) {
            if (!isTileSetArray[i]) {
                while(true) {
                    rmdPosition = (int) (Math.random() * 16);
                    if (!isTileSetArray[rmdPosition]) {
                        isTileSetArray[rmdPosition] = true;
                        Twenty48_Coordinate c = tileCoordinateArray[rmdPosition];
                        tileArray[rmdPosition] = new Twenty48_Tile(c.startX, c.startY, c.endX, c.endY, rmdValue);
                        return;
                    }
                }
            }
        }
        endGame();
    }

    private void update() {
        if (score > high_score) {
            high_score = score;
        }
        if (isBtnDown && isBtnUp) {
            isBtnDown = isBtnUp = false;
            detectSwipe();
        }
    }

    private void draw() {
        if  (this.surfaceHolder.getSurface().isValid()) {
            if (isInitialized) {
                Canvas canvas = this.surfaceHolder.lockCanvas();

                // draw uiElements
                Twenty48_UiElement board = uiElementArray[0];
                Twenty48_UiElement title = uiElementArray[1];
                Twenty48_UiElement score = uiElementArray[2];
                Twenty48_UiElement best = uiElementArray[3];

                // paint uiElements
                paint.setColor(Color.argb(255, 100, 100, 100));
                canvas.drawRect(board.startX, board.startY, board.endX, board.endY, paint);
                paint.setColor(Color.argb(255, 216, 216, 216));
                canvas.drawRect(score.startX, score.startY, score.endX, score.endY, paint);
                canvas.drawRect(best.startX, best.startY, best.endX, best.endY, paint);
                // write score board text
                paint.setColor(Color.BLACK);
                paint.setTextSize(40f);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("Score", score.startX, score.startY - 7, paint);
                canvas.drawText("Best", best.startX, best.startY-  7, paint);
                paint.setTextSize(128f);
                canvas.drawText("0x800", title.startX, title.startY, paint);
                // draw tiles to board
                for (int i = 0; i < tileArray.length; i++) {
                    if (tileArray[i] != null) {
                        Twenty48_Tile tile = tileArray[i];
                        // draw colored tile
                        paint.setColor(Color.argb(255, tile.color.red, tile.color.green, tile.color.blue));
                        canvas.drawRect(tile.startX, tile.startY, tile.endX, tile.endY, paint);
                        // draw number to tile
                        paint.setColor(Color.DKGRAY);
                        paint.setTextSize(80f);
                        paint.setTextAlign(Paint.Align.CENTER);
                        float textX = tile.startX + ((tile.endX - tile.startX) / 2);
                        float textY = tile.startY + ((tile.endY - tile.startY) / 2) - (paint.descent() +
                                paint.ascent() / 2);
                        canvas.drawText(Integer.toHexString(tile.value), textX, textY, paint);
                        paint.setTextAlign(Paint.Align.LEFT);
                    } else {
                        paint.setColor(Color.argb(255, 192, 192, 192));
                        canvas.drawRect(tileCoordinateArray[i].startX, tileCoordinateArray[i].startY, tileCoordinateArray[i].endX, tileCoordinateArray[i].endY, paint);
                    }
                }

                this.surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void endGame() {

        Log.d("Syso", "Game ended.....");
    }

    private void detectSwipe() {
        if (downX <= upX) {
            // East
            if (downY <= upY) {
                // South
                if ((upX - downX) <= (upY - downY)) {
                    // SE -> south
                    processSwipe("down");
                } else {
                    // SE -> east
                    processSwipe("right");
                }
            } else {
                // North
                if ((upX - downX) <= (downY - upY)) {
                    // NE -> north
                    processSwipe("up");
                } else {
                    // NE -> east
                    processSwipe("right");
                }
            }
        } else {
            // West
            if (downY <= upY) {
                // South
                if ((downX - upX) <= (upY - downY)) {
                    // SW -> south
                    processSwipe("down");
                } else {
                    // SW -> west
                    processSwipe("left");
                }
            } else {
                // North
                if ((downX - upX) <= (downY - upY)) {
                    // NW -> north
                    processSwipe("up");
                } else {
                    // NW -> west
                    processSwipe("left");
                }
            }
        }
    }

    private void processSwipe(String s) {

        Log.d("Syso", s);
    }

    @SuppressLint("ClickableViewAccessibility")
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
            if (!isInitialized) initialize();
            if (this.nextFrame <= System.currentTimeMillis()) {
                this.update();
                this.draw();
                // 20 frames per second
                // UNSAFE, but close to
                this.nextFrame += 50;
            }
        }
    }

    public void pause() {

        this.isPlaying = false;
    }

    public void resume() {
        this.isPlaying = true;
        Thread thread = new Thread(this);
        thread.start();
    }
}

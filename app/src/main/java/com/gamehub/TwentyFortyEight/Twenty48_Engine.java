package com.gamehub.TwentyFortyEight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
    private SharedPreferences preferences;

    // variables to handle swipe detection
    private float downX, downY, upX, upY;
    private boolean isBtnDown, isBtnUp;

    // arrays to track board and game information
    private Twenty48_Coordinate[] tileCoordinateArray = new Twenty48_Coordinate[16];
    private Twenty48_UiElement[] uiElementArray = new Twenty48_UiElement[4];
    private Twenty48_Tile[] tileArray = new Twenty48_Tile[16];
    private boolean[] isTileSetArray = new boolean[16];

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

        preferences = context.getSharedPreferences("com.gamehub", Context.MODE_PRIVATE);

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
            high_score = preferences.getInt("score2048", 0);
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
            preferences.edit().putInt("score2048", high_score).apply();
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
                paint.setTextAlign(Paint.Align.CENTER);
                float scoreTextX = score.startX + ((score.endX - score.startX) / 2);
                float scoreTextY = score.startY + ((score.endY - score.startY) / 2) - (paint.descent() +
                        paint.ascent() / 2);
                float bestTextX = best.startX + ((best.endX - best.startX) / 2);
                float bestTextY = best.startY + ((best.endY - best.startY) / 2) - (paint.descent() +
                        paint.ascent() / 2);
                canvas.drawText(Integer.toString(this.score), scoreTextX, scoreTextY, paint);
                canvas.drawText(Integer.toString(this.high_score), bestTextX, bestTextY, paint);
                paint.setTextAlign(Paint.Align.LEFT);
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
        isPlaying = false;
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
        // valueMerged holds the total value of tile merging this round
        int valueMerged = 0;
        // indices for all 4 tiles in a row/column
        int i1, i2, i3, i4;
        boolean movementHappened = false;
        for (int a = 0; a < 4; a++) {
            switch (s) {
                case "up":
                    i1 = a;
                    i2 = a + 4;
                    i3 = a + 8;
                    i4 = a + 12;
                    break;
                case "left":
                    i1 = 4 * a;
                    i2 = 4 * a + 1;
                    i3 = 4 * a + 2;
                    i4 = 4 * a + 3;
                    break;
                case "down":
                    i1 = a + 12;
                    i2 = a + 8;
                    i3 = a + 4;
                    i4 = a;
                    break;
                case "right":
                    i1 = 4 * a + 3;
                    i2 = 4 * a + 2;
                    i3 = 4 * a + 1;
                    i4 = 4 * a;
                    break;
                default:
                    i1 = i2 = i3 = i4 = 0;
                    break;

            }
            // Start moving tiles
            // no need to check for tile 1 movement
            // check tile 2
            if (isTileSetArray[i2]) {
                if (!isTileSetArray[i1]) {
                    // move pos 2 to pos 1
                    isTileSetArray[i2] = false;
                    isTileSetArray[i1] = true;
                    tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i2].value);
                    tileArray[i2] = null;
                    movementHappened = true;
                } else if (tileArray[i2].value == tileArray[i1].value) {
                    // merge pos 2 and pos 1
                    isTileSetArray[i2] = false;
                    tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i2].value * 2);
                    tileArray[i2] = null;
                    movementHappened = true;
                    valueMerged += tileArray[i1].value;
                }
            }
            // check tile 3
            if (isTileSetArray[i3]) {
                if (!isTileSetArray[i2]) {
                    if (!isTileSetArray[i1]) {
                        // move pos 3 to pos 1
                        isTileSetArray[i3] = false;
                        isTileSetArray[i1] = true;
                        tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i3].value);
                        tileArray[i3] = null;
                        movementHappened = true;
                    } else if (tileArray[i3].value == tileArray[i1].value) {
                        // merge pos 3 and pos 1
                        isTileSetArray[i3] = false;
                        tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i3].value * 2);
                        tileArray[i3] = null;
                        movementHappened = true;
                        valueMerged += tileArray[i1].value;
                    } else {
                        // move pos 3 to pos 2
                        isTileSetArray[i3] = false;
                        isTileSetArray[i2] = true;
                        tileArray[i2] = new Twenty48_Tile(tileCoordinateArray[i2].startX, tileCoordinateArray[i2].startY, tileCoordinateArray[i2].endX, tileCoordinateArray[i2].endY, tileArray[i3].value);
                        tileArray[i3] = null;
                        movementHappened = true;
                    }
                } else if (tileArray[i3].value == tileArray[i2].value) {
                    // merge pos 3 and pos 2
                    isTileSetArray[i3] = false;
                    tileArray[i2] = new Twenty48_Tile(tileCoordinateArray[i2].startX, tileCoordinateArray[i2].startY, tileCoordinateArray[i2].endX, tileCoordinateArray[i2].endY, tileArray[i3].value * 2);
                    tileArray[i3] = null;
                    movementHappened = true;
                    valueMerged += tileArray[i2].value;
                }
            }
            // check tile 4
            if (isTileSetArray[i4]) {
                if (!isTileSetArray[i3]) {
                    if (!isTileSetArray[i2]) {
                        if (!isTileSetArray[i1]) {
                            // move pos 4 to pos 1
                            isTileSetArray[i4] = false;
                            isTileSetArray[i1] = true;
                            tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i4].value);
                            tileArray[i4] = null;
                            movementHappened = true;
                        } else if (tileArray[i4].value == tileArray[i1].value) {
                            // merge pos 4 and pos 1
                            isTileSetArray[i4] = false;
                            tileArray[i1] = new Twenty48_Tile(tileCoordinateArray[i1].startX, tileCoordinateArray[i1].startY, tileCoordinateArray[i1].endX, tileCoordinateArray[i1].endY, tileArray[i4].value * 2);
                            tileArray[i4] = null;
                            movementHappened = true;
                            valueMerged += tileArray[i1].value;
                        } else {
                            // move pos 4 to pos 2
                            isTileSetArray[i4] = false;
                            isTileSetArray[i2] = true;
                            tileArray[i2] = new Twenty48_Tile(tileCoordinateArray[i2].startX, tileCoordinateArray[i2].startY, tileCoordinateArray[i2].endX, tileCoordinateArray[i2].endY, tileArray[i4].value);
                            tileArray[i4] = null;
                            movementHappened = true;
                        }
                    } else if (tileArray[i4].value == tileArray[i2].value) {
                        // merge pos 4 and pos 2
                        isTileSetArray[i4] = false;
                        tileArray[i2] = new Twenty48_Tile(tileCoordinateArray[i2].startX, tileCoordinateArray[i2].startY, tileCoordinateArray[i2].endX, tileCoordinateArray[i2].endY, tileArray[i4].value * 2);
                        tileArray[i4] = null;
                        movementHappened = true;
                        valueMerged += tileArray[i2].value;
                    } else {
                        // move pos 4 to pos 3
                        isTileSetArray[i4] = false;
                        isTileSetArray[i3] = true;
                        tileArray[i3] = new Twenty48_Tile(tileCoordinateArray[i3].startX, tileCoordinateArray[i3].startY, tileCoordinateArray[i3].endX, tileCoordinateArray[i3].endY, tileArray[i4].value);
                        tileArray[i4] = null;
                        movementHappened = true;
                    }
                } else if (tileArray[i4].value == tileArray[i3].value) {
                    // merge pos 4 and pos 3
                    isTileSetArray[i4] = false;
                    tileArray[i3] = new Twenty48_Tile(tileCoordinateArray[i3].startX, tileCoordinateArray[i3].startY, tileCoordinateArray[i3].endX, tileCoordinateArray[i3].endY, tileArray[i4].value * 2);
                    tileArray[i4] = null;
                    movementHappened = true;
                    valueMerged += tileArray[i3].value;
                }
            }
        }
        score += valueMerged;
        if (movementHappened) spawnTile();
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
                this.nextFrame += 30;
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

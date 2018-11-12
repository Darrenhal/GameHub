package com.gamehub.Snake;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Process;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;
import android.app.Activity;

import com.gamehub.R;

import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {

    private MediaPlayer mediaPlayer;
    private int mediaposition;
    private SnakeActivity snakeActivity;
    private Thread thread = null;
    private Context context;
    public enum Heading{UP,RIGHT,DOWN,LEFT};
    private Heading heading = Heading.LEFT;
    private int screenX,screenY;
    private int snakeLength;
    private int appleX,appleY;
    private int blockSize;
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;
    private long nextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;
    private int resourceIdNavigation = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
    private int resourceIdStatus = getResources().getIdentifier("status_bar_height", "dimen", "android");

    final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });


    private int navigationBarHeight = 0;
    private int statusBarHeight = 0;
    private int actionBarHeight = 0;

    private int score;
    private int[] snakeXs;
    private int[] snakeYs;

    private volatile boolean isPlaying;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    public SnakeEngine(Context context, SnakeActivity snakeActivity, Point size){
        super(context);

        this.context = context;
        this.snakeActivity = snakeActivity;


        navigationBarHeight = getResources().getDimensionPixelSize(resourceIdNavigation);
        statusBarHeight = getResources().getDimensionPixelSize(resourceIdStatus);
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        screenX = size.x;
        screenY = size.y - (navigationBarHeight+statusBarHeight+actionBarHeight);

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;

        surfaceHolder = getHolder();
        paint = new Paint();

        snakeXs = new int[200];
        snakeYs = new int[200];

        newGame();
    }

    public void run(){

        while(isPlaying){
            if(updateRequired()){
                update();
                draw();
            }
        }
    }

    public void onPause(){
        mediaPlayer.pause();
        mediaposition = mediaPlayer.getCurrentPosition();
        isPlaying = false;
        try{
            thread.join();
        }catch(InterruptedException e){

        }
    }

    public void onDestroy(){
        mediaPlayer.stop();
    }

    public void resume(){
        //mediaPlayer.seekTo(mediaposition);
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame(){
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;
        spawnApple();
        score = 0;
        nextFrameTime = System.currentTimeMillis();
        mediaPlayer = MediaPlayer.create(snakeActivity.getApplicationContext(), R.raw.snakemusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void spawnApple(){
        Random random = new Random();
        appleX = random.nextInt(NUM_BLOCKS_WIDE -1) +1;
        appleY = random.nextInt(numBlocksHigh -1) +1;
    }

    private void eatApple(){
        snakeLength++;
        spawnApple();
        score = score +1;
    }

    private void moveSnake(){
        for(int i = snakeLength; i > 0; i--){
            snakeXs[i] = snakeXs[i-1];
            snakeYs[i] = snakeYs[i-1];
        }

        switch(heading){
            case UP:
                snakeYs[0]--;
                break;
            case RIGHT:
                snakeXs[0]++;
                break;
            case DOWN:
                snakeYs[0]++;
                break;
            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean death(){
        boolean dead = false;
        if(snakeXs[0] == -1){
            dead = true;
        }
        if(snakeXs[0] >= NUM_BLOCKS_WIDE){
            dead = true;
        }
        if(snakeYs[0] == -1){
            dead = true;
        }
        if(snakeYs[0] == numBlocksHigh){
            dead = true;
        }

        for(int i = snakeLength -1; i > 0; i--){
            if ((snakeXs[0] == snakeXs[i]) && snakeYs[0] == snakeYs[i]) {
                dead = true;
            }
        }

        return dead;
    }

    public void update(){
        if(snakeXs[0] == appleX && snakeYs[0] == appleY){
            eatApple();
        }

        moveSnake();

        if(death()){
            Intent intent = new Intent(snakeActivity.getApplicationContext(),SnakeGameOver.class);
            intent.putExtra("Score: ",score);
            snakeActivity.finish();
            snakeActivity.startActivity(new Intent(snakeActivity.getApplicationContext(),SnakeGameOver.class));
        }
    }

    public void draw(){
        if(surfaceHolder.getSurface().isValid()){
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(90);
            canvas.drawText("Score: " + score, 10, 70, paint);
            for(int i = 0; i < snakeLength; i++){
                canvas.drawRect(snakeXs[i] * blockSize,(snakeYs[i] * blockSize),(snakeXs[i] * blockSize) + blockSize, (snakeYs[i] * blockSize) + blockSize, paint);
            }
            canvas.drawLine(0,0,0, numBlocksHigh * blockSize, paint);
            canvas.drawLine(0,0,NUM_BLOCKS_WIDE * blockSize, 0, paint);
            canvas.drawLine(NUM_BLOCKS_WIDE * blockSize,numBlocksHigh * blockSize,NUM_BLOCKS_WIDE * blockSize, 0, paint);
            canvas.drawLine(NUM_BLOCKS_WIDE * blockSize,numBlocksHigh * blockSize,0, numBlocksHigh * blockSize, paint);
            paint.setColor(Color.RED);
            canvas.drawRect(appleX * blockSize,(appleY * blockSize),(appleX * blockSize)+blockSize,(appleY * blockSize)+blockSize,paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired(){
        if(nextFrameTime <= System.currentTimeMillis()){
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent){
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                if(motionEvent.getX() >= screenX / 2){
                    switch(heading){
                        case UP:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.UP;
                            break;
                    }
            }else{
                    switch(heading){
                        case UP:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.UP;
                            break;
                    }
                }
        }return true;
    }
}

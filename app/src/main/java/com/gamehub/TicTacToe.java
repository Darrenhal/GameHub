package com.gamehub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TicTacToe extends SurfaceView implements Runnable{

    private long nextFrameTime;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private boolean isPlaying;
    public Thread thread;


    public TicTacToe(Context context) {
        super(context);

        this.thread = null;
        this.nextFrameTime = System.currentTimeMillis() + 100;
        this.surfaceHolder = getHolder();
        this.paint = new Paint();
        this.isPlaying = true;
    }


    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            int x = canvas.getWidth();
            int y = canvas.getHeight();


            this.paint.setColor(Color.BLUE);
            canvas.drawColor(this.paint.getColor());

            this.paint.setColor(Color.RED);
            canvas.drawRect(0,0,50,50, paint);
            canvas.drawRect(x - 50,0, x,50, paint);
            canvas.drawRect(0,y - 50,50, y, paint);
            canvas.drawRect(x - 50,y - 50, x, y, paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.draw();
        return true;
    }

    @Override
    public void run() {

        while (this.isPlaying) {
            if (this.nextFrameTime <= System.currentTimeMillis()) {
                System.out.println(this.nextFrameTime);
                this.nextFrameTime = System.currentTimeMillis() + 1000;
                this.draw();
            }
            //////////////////////// to not make it crash, doesn't close correctly yet
            //this.isPlaying = false;
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

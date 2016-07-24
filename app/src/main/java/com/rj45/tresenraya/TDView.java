package com.rj45.tresenraya;

/**
 * Created by Usuario on 23/07/2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class TDView extends SurfaceView implements
        Runnable{

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private List<EnemyShip> enemies = new ArrayList<EnemyShip>();
    private List<SpaceDust> dusts = new ArrayList<SpaceDust>();
    private int points;
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private int screenX;
    private int screenY;
    private Context context;

    public TDView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = maxX;
        screenY = maxY;
        startGame();
    }

    public void startGame() {
        points = 0;
        player = new PlayerShip(context, screenX, screenY);
        enemies.clear();
        dusts.clear();
        for(int i = 0; i < 4; i++) {
            enemies.add(new EnemyShip(context, screenX, screenY));
        }
        for(int i = 0; i < 40; i++) {
            dusts.add(new SpaceDust(screenX, screenY));
        }
        // Reset time and distance
        distanceRemaining = 10000;// 10 km
        timeTaken = 0;
        // Get start time
        timeStarted = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }

    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }

    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {

        for(final EnemyShip enemy:enemies) {
            if(Rect.intersects(player.getHitBox(), enemy.getHitBox())) {
                enemy.setX(-200);
                player.decreaseShield();
            }
        }

        if (player.getShield() < 0) {
            startGame();
        }

        player.update();
        for(final EnemyShip enemy:enemies) {
            enemy.update(player.getSpeed());
            if(enemy.isSurpassed()) {
                points++;
                enemy.setSurpassed(false);
            }
        }

        for(final SpaceDust dust:dusts) {
            dust.update(player.getSpeed());
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();
            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            for (final EnemyShip enemy: enemies) {
                canvas.drawBitmap(
                        enemy.getBitmap(),
                        enemy.getX(),
                        enemy.getY(),
                        paint);
            }

            paint.setColor(Color.argb(255, 255, 255, 255));

            for (final SpaceDust dust : dusts) {
                canvas.drawPoint(dust.getX(), dust.getY(), paint);
            }

            // Draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            paint.setColor(Color.CYAN);
            paint.setTextSize(60);
            canvas.drawText(String.valueOf(points), 100, 100, paint);
// Draw the hud
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(25);
            canvas.drawText("Fastest:"+ fastestTime + "s", 10, 20, paint);
            canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20,
                    paint);
            canvas.drawText("Distance:" +
                    distanceRemaining / 1000 +
                    " KM", screenX / 3, screenY - 20, paint);
            canvas.drawText("Shield:" +
                    player.getShield(), 10, screenY - 20, paint);

            canvas.drawText("Speed:" +
                    player.getSpeed() * 60 +
                    " MPS", (screenX /3 ) * 2, screenY - 20, paint);
            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);

        }
    }

    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // There are many different events in MotionEvent
        // We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
                player.stopBoost();
                break;
            // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                player.startBoost();
                break;
        }
        return true;
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            //Vacío por diseño.
        }
    }
}
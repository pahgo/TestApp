package com.rj45.tresenraya;

/**
 * Created by Usuario on 23/07/2016.
 */
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
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
    private List<EnemyShip> enemies = new ArrayList<>();
    private List<SpaceDust> dusts = new ArrayList<SpaceDust>();
    private int points;
    private long timeTaken;

    private long highestPoints;
    private int screenX;
    private int screenY;
    private Context context;
    private boolean gameEnded = false;
    MediaPlayer mediaPlayer = null;

    public TDView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = maxX;
        screenY = maxY;
        mediaPlayer = MediaPlayer.create(context, R.raw.flappenny);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
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
        timeTaken = 0;
        gameEnded = false;
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
        mediaPlayer.pause();
        if(gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {

            }
        }
    }

    public void resume() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playing = true;
            mediaPlayer.start();
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    private void update() {
        boolean addEnemy = false;

        if (player.getShield() < 0) {
            endGame();
        }

        if(!gameEnded) {
            List<EnemyShip> enemyCopies = new ArrayList<EnemyShip>();
            enemyCopies.addAll(enemies);
            for (final EnemyShip enemy : enemyCopies) {
                if (Rect.intersects(player.getHitBox(), enemy.getHitBox())) {
                    enemy.setX(-200);
                    player.decreaseShield();
                }
            }

            player.update();
            for (final EnemyShip enemy : enemyCopies) {
                enemy.update(player.getSpeed());
                if (enemy.isSurpassed()) {
                    points++;
                    if (points % 100 == 0) {
                        addEnemy = true;
                    }
                    enemy.setSurpassed(false);
                }
            }
            if (addEnemy) {
                enemies.add(new EnemyShip(context, screenX, screenY));
            }

            for (final SpaceDust dust : dusts) {
                dust.update(player.getSpeed());
            }
        }

    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();
            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            List<EnemyShip> enemyCopies = new ArrayList<EnemyShip>();
            enemyCopies.addAll(enemies);

            for (final EnemyShip enemy: enemyCopies) {
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
            if(!gameEnded) {
                // Draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest:"+ highestPoints + "s", 10, 20, paint);
                canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20,
                        paint);
                canvas.drawText("Shield:" +
                        player.getShield(), 10, screenY - 20, paint);

                canvas.drawText("Speed:" +
                        player.getSpeed() * 60 +
                        " MPS", (screenX /3 ) * 2, screenY - 20, paint);
            }
            else {
                // Show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Highest:"+
                        highestPoints + "s", screenX/2, 160, paint);

                canvas.drawText("Time:" + timeTaken +
                        "s", screenX / 2, 200, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 350, paint);
            }
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
                if(gameEnded) {
                    startGame();
                }
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

    private void endGame() {
        gameEnded = true;

        if(highestPoints < points){
            highestPoints = points;
        }
    }
}
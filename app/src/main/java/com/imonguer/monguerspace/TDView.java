package com.imonguer.monguerspace;

/**
 * Created by Usuario on 23/07/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TDView extends SurfaceView implements
        Runnable{

    private boolean gServicesActive = false;
    private int difficulty = 1;
    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Paint invulnerabilityPaint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private List<EnemyShip> enemies = new ArrayList<>();
    private List<SpaceDust> dusts = new ArrayList<>();

    private Shield shield;
    private int points;
    private long highestPoints;
    private int screenX;
    private int screenY;
    private Context context;
    private boolean gameEnded = false;
    private MediaPlayer mediaPlayer = null;
    private boolean invulnerability = false;
    private Date invulnerabilityTimer;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Planet planet;
    private long startFrameTime;
    private long timeThisFrame;
    private long fps = 0;
    private boolean debugEnabled = true;
    private boolean showPlanets = true;



    public TDView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = maxX;
        screenY = maxY;
        mediaPlayer = MediaPlayer.create(context, R.raw.lookout);
        mediaPlayer.setVolume(0.5F, 0.5F);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        prefs = context.getSharedPreferences("HiScores",
                Context.MODE_PRIVATE);
        editor = prefs.edit();
        highestPoints = prefs.getLong("highestPoints", 0);


        invulnerabilityPaint = new Paint();
        invulnerabilityPaint.setStyle(Paint.Style.STROKE);
        invulnerabilityPaint.setTextSize(80);
        invulnerabilityPaint.setColor(Color.WHITE);

        startGame();
    }

    public void startGame() {

        difficulty = 1;
        points = 0;
        player = new PlayerShip(context, screenX, screenY);
        enemies.clear();
        dusts.clear();

        shield = new Shield(context, screenX, screenY);
        shield.stopDraw();
        for(int i = 0; i < 4; i++) {
            enemies.add(new EnemyShip(context, screenX, screenY));
        }
        for(int i = 0; i < 40; i++) {
            dusts.add(new SpaceDust(screenX, screenY));
        }

        if(showPlanets) {
            planet = new Planet(context, screenX, screenY);
            planet.stopDraw();
        }

        gameEnded = false;
    }

    @Override
    public void run() {
        while (playing) {
            if (debugEnabled) {
                startFrameTime = System.currentTimeMillis();
            }
            update();
            draw();
            control();
            if (debugEnabled) {
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
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
        gServicesActive = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    private void update() {
        boolean addEnemy = false;

        if (player.getShield() < 0) {
            endGame();
        }

        if(!gameEnded) {
            List<EnemyShip> enemyCopies = new ArrayList<EnemyShip>();
            enemyCopies.addAll(enemies);
            if(!invulnerability) {
                for (final EnemyShip enemy : enemyCopies) {
                    if (Rect.intersects(player.getHitBox(), enemy.getHitBox())) {
                        enemy.setX(-200);
                        player.decreaseShield();
                        invulnerability = true;
                        invulnerabilityTimer = new Date();
                        EnemyShip.hitActions();
                    }
                }
            }

            if (shield.needToDraw()) {
                shield.update();
                if (Rect.intersects(player.getHitBox(), shield.getHitBox())) {
                    shield.respawn();
                    shield.stopDraw();
                    player.increaseShield();
                    Shield.hitActions();
                }
            }

            if (shield.canDraw()) {
                shield.startDraw();
            }

            if(invulnerability && null != invulnerabilityTimer && (invulnerabilityTimer.getTime() + 3000) <= new Date().getTime()){
                invulnerability = false;
            }

            player.update();
            for (final EnemyShip enemy : enemyCopies) {
                enemy.update(player.getSpeed());
                if (enemy.isSurpassed()) {
                    points++;
                    if (points == 100) {
                        addEnemy = true;
                        difficulty++;
                    } else if (points % (125 * difficulty) == 0) {
                        addEnemy = true;
                        difficulty++;
                    }
                    enemy.setSurpassed(false);
                }
            }
            if (addEnemy && enemies.size() < 8) {
                enemies.add(new EnemyShip(context, screenX, screenY));
            }

            for (final SpaceDust dust : dusts) {
                dust.update(player.getSpeed());
            }

            if(showPlanets) {
                if(planet.needToDraw()){
                    planet.update();
                } else if (planet.canDraw()) {
                    planet.startDraw();
                }
            }

            player.increaseFrameCount();
        }
    }

    /**
     * Creado por tema de claridad y legibilidad de código.
     */
    private void clean() {
        canvas.drawColor(Color.argb(255, 0, 0, 0));
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            // Borramos la pantalla.
            clean();

            List<EnemyShip> enemyCopies = new ArrayList<EnemyShip>();
            enemyCopies.addAll(enemies);

            // Pintamos desde la capa más interior a la más exterior: Planetas.
            if (showPlanets) {
                planet.draw(canvas, paint);
            }

            // Polvo espacial.
            for (final SpaceDust dust : dusts) {
                dust.draw(canvas, paint);
            }

            // Enemigos.
            for (final EnemyShip enemy: enemyCopies) {
                enemy.draw(canvas, paint);
            }

            // Escudos.
            shield.draw(canvas, paint);

            // Invulnerabilidad.
            if(invulnerability) {
                canvas.drawText(getResources().getString(R.string.invulnerability), (screenX * 7) / 20, screenY / 2, invulnerabilityPaint);
                canvas.drawCircle(player.getX() + player.getBitmap().getWidth() / 2,
                        player.getY() + player.getBitmap().getHeight() / 2, 100, invulnerabilityPaint);
            }

            // Jugador.
            player.draw(canvas, paint);

            // HUD / Game over.
            if(!gameEnded) {
                drawHUD();
            }
            else {
                drawGameOverScreen();
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameOverScreen() {
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", screenX / 2, 100, paint);
        canvas.drawText(getResources().getString(R.string.replay), screenX / 2, 350, paint);
        paint.setTextSize(25);
        canvas.drawText(getResources().getString(R.string.highscore) + Constants.DOTS +
                highestPoints, screenX / 2, 160, paint);
    }

    private void drawHUD() {
        paint.setColor(Color.CYAN);
        paint.setTextSize(60);
        canvas.drawText(String.valueOf(points), 100, 100, paint);

        if (debugEnabled) {
            canvas.drawText("FPS: " + String.valueOf(fps), 100, screenY - 100, paint);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(25);
        canvas.drawText(getResources().getString(R.string.highscore) + Constants.DOTS + highestPoints, 10, 20, paint);
        canvas.drawText(getResources().getString(R.string.shield) + ": " +
                player.getShield(), 10, screenY - 20, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoost();
                break;
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
            Thread.sleep(15);
        } catch (InterruptedException e) {
            //Vacío por diseño.
        }
    }

    private void endGame() {
        gameEnded = true;
        invulnerability = false;
        if(highestPoints < points){
            highestPoints = points;
            editor.putLong("highestPoints", highestPoints);
            editor.commit();
        }
    }
}

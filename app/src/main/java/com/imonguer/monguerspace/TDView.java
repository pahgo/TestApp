package com.imonguer.monguerspace;

/**
 * Created by Usuario on 23/07/2016.
 *
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class TDView extends SurfaceView implements
        Runnable{

    public static final double MAGIC_CONSTANT_Y = 1080.0; //si en mi movil de 1920/1080 va bien... hacemos proporcional con esta constante mágica!
    private static final double MAGIC_CONSTANT_X = 1920.0;
    public static int timesTouch;
    volatile boolean playing;
    Thread gameThread = null;
    private boolean boosting = false;
    private int difficulty = 1;
    private int points = 0;
    private PlayerShip player;
    private Paint paint;
    private Paint invulnerabilityPaint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private List<EnemyShip> enemies = new ArrayList<>();
    private List<SpaceDust> dusts = new ArrayList<>();
    private Typeface face;
    private Shield shield;
    private int enemiesSurpassedTotal;
    private long highestPoints;
    private int screenX;
    private int screenY;
    private Context context;
    private boolean gameEnded = false;
    private MediaPlayer mediaPlayer = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Planet planet;
    private long startFrameTime;
    private long fps = 0;
    private boolean showPlanets = true;
    private int framesWithoutCrash = 0;
    private Timer invulnerableTimer;
    private boolean pauseUI = false;
    private int minDistanceToCheck;

    /*FIN - fferezsa - Corrección de FPS en dispositivos rápidos/lentos*/
    public TDView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        face = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
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
        editor.apply();

        invulnerabilityPaint = new Paint();
        invulnerabilityPaint.setStyle(Paint.Style.STROKE);
        invulnerabilityPaint.setTextSize(80);
        invulnerabilityPaint.setColor(Color.WHITE);

        startGame();
    }

    public int getTimesTouch() {
        return timesTouch;
    }

    public void setTimesTouch(int timesTouch) {
        TDView.timesTouch = timesTouch;
    }

    public void startGame() {
        pauseUI = false;
        invulnerableTimer = new Timer(0);
        points = 0;
        difficulty = 1;
        enemiesSurpassedTotal = 0;
        player = new PlayerShip(context, screenX, screenY);
        minDistanceToCheck = player.getX() + player.getBitmap().getWidth() + 1;
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
            /*INI - fferezsa - Corrección de FPS en dispositivos rápidos/lentos*/
            startFrameTime = System.currentTimeMillis();
            /*FIN - fferezsa - Corrección de FPS en dispositivos rápidos/lentos*/
            update();
            draw();
            control();
            if (Constants.DEBUG_ENABLED) {
                long timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }
    }

    public void pause() {
        playing = false;
        mediaPlayer.pause();
        shield.pause();
        invulnerableTimer.pause();
        if(gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                //empty
            }
        }
    }

    public void resume() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            invulnerableTimer.resume();
            shield.resume();
            playing = true;
            mediaPlayer.start();
            gameThread = new Thread(this);
            gameThread.start();
            timesTouch = 0;
        }
    }

    private void update() {
        long ini = System.nanoTime();
        if (!pauseUI) {
            boolean addEnemy = false;

            if (player.getShield() < 0) {
                endGame();
            }

            if (!gameEnded) {
                // Si estamos acelerando, ganamos el triple de puntos por frame.
                if (boosting) {
                    points += 3;
                } else {
                    points++;
                }
                List<EnemyShip> enemyCopies = new ArrayList<>();
                enemyCopies.addAll(enemies);
                if (invulnerableTimer.isExpired()) {
                    int i = 0;
                    for (final EnemyShip enemy : enemyCopies) {
                        if (minDistanceToCheck >= enemy.getX() && Rect.intersects(player.getHitBox(), enemy.getHitBox())) {
                            enemy.setX(-200);
                            player.decreaseShield();
                            invulnerableTimer = new Timer(Constants.INVULNERABLE_TIME);
                            EnemyShip.hitActions();
                            framesWithoutCrash = 0;
                            break;
                        }
                    }
                }
                if (shield.needToDraw()) {
                    shield.update();
                    if (minDistanceToCheck >= shield.getX() && Rect.intersects(player.getHitBox(), shield.getHitBox())) {
                        points += 2500;
                        shield.respawn();
                        shield.stopDraw();
                        player.increaseShield();
                        Shield.hitActions();
                    }
                }
                player.update();
                for (final EnemyShip enemy : enemyCopies) {
                    enemy.update(player.getSpeed());
                    if (enemy.isSurpassed()) {
                        points += 250;
                        enemiesSurpassedTotal++;
                        if (enemiesSurpassedTotal == 100) {
                            addEnemy = true;
                            difficulty++;
                        } else if (enemiesSurpassedTotal % (125 * difficulty) == 0) {
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

                if (showPlanets) {
                    if (planet.needToDraw()) {
                        planet.update();
                    } else if (planet.canDraw()) {
                        planet.startDraw();
                    }
                }
                Log.d("Rendimiento", "5: " + (System.nanoTime() - ini));

                framesWithoutCrash++;
                player.increaseFrameCount();
            }
        }
    }

    /**
     * Creado por tema de claridad y legibilidad de código.
     */
    private void clean() {
        canvas.drawColor(Color.argb(255, 0, 0, 0));
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            paint.setTypeface(face);

            // Borramos la pantalla.
            clean();

            List<EnemyShip> enemyCopies = new ArrayList<>();
            enemyCopies.addAll(enemies);

            //INI - Pete brutal por concurrentModification - 20160818
            List<SpaceDust> dustCopies = new ArrayList<>();
            dustCopies.addAll(dusts);
            //FIN - Pete brutal por concurrentModification - 20160818

            // Pintamos desde la capa más interior a la más exterior: Planetas.
            if (showPlanets) {
                planet.draw(canvas, paint);
            }

            // Polvo espacial.
            //INI - Pete brutal por concurrentModification - 20160818
            for (final SpaceDust dust : dustCopies) {
                dust.draw(canvas, paint);
            }
            //FIN - Pete brutal por concurrentModification - 20160818

            // Enemigos.
            for (final EnemyShip enemy: enemyCopies) {
                enemy.draw(canvas, paint);
            }

            // Escudos.
            shield.draw(canvas, paint);

            // Invulnerabilidad.
            if (!invulnerableTimer.isExpired()) {
                invulnerabilityPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(getResources().getString(R.string.invulnerability), screenX / 2, screenY / 2, invulnerabilityPaint);
                canvas.drawCircle(player.getX() + player.getBitmap().getWidth() / 2,
                        player.getY() + player.getBitmap().getHeight() / 2, screenY / 10, invulnerabilityPaint);
            }

            // Jugador.
            player.draw(canvas, paint);

            if (pauseUI && !gameEnded) {
                drawPause();
            }
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

    private void drawPause() {
        /*INI - fferezsa - Tamaño de texto se sale de pantalla*/
        paint.setTextAlign(Paint.Align.CENTER);
        int size = screenX / (getResources().getString(R.string.replay).length() - 5);
        paint.setTextSize(size);
        canvas.drawText(getResources().getString(R.string.pause), screenX / 2, (int) (100 * screenY / MAGIC_CONSTANT_Y), paint);
        canvas.drawText(getResources().getString(R.string.tap_to_continue), screenX / 2, (int) (450 * screenY / MAGIC_CONSTANT_Y), paint);
    }

    private void drawGameOverScreen() {
        /*INI - fferezsa - Tamaño de texto se sale de pantalla*/
        paint.setTextAlign(Paint.Align.CENTER);
        int size = screenX / (getResources().getString(R.string.replay).length() - 5);
        paint.setTextSize(size);
        /*FIN - fferezsa - Tamaño de texto se sale de pantalla*/
        canvas.drawText(getResources().getString(R.string.GAME_OVER), screenX / 2, (int) (100 * screenY / MAGIC_CONSTANT_Y), paint);
        canvas.drawText(getResources().getString(R.string.points) + Constants.DOTS + " " + points, screenX / 2, (int) (350 * screenY / MAGIC_CONSTANT_Y), paint);
        canvas.drawText(getResources().getString(R.string.replay), screenX / 2, (int) (450 * screenY / MAGIC_CONSTANT_Y), paint);

        paint.setTextSize(25);
        canvas.drawText(getResources().getString(R.string.highscore) + Constants.DOTS +
                highestPoints, screenX / 2, 160, paint);
    }

    private void drawHUD() {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.GREEN);
        paint.setTextSize(40);
        canvas.drawText(getResources().getString(R.string.points) + ": " + String.valueOf(points), screenX - (int) (450 * screenX / MAGIC_CONSTANT_X), (int) (80 * screenY / MAGIC_CONSTANT_Y), paint);
        canvas.drawText(getResources().getString(R.string.shield) + ": " + player.getShield(), screenX - (int) (450 * screenX / MAGIC_CONSTANT_X), (int) (160 * screenY / MAGIC_CONSTANT_Y), paint);
        paint.setTextSize(25);
        canvas.drawText(getResources().getString(R.string.highscore) + Constants.DOTS + highestPoints, (int) (10 * screenX / MAGIC_CONSTANT_X), (int) (20 * screenY / MAGIC_CONSTANT_Y), paint);

        if (Constants.DEBUG_ENABLED) {
            canvas.drawText("FPS: " + String.valueOf(fps), 100, screenY - 100, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoost();
                boosting = true;
                break;
            case MotionEvent.ACTION_DOWN:
                if (pauseUI) {
                    pauseUI = false;
                    resume();
                }
                boosting = false;
                player.startBoost();
                if(gameEnded) {
                    goToMain();
                }
                break;
        }
        return true;
    }

    private void goToMain() {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("POINTS", points);
        context.startActivity(i);
    }

    private void control() {
        try {
            /*INI - fferezsa - Corrección de FPS en dispositivos rápidos/lentos*/
            final Long remainingTimeToDisplay = System.currentTimeMillis() - startFrameTime;
            long TIME_NEEDED_TO_50_FPS = 1000 / 50;
            if (TIME_NEEDED_TO_50_FPS - remainingTimeToDisplay > 0) {
                Thread.sleep(TIME_NEEDED_TO_50_FPS - remainingTimeToDisplay);
            }
            /*FIN - fferezsa - Corrección de FPS en dispositivos rápidos/lentos*/
        } catch (InterruptedException e) {
            //Vacío por diseño.
        }
        controlAchievements();
    }

    private void controlAchievements() {
        if (MyGoogleApi.getInstance(null) != null && MyGoogleApi.getInstance(null).getClient() != null && MyGoogleApi.getInstance(null).getClient().isConnected()) {
            if (points > 9000) {
                if (MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000))) {
                    MyGoogleApi.getInstance(null).revealAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_ii));
                }
            }
            if (points > 90000) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_ii));
                MyGoogleApi.getInstance(null).revealAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_iii));
            }
            if (points > 900000) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_iii));
            }
            if (player.getShield() < 2) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_asteroid_crasher));
            }
            if (framesWithoutCrash >= 60 * 50) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_keep_calm_i_take_the_controls));
            }
        }
    }

    private void endGame() {
        gameEnded = true;
        invulnerableTimer = new Timer(0);
        if(highestPoints < points){
            highestPoints = points;
            editor.putLong("highestPoints", highestPoints);
            editor.commit();
        }
    }

    public void setUIPause() {
        pauseUI = true;
    }
}

package com.imonguer.monguerspace;

/**
 * Created by Usuario on 23/07/2016.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class InstructionsView extends SurfaceView implements
        Runnable {

    private int points = 0;
    private Paint paintShield;
    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private List<EnemyShip> enemies = new ArrayList<>();
    private List<SpaceDust> dusts = new ArrayList<>();

    private Typeface face;
    private Shield shield;
    private int screenX;
    private int screenY;
    private Context context;
    private int step;

    public InstructionsView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        face = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
        ourHolder = getHolder();
        paint = new Paint();
        paintShield = new Paint();
        screenX = maxX;
        screenY = maxY;
        step = 1;
        paintShield.setStyle(Paint.Style.STROKE);
        paintShield.setTextSize(80);
        paintShield.setColor(Color.WHITE);

        startGame();
    }

    public void startGame() {
        points = 127482;
        player = new PlayerShip(context, screenX, screenY);
        player.increaseShield();
        player.increaseShield();
        enemies.clear();
        dusts.clear();

        shield = new Shield(context, screenX, screenY);
        shield.setX(screenX / 2);
        shield.forceDraw();
        for (int i = 0; i < 6; i++) {
            EnemyShip enemy = new EnemyShip(context, screenX, screenY);
            enemy.setX(screenX / (i + 1) + 100);
            enemies.add(enemy);
        }
        for (int i = 0; i < 40; i++) {
            dusts.add(new SpaceDust(screenX, screenY));
        }
    }

    @Override
    public void run() {
        while (playing) {
            draw();
            control();
        }
    }

    public void pause() {
        playing = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {

            }
        }
    }

    public void resume() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
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
            paint.setTypeface(face);

            // Borramos la pantalla.
            clean();

            List<EnemyShip> enemyCopies = new ArrayList<EnemyShip>();
            enemyCopies.addAll(enemies);

            List<SpaceDust> dustCopies = new ArrayList<SpaceDust>();
            dustCopies.addAll(dusts);

            // Polvo espacial.
            //INI - Pete brutal por concurrentModification - 20160818
            for (final SpaceDust dust : dustCopies) {
                dust.draw(canvas, paint);
            }
            //FIN - Pete brutal por concurrentModification - 20160818

            // Enemigos.
            for (final EnemyShip enemy : enemyCopies) {
                enemy.draw(canvas, paint);
            }

            if (step == 3) {
                canvas.drawCircle(shield.getX() + shield.getBitmap().getWidth() / 2,
                        shield.getY() + shield.getBitmap().getHeight() / 2, 100, paintShield);
            }
            // Escudos.
            shield.draw(canvas, paint);

            // Jugador.
            player.draw(canvas, paint);

            drawHUD();

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }


    private void drawHUD() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.CYAN);
        paint.setTextSize(40);
        switch (step) {
            case 1:
                canvas.drawText(getResources().getString(R.string.instruct1), screenX / 2, (screenY / 2) - 50, paint);
                canvas.drawText(getResources().getString(R.string.instruct2), screenX / 2, (screenY / 2), paint);
                canvas.drawText(getResources().getString(R.string.instruct3), screenX / 2, (screenY / 2) + 50, paint);
                canvas.drawText(getResources().getString(R.string.more), screenX / 2, (screenY - 100), paint);
                break;
            case 2:
                canvas.drawText(getResources().getString(R.string.instruct4), screenX / 2, (screenY / 2) - 50, paint);
                canvas.drawText(getResources().getString(R.string.instruct5), screenX / 2, (screenY / 2), paint);
                canvas.drawText(getResources().getString(R.string.instruct6), screenX / 2, (screenY / 2) + 50, paint);
                canvas.drawText(getResources().getString(R.string.more), screenX / 2, (screenY - 100), paint);
                break;
            case 3:
                canvas.drawText(getResources().getString(R.string.instruct7), screenX / 2, (screenY / 2) - 50, paint);
                canvas.drawText(getResources().getString(R.string.instruct8), screenX / 2, (screenY / 2), paint);
                canvas.drawText(getResources().getString(R.string.instruct9), screenX / 2, (screenY / 2) + 50, paint);
                canvas.drawText(getResources().getString(R.string.more), screenX / 2, (screenY - 100), paint);
                break;
            default:
                canvas.drawText(getResources().getString(R.string.instruct10), screenX / 2, screenY / 2, paint);
                break;

        }
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.GREEN);
        paint.setTextSize(40);
        canvas.drawText(getResources().getString(R.string.points) + ": " + String.valueOf(points), screenX - 450, 80, paint);
        canvas.drawText(getResources().getString(R.string.shield) + ": " + player.getShield(), screenX - 450, 160, paint);
        paint.setTextSize(25);
        canvas.drawText(getResources().getString(R.string.highscore) + Constants.DOTS + 159335, 10, 20, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                step++;
                break;
        }
        return true;
    }

    private void control() {
        try {
            Thread.sleep(125);
        } catch (InterruptedException e) {
            //Vacío por diseño.
        }
    }
}

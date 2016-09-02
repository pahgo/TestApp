package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Vibrator;

import java.util.Random;

/**
 * Created by Usuario on 23/07/2016.
 *
 */
public class EnemyShip {
    private static Bitmap firstBitmap = null;
    private static Bitmap secondBitmap = null;
    private static MediaPlayer mediaExplosions = null;
    private static Vibrator vibrator = null;
    private Bitmap shownBitmap = null;
    private int x, y;
    private int speed = 1;
    // Detect enemies leaving the screen
    private int maxX;
    private int minX;
    // Spawn enemies within screen bounds
    private int maxY;
    private Rect hitBox;
    private boolean surpassed;


    public EnemyShip(Context context, int screenX, int screenY){
        setMediaExplosions(context);
        setVibrator(context);

        if (firstBitmap == null || secondBitmap == null) {
            firstBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid);
            secondBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid2);
            firstBitmap = Bitmap.createScaledBitmap(firstBitmap, (screenX / 20), (screenY / 15), false);
            secondBitmap = Bitmap.createScaledBitmap(secondBitmap, (screenX / 20), (screenY / 15), false);
        }
        Random generator = new Random();
        maxX = screenX;
        setSpeed(generator, maxX / 100);
        shownBitmap = (speed % 2 == 0) ? firstBitmap : secondBitmap;
        maxY = screenY - firstBitmap.getHeight();
        minX = 0;
        x = screenX + generator.nextInt(500);
        y = generator.nextInt(maxY);
        hitBox = new Rect(x, y, shownBitmap.getWidth(), shownBitmap.getHeight());
        surpassed = false;
    }

    public static void hitActions() {
        mediaExplosions.start();
        vibrator.vibrate(Constants.VIBRATION_TIME);
    }

    private void setSpeed(Random generator, int n) {
        speed = generator.nextInt(n) + 10;
    }

    private void setVibrator(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    private void setMediaExplosions(Context context) {
        if (mediaExplosions == null) {
            mediaExplosions = MediaPlayer.create(context, R.raw.explosion2);
            mediaExplosions.setLooping(false);
            mediaExplosions.setVolume(1, 1);
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed){

        // Move to the left
        x -= playerSpeed;
        x -= speed;
        //respawn when off screen
        if (x < minX - firstBitmap.getWidth()) {
            Random generator = new Random();
            setSpeed(generator, maxX / 100);
            shownBitmap = (speed % 2 == 0) ? firstBitmap : secondBitmap;
            x = maxX;
            y = generator.nextInt(maxY);
            surpassed = true;
        }

        // Refresh hitActions box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + shownBitmap.getWidth();
        hitBox.bottom = y + shownBitmap.getHeight();

        hitBox = new Rect(x, y, x + shownBitmap.getWidth(), y + shownBitmap.getHeight());
    }

    public boolean isSurpassed() {
        return surpassed;
    }

    public void setSurpassed(boolean surpassed) {
        this.surpassed = surpassed;
    }

    public Rect getHitBox() { return hitBox; }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(shownBitmap, x, y, paint);
    }
}
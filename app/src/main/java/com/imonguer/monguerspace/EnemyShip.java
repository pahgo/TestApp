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
 */
public class EnemyShip {
    private static Bitmap bitmap = null;
    private int x, y;
    private int speed = 1;
    // Detect enemies leaving the screen
    private int maxX;
    private int minX;
    // Spawn enemies within screen bounds
    private int maxY;
    private int minY;
    private Rect hitBox;
    private boolean surpassed;
    private static MediaPlayer mediaExplosions = null;
    private static Vibrator vibrator = null;


    public EnemyShip(Context context, int screenX, int screenY){
        if (mediaExplosions == null) {
            mediaExplosions = MediaPlayer.create(context, R.raw.explosion2);
            mediaExplosions.setLooping(false);
            mediaExplosions.setVolume(1, 1);
        }

        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource
                    (context.getResources(), R.drawable.asteroid);
            bitmap = Bitmap.createScaledBitmap(bitmap, (screenX / 20), (screenY / 15), false);
        }
        maxX = screenX;
        maxY = screenY - bitmap.getHeight();
        minX = 0;
        minY = 0;
        Random generator = new Random();
        speed = generator.nextInt(6)+10;
        x = screenX + generator.nextInt(500);
        y = generator.nextInt(maxY);
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        surpassed = false;
    }

    public static void hitActions() {
        mediaExplosions.start();
        vibrator.vibrate(Constants.VIBRATION_TIME);
    }

    //Getters and Setters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed){

        // Move to the left
        x -= playerSpeed;
        x -= speed;
        //respawn when off screen
        if(x < minX-bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(10)+10;
            x = maxX;
            y = generator.nextInt(maxY);
            surpassed = true;
        }

        // Refresh hitActions box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public boolean isSurpassed() {
        return surpassed;
    }

    public void setSurpassed(boolean surpassed) {
        this.surpassed = surpassed;
    }

    public Rect getHitBox() { return hitBox; }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
    }
}
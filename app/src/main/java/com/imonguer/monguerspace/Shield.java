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

public class Shield {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int maxY;
    private Rect hitBox;
    private boolean draw;
    private boolean surpassed;
    private static MediaPlayer mediaShield = null;
    private static Vibrator vibrator = null;
    private boolean forceDraw;
    private Timer timer;

    public Shield(Context context, int screenX, int screenY) {
        forceDraw = false;
        if (mediaShield == null) {
            mediaShield = MediaPlayer.create(context, R.raw.good);
            mediaShield.setLooping(false);
            mediaShield.setVolume(1, 1);
        }

        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        bitmap = BitmapFactory.decodeResource
                (context.getResources(), R.drawable.shield);
        bitmap = Bitmap.createScaledBitmap(bitmap, (screenX / 16), (screenY / 10), false);
        maxX = screenX;
        maxY = screenY - bitmap.getHeight();
        minX = 0;
        Random generator = new Random();
        speed = 20;
        x = screenX;
        y = generator.nextInt(maxY);
        y = y > maxY-50 ? y-50 : y;      //NO se meta en el banner
        hitBox = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
        surpassed = false;
        timer = new Timer(Constants.TIME_BETWEEN_SHIELDS);
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

    public void forceDraw() {
        forceDraw = true;
    }

    public void update() {
        x -= speed;
        //respawn when off screen
        if (x < minX - bitmap.getWidth()) {
            surpassed = true;
            respawn();
        }

        // Refresh hitActions box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void startDraw() {
        draw = true;
        respawn();
    }

    public void stopDraw() {
        timer = new Timer(Constants.TIME_BETWEEN_SHIELDS);
        draw = false;
    }

    public void respawn() {
        Random generator = new Random();
        speed = generator.nextInt(10) + 10;
        x = maxX;
        y = generator.nextInt(maxY);
        y = y > maxY-50 ? y-50 : y;      //NO se meta en el banner
        if (surpassed) {
            timer = new Timer(Constants.TIME_BETWEEN_SHIELDS);
            surpassed = false;
        }
    }

    public boolean needToDraw() {
        return canDraw();
    }

    public boolean canDraw() {
        return timer.isExpired();
    }

    public void draw(Canvas canvas, Paint paint) {
        if (forceDraw || needToDraw()) {
            canvas.drawBitmap(getBitmap(), getX(), getY(), paint);
        }
    }

    public static void hitActions() {
        mediaShield.start();
        vibrator.vibrate(Constants.VIBRATION_TIME);
    }

    public void pause() {
        timer.pause();
    }

    public void resume() {
        timer.resume();
    }
}

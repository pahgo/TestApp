package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Date;
import java.util.Random;

/**
 * Created by Usuario on 27/07/2016.
 */
public class Shield {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int maxY;
    private Rect hitBox;
    private boolean draw;
    private Date lastTimeTaken;
    private boolean surpassed;

    public Shield(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource
                (context.getResources(), R.drawable.shield);
        bitmap = Bitmap.createScaledBitmap(bitmap, (screenX / 16), (screenY / 10), false);
        maxX = screenX;
        maxY = screenY - bitmap.getHeight();
        minX = 0;
        Random generator = new Random();
        speed = 8;
        x = screenX;
        y = generator.nextInt(maxY);
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        lastTimeTaken = new Date();
        surpassed = false;
    }

    //Getters and Setters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public boolean isSurpassed() {
        return surpassed;
    }

    public void setSurpassed(boolean surpassed) {
        this.surpassed = surpassed;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update() {
        x -= speed;
        //respawn when off screen
        if (x < minX - bitmap.getWidth()) {
            surpassed = true;
            respawn();
        }

        // Refresh hit box location
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
        lastTimeTaken = new Date();
        draw = false;
    }

    public void respawn() {
        Random generator = new Random();
        speed = generator.nextInt(10) + 10;
        x = maxX;
        y = generator.nextInt(maxY);
        if (surpassed) {
            lastTimeTaken = new Date();
            surpassed = false;
        }
    }

    public boolean needToDraw() {
        return draw;
    }

    public boolean canDraw(Date now) {
        return ((lastTimeTaken.getTime() + Constants.TIME_BETWEEN_SHIELDS) > now.getTime());
    }

    public void draw(Canvas canvas, Paint paint) {
        if (needToDraw()) {
            canvas.drawBitmap(getBitmap(), getX(), getY(), paint);
        }
    }
}

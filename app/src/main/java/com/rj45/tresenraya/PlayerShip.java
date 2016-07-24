package com.rj45.tresenraya;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Usuario on 23/07/2016.
 */
public class PlayerShip {
    private final int MAX_SPEED = 25;
    private final int MIN_SPEED = 1;
    private final int GRAVITIY = -12;
    private int maxY;
    private int minY;
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed;
    private boolean boosting;
    private Rect hitBox;

    public PlayerShip(Context context, int maxX, int maxY) {
        x = 50;
        y = 50;
        speed = 1;
        boosting = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        this.maxY = maxY - bitmap.getHeight();
        this.minY = 0;
        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void startBoost() {
        boosting = true;
    }

    public void stopBoost() {
        boosting = false;
    }

    public void update() {
        // Are we boosting?
        if (boosting) {
            // Speed up
            speed += 2;
        } else {
            // Slow down
            speed -= 5;
        }
        // Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        // Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
        // move the ship up or down
        y -= speed + GRAVITIY;
        // But don't let ship stray off screen
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

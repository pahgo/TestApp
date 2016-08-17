package com.imonguer.monguerspace;

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
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private int x;
    private int y;
    private int speed;
    private boolean boosting;
    private Rect hitBox;
    private int shipShield = 2;
    private int frameCount;
    private int frame;

    public PlayerShip(Context context, int maxX, int maxY) {
        x = maxX / 7;
        y = maxY / 7;
        speed = 1;
        boosting = false;
        firstBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playership);
        firstBitmap = Bitmap.createScaledBitmap(firstBitmap, (maxX / 15), (maxY / 15), false);
        secondBitmap = Bitmap.createScaledBitmap(firstBitmap, (maxX / 16), (maxY / 15), false);
        this.maxY = maxY - firstBitmap.getHeight();
        this.minY = 0;
        hitBox = new Rect(x, y, firstBitmap.getWidth(), firstBitmap.getHeight());
        frameCount = 1;
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

    public int getShield() { return shipShield; }

    public void decreaseShield() {
        shipShield--;
    }

    public void increaseShield() {
        shipShield++;
    }

    public void update() {
        // Are we boosting?
        if (boosting) {
            // Speed up
            speed += 2;
        } else {
            // Slow down
            speed -= 3;
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
        hitBox.right = x + firstBitmap.getWidth();
        hitBox.bottom = y + firstBitmap.getHeight();
    }

    public void changeFrame() {
        frame = frame == 1 ? 2 : 1;
    }

    public void increaseFrameCount() {
        if (frameCount > 10) {
            changeFrame();
            frameCount = 1;
        } else {
            frameCount++;
        }
    }

    public Bitmap getBitmap() {
        return frame == 1 ? firstBitmap : secondBitmap;
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

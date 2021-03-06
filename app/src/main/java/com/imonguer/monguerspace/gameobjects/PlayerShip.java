package com.imonguer.monguerspace.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.imonguer.monguerspace.R;

/**
 * Created by Usuario on 23/07/2016.
 */
public class PlayerShip extends GameObject {
    private final int MAX_SPEED;
    private final int MIN_SPEED = 1;
    private int maxY;
    private int minY;
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private int speed;
    private boolean boosting;
    private Rect hitBox;
    private int shipShield = 2;
    private long frameCount;
    private int frame;


    public PlayerShip(Context context, int maxX, int maxY) {
        super(context);
        MAX_SPEED = (int) ((26 / 1080.0) * maxY);
        affectedByGravity = GRAVITY_ON;
        gravityValue = (int) ((-12 / 1080.0) * maxY);
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
            speed += (int) ((3 / 1080.0) * maxY);
        } else {
            // Slow down
            speed -= (int) ((3 / 1080.0) * maxY);
        }
        // Constrain top speed
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        // Never stop completely
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
        if (isAffectedByGravity()) {
            y -= speed + gravityValue;
        }
        // But don't let ship stray off screen
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
        // Refresh hitActions box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + firstBitmap.getWidth();
        hitBox.bottom = y + firstBitmap.getHeight();
    }

    public void changeFrame() {
        frame = frame == 1 ? 2 : 1;
    }

    public void increaseFrameCount() {
        if (frameCount % 10 == 0) {
            changeFrame();
            frameCount++;
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

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(getBitmap(), x, y, paint);
    }

    public long getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(long frameCount) {
        this.frameCount = frameCount;
    }
}

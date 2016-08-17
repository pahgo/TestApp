package com.imonguer.monguerspace;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Usuario on 23/07/2016.
 */
public class SpaceDust {
    private int x, y;
    private int speed;
    // Detect dust leaving the screen
    private int maxX;
    private int maxY;
    // Constructor
    public SpaceDust(int screenX, int screenY){
        maxX = screenX;
        maxY = screenY;

        // Set a speed between 0 and 9
        Random generator = new Random();
        speed = generator.nextInt(10);
        // Set the starting coordinates
        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }
    public void update(int playerSpeed){
        // Speed up when the player does
        x -= playerSpeed;
        x -= speed;
        //respawn space dust
        if(x < 0){
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(15);
        }
    }
    // Getters and Setters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawCircle(x, y, Constants.DUST_RADIUS, paint);
    }
}

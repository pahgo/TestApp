package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by Usuario on 28/07/2016.
 */
public class Planet {
    private int x, y;
    private int speed;
    private int maxX;
    private int maxY;
    private Bitmap bitmap;
    // Constructor
    public Planet(Context context, int screenX, int screenY, int var){

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.luna);

        maxX = screenX * var;
        maxY = screenY - getBitmap().getHeight() - 25;

        // Set a speed between 0 and 9
        Random generator = new Random();
        speed = 5;
        // Set the starting coordinates
        x = maxX * 2;
        y = generator.nextInt(maxY);
    }

    public void update() {
        /* Ya nos hemos pasado, no hacer nada más con él y destruirlo de memoria. */
        if (bitmap != null && (x + bitmap.getWidth()) < 0) {
            bitmap.recycle();
            bitmap = null;
        } else {
            x -= speed;
        }
    }
    // Getters and Setters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
}

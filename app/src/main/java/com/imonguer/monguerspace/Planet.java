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
    // Detect dust leaving the screen
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;
    private Bitmap bitmap;
    // Constructor
    public Planet(Context context, int screenX, int screenY, int var){

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.luna);


        maxX = screenX * var;
        maxY = screenY - getBitmap().getHeight() - 25;
        minX = 0;
        minY = 0 + getBitmap().getHeight() + 25 ;


        // Set a speed between 0 and 9
        Random generator = new Random();
        speed = 5;
        // Set the starting coordinates
        x = maxX * 2;
        y = generator.nextInt(maxY);
    }
    public void update(int playerSpeed){
        // Speed up when the player does
        x -= playerSpeed;
        x -= speed;


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

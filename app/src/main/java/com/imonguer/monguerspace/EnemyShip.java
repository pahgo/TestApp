package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

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


    public EnemyShip(Context context, int screenX, int screenY){
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource
                    (context.getResources(), R.drawable.enemy);
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

        // Refresh hit box location
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
}
package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;
import java.util.Random;

/**
 * Created by iMonguer on 28/07/2016.
 */
public class Planet {

    private int x, y;
    private int speed;
    private int maxX;
    private int maxY;
    private int screenY;
    private Bitmap bitmap;
    private Random generator = new Random();
    private Context context;
    private boolean draw;
    private Date lastTimeTaken;

    public Planet(Context context, int screenX, int screenY){
        this.context = context;
        this.screenY = screenY;
        maxX = screenX;
        speed = Constants.planetsSpeed;
        lastTimeTaken = new Date();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update() {
        if(bitmap!=null) {
            x -= speed;
            if (x < (0 - getBitmap().getWidth() - 10)) {
                stopDraw();
            }
        }
    }


    public void startDraw() {
        draw = true;
        int whichBitmap;
        whichBitmap = generator.nextInt(4);
        switch (whichBitmap){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.saturno);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.luna);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.latierra);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sol);
                break;
        }

        maxY = screenY - getBitmap().getHeight();
        x = maxX;
        y = generator.nextInt(maxY) ;
    }

    public void stopDraw() {
        lastTimeTaken = new Date();
        draw = false;
        if(bitmap != null ) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public boolean needToDraw() {
        return draw;
    }
    public boolean canDraw(Date now) {
        return ((lastTimeTaken.getTime() + Constants.TIME_BETWEEN_PLANETS) < now.getTime());
    }
}

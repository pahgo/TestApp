package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

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
        speed = Constants.PLANETS_SPEED;
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
        int whichBitmap = generator.nextInt(4);
        Planets p = Planets.getPlanets(whichBitmap);

        bitmap = BitmapFactory.decodeResource(context.getResources(), p.resource);
        bitmap = scaleBitmap(bitmap, (maxX / 6), (screenY / 5));

        maxY = screenY - bitmap.getHeight();
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

    public boolean canDraw() {
        return ((lastTimeTaken.getTime() + Constants.TIME_BETWEEN_PLANETS) < new Date().getTime());
    }

    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null && needToDraw()) {
            canvas.drawBitmap(bitmap, x, y, paint);
        }
    }

    private enum Planets {
        SATURN(0, R.drawable.saturno), MOON(1, R.drawable.luna), EARTH(2, R.drawable.latierra);
        int selector;
        int resource;

        Planets(int selector, int resource) {
            this.selector = selector;
            this.resource = resource;
        }

        public static Planets getPlanets(int selector) {
            Planets planet = SATURN;
            for (final Planets p : Planets.values()) {
                if (p.selector == selector) {
                    planet = p;
                }
            }
            return planet;
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int targetW, int targetH) {
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(bitmap.getWidth()/targetW, bitmap.getHeight()/targetH);
            scaleFactor = Math.max(scaleFactor, 1);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/scaleFactor, bitmap.getHeight()/scaleFactor, false);
        return bitmap;
    }
}

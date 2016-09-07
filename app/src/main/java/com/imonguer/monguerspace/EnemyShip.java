package com.imonguer.monguerspace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Vibrator;

import java.util.Random;

/**
 * Created by Usuario on 23/07/2016.
 *
 */
public class EnemyShip {
    private static MediaPlayer mediaExplosions = null;
    private static Vibrator vibrator = null;
    private Bitmap shownBitmap = null;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int maxY;
    private Rect hitBox;
    private boolean surpassed;
    protected Context context;
    private Random generator = new Random();


    public EnemyShip(Context context, int screenX, int screenY){
        setMediaExplosions(context);
        setVibrator(context);
        this.context = context;
        maxX = screenX;
        setSpeed(maxX / 100);
        minX = 0;
        maxY = screenY;
        setBitmap();
        maxY = screenY - shownBitmap.getHeight();
        x = screenX + generator.nextInt(500);
        y = generator.nextInt(maxY);
        hitBox = new Rect(x, y, shownBitmap.getWidth(), shownBitmap.getHeight());
        surpassed = false;
    }

    public static void hitActions() {
        mediaExplosions.start();
        vibrator.vibrate(Constants.VIBRATION_TIME);
    }

    private void setSpeed(int n) {
        if (n < 10) {
            speed = generator.nextInt(n - 6) + 10;
        } else {
            speed = generator.nextInt(n - 10) + 10;
        }
    }

    private void setVibrator(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    private void setMediaExplosions(Context context) {
        if (mediaExplosions == null) {
            mediaExplosions = MediaPlayer.create(context, R.raw.explosion2);
            mediaExplosions.setLooping(false);
            mediaExplosions.setVolume(1, 1);
        }
    }

    private void setBitmap() {
        int whichBitmap = generator.nextInt(Ships.values().length);
        Ships p = Ships.getShips(whichBitmap);
        shownBitmap = p.getBitmap(context, maxX, maxY);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed){

        // Move to the left
        x -= playerSpeed;
        x -= speed;
        //respawn when off screen
        if (x < minX - shownBitmap.getWidth()) {
            setSpeed(maxX / 100);
            x = maxX;
            y = generator.nextInt(maxY);
            surpassed = true;
            setBitmap();
        }

        hitBox = new Rect(x, y, x + shownBitmap.getWidth(), y + shownBitmap.getHeight());
    }

    public boolean isSurpassed() {
        return surpassed;
    }

    public void setSurpassed(boolean surpassed) {
        this.surpassed = surpassed;
    }

    public Rect getHitBox() { return hitBox; }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(shownBitmap, x, y, paint);
    }

    public int getX() {
        return x;
    }

    private enum Ships {
        A(0, R.drawable.asteroid), B(1, R.drawable.asteroid2), C(2, R.drawable.asteroid4);

        int selector;
        int resource;
        Bitmap normal;
        Bitmap horizontal;
        Bitmap vertical;
        Bitmap horizontalAndVertical;

        Ships(int selector, int resource) {
            this.selector = selector;
            this.resource = resource;
        }

        public static Ships getShips(int selector) {
            Ships ship = A;
            for (final Ships s : Ships.values()) {
                if (s.selector == selector) {
                    ship = s;
                }
            }
            return ship;
        }

        public Bitmap getBitmap(Context context, int maxX, int maxY) {
            Random generator = new Random();
            int which = generator.nextInt(4);

            switch (which) {
                // Normal
                case 0:
                    if (normal == null) {
                        normal = BitmapFactory.decodeResource(context.getResources(), resource);
                        normal = scaleBitmap(normal, maxX / 20, maxY / 20);
                    }
                    return normal;
                case 1:
                    if (horizontal == null) {
                        horizontal = BitmapFactory.decodeResource(context.getResources(), resource);
                        horizontal = scaleBitmap(horizontal, maxX / 20, maxY / 20);
                        horizontal = flip(horizontal, Direction.HORIZONTAL);
                    }
                    return horizontal;
                case 2:
                    if (vertical == null) {
                        vertical = BitmapFactory.decodeResource(context.getResources(), resource);
                        vertical = scaleBitmap(vertical, maxX / 20, maxY / 20);
                        vertical = flip(vertical, Direction.VERTICAL);
                    }
                    return vertical;
                case 3:
                    if (horizontalAndVertical == null) {
                        horizontalAndVertical = BitmapFactory.decodeResource(context.getResources(), resource);
                        horizontalAndVertical = scaleBitmap(horizontalAndVertical, maxX / 20, maxY / 20);
                        horizontalAndVertical = flip(horizontalAndVertical, Direction.VERTICAL);
                        horizontalAndVertical = flip(horizontalAndVertical, Direction.HORIZONTAL);
                    }
                    return horizontalAndVertical;
            }
            // Never!
            return null;
        }

        private Bitmap scaleBitmap(Bitmap bitmap, int targetW, int targetH) {
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(bitmap.getWidth() / targetW, bitmap.getHeight() / targetH);
                scaleFactor = Math.max(scaleFactor, 1);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scaleFactor, bitmap.getHeight() / scaleFactor, false);
            return bitmap;
        }
    }


    public enum Direction {VERTICAL, HORIZONTAL}

    /**
     * Creates a new bitmap by flipping the specified bitmap
     * vertically or horizontally.
     *
     * @param src  Bitmap to flip
     * @param type Flip direction (horizontal or vertical)
     * @return New bitmap created by flipping the given one
     * vertically or horizontally as specified by
     * the <code>type</code> parameter or
     * the original bitmap if an unknown type
     * is specified.
     **/
    public static Bitmap flip(Bitmap src, Direction type) {
        Matrix matrix = new Matrix();

        if (type == Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == Direction.HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
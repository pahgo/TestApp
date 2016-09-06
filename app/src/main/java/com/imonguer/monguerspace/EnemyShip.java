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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Usuario on 23/07/2016.
 *
 */
public class EnemyShip {
    private static MediaPlayer mediaExplosions = null;
    private static Vibrator vibrator = null;
    private static Map<Integer, Bitmap> resources = new HashMap<>();
    private Bitmap shownBitmap = null;
    private int x, y;
    private int speed = 1;
    // Detect enemies leaving the screen
    private int maxX;
    private int minX;
    // Spawn enemies within screen bounds
    private int maxY;
    private Rect hitBox;
    private boolean surpassed;
    private Context context;
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

    private void setSpeed(int n) {
        speed = generator.nextInt(n - 10) + 10;
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
        if (resources.get(p.resource) == null) {
            shownBitmap = BitmapFactory.decodeResource(context.getResources(), p.resource);
            resources.put(p.resource, shownBitmap);
        } else {
            shownBitmap = resources.get(p.resource);
        }
        shownBitmap = scaleBitmap(shownBitmap, maxX / 20, maxY / 20);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed){
        // Move to the left
        x -= playerSpeed + speed;
        //respawn when off screen
        if (x - shownBitmap.getWidth() < 0) {
            setSpeed(maxX / 100);
            x = maxX;
            y = generator.nextInt(maxY);
            surpassed = true;
            setBitmap();
        }

        hitBox.set(x, y, x + shownBitmap.getWidth(), y + shownBitmap.getHeight());
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

    private Bitmap scaleBitmap(Bitmap bitmap, int targetW, int targetH) {
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(bitmap.getWidth() / targetW, bitmap.getHeight() / targetH);
            scaleFactor = Math.max(scaleFactor, 1);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scaleFactor, bitmap.getHeight() / scaleFactor, false);
        switch (generator.nextInt(4)) {
            case 0:
                bitmap = flip(bitmap, Direction.HORIZONTAL);
                break;
            case 1:
                bitmap = flip(bitmap, Direction.VERTICAL);
                break;
            case 2:
                bitmap = flip(bitmap, Direction.VERTICAL);
                bitmap = flip(bitmap, Direction.HORIZONTAL);
                break;
            default:
                break;
        }
        return bitmap;
    }

    private enum Ships {
        A(0, R.drawable.asteroid), B(1, R.drawable.asteroid2), C(2, R.drawable.asteroid4);

        int selector;
        int resource;

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
    }

    public enum Direction {VERTICAL, HORIZONTAL}
}
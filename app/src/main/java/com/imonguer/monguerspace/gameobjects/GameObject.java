package com.imonguer.monguerspace.gameobjects;

import android.content.Context;

import java.util.Random;

/**
 * Created by Usuario on 07/09/2016.
 */
public abstract class GameObject {
    public static final boolean GRAVITY_ON = true;
    public static final boolean GRAVITY_OFF = false;
    protected boolean affectedByGravity;
    protected int gravityValue;
    protected Context context;
    protected int x;
    protected int y;
    private static Random generator = new Random();

    public GameObject(final Context context) {
        x = 0;
        y = 0;
        this.context = context;
        affectedByGravity = GRAVITY_OFF;
        gravityValue = 0;
    }

    public static int random(final int n) {
        return generator.nextInt(n);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    /**
     * Hace que el objeto se vea afectado por la gravedad.
     */
    public void gravityOn() {
        affectedByGravity = GRAVITY_ON;
    }

    /**
     * Hace que el objeto no se vea afectado por la gravedad.
     */
    public void gravityOff() {
        affectedByGravity = GRAVITY_OFF;
    }
}

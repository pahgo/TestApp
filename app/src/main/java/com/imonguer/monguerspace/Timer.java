package com.imonguer.monguerspace;

import java.util.Date;

public class Timer {
    private long initial;
    private boolean paused;
    private long target;

    public Timer(final long target) {
        paused = false;
        this.target = target;
        refresh();
    }

    public void pause() {
        paused = true;
        long milisElapsed = new Date().getTime() - initial;
        target -= milisElapsed;
    }

    public void resume() {
        paused = false;
        refresh();
    }

    public void refresh() {
        initial = System.currentTimeMillis();
    }

    public boolean isExpired() {
        if (paused) {
            resume();
        }
        final long now = System.currentTimeMillis();
        return now >= initial + target;
    }
}

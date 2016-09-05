package com.imonguer.monguerspace;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;

public class GameActivity extends Activity {

    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new TDView(this, size.x, size.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onBackPressed() {
        if (gameView.getTimesTouch() < 1) {
            gameView.setUIPause();
            gameView.draw();
            onPause();
            gameView.setTimesTouch(1);
        } else if (gameView.getTimesTouch() == 1) {
            super.onBackPressed();
            gameView.setTimesTouch(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gameView.setTimesTouch(0);
        return gameView.onTouchEvent(event);
    }

}
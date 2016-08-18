package com.imonguer.monguerspace;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends /* BaseGameActivity */ Activity implements View.OnClickListener {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button play = (Button) findViewById(R.id.buttonPlay);
        final Button credits = (Button) findViewById(R.id.creditos);

        play.setOnClickListener(this);
        credits.setOnClickListener(this);

        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        writeHighScore((TextView) findViewById(R.id.textHighScore));
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.buttonPlay:
                i = new Intent(this, GameActivity.class);
                break;
            case R.id.creditos:
                i = new Intent(getApplicationContext(), CreditsActivity.class);
                break;
            default:
                Log.d("MainActivity", "onClick: default case");
        }
        if (i != null){
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        writeHighScore((TextView) findViewById(R.id.textHighScore));
    }

    private void writeHighScore(final TextView view) {
        view.setText(loadHighScore());
    }

    private String loadHighScore() {
        long fastestTime = prefs.getLong("highestPoints", 0);
        String highText = getResources().getString(R.string.highscore);
        return highText + Constants.DOTS + fastestTime;
    }
}

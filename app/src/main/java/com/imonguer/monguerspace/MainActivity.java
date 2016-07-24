package com.imonguer.monguerspace;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the button in our layout
        final Button buttonPlay =
                (Button) findViewById(R.id.buttonPlay);
        // Listen for clicks
        buttonPlay.setOnClickListener(this);

        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        // Get a reference to the button in our layout

        final TextView textFastestTime =
                (TextView) findViewById(R.id.textHighScore);

        long fastestTime = prefs.getLong("highestPoints", 0);
        // Put the high score in our TextView
        textFastestTime.setText("Puntuación máxima" + fastestTime);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        final TextView textFastestTime =
                (TextView) findViewById(R.id.textHighScore);

        long fastestTime = prefs.getLong("highestPoints", 0);
        // Put the high score in our TextView
        textFastestTime.setText("Puntuación máxima: " + fastestTime);
    }
}
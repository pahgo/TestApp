package com.imonguer.monguerspace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.example.games.basegameutils.BaseGameActivity;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonPlay =
                (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);

        final Button creditsButton =
                (Button) findViewById(R.id.creditos);
        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreditsActivity.class);
                startActivity(i);
            }
        });

        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        // Get a reference to the button in our layout

        final TextView textFastestTime =
                (TextView) findViewById(R.id.textHighScore);

        long fastestTime = prefs.getLong("highestPoints", 0);
        // Put the high score in our TextView
        String highText = getResources().getString(R.string.highscore);
        textFastestTime.setText(highText + fastestTime);
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
        String highText = getResources().getString(R.string.highscore);
        textFastestTime.setText(highText + fastestTime);
    }

    @Override
    public void onSignInFailed() {
        System.out.println("ERROR");
    }

    @Override
    public void onSignInSucceeded() {
        System.out.println("BIEEEEEEN");
    }
}

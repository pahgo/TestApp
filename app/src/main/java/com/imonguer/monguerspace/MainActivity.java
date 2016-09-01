package com.imonguer.monguerspace;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends /* BaseGameActivity */ Activity implements View.OnClickListener {

    private SharedPreferences prefs;
    private Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        face = Typeface.createFromAsset(getAssets(), "fonts/android_7.ttf");

        changeTypeFace(R.id.buttonPlay);
        changeTypeFace(R.id.creditos);
        changeTypeFace(R.id.company);
        changeTypeFace(R.id.textGame);
        changeTypeFace(R.id.instructions);

        final Button play = (Button) findViewById(R.id.buttonPlay);
        final Button credits = (Button) findViewById(R.id.creditos);
        final Button instructions = (Button) findViewById(R.id.instructions);

        play.setTypeface(face);
        credits.setTypeface(face);

        play.setOnClickListener(this);
        credits.setOnClickListener(this);
        instructions.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        writeHighScore((TextView) findViewById(R.id.textHighScore));
        if (getIntent().getExtras() != null && getIntent().getExtras().getInt("POINTS") != 0) {
            AdRequest request = null;
            if (Constants.DEBUG_ENABLED) {
                request = new AdRequest.Builder()
                        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("819F9C3E1A68FE85E1F7134B04075AF1")
                        .build();
            } else {
                request = new AdRequest.Builder().build();
            }
            mAdView.loadAd(request);
            TextView tv = (TextView) findViewById(R.id.lastPoints);
            tv.setTypeface(face);
            tv.setText(getResources().getString(R.string.last_points) + " "+ getIntent().getExtras().getInt("POINTS"));
            findViewById(R.id.lastPoints).setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.lastPoints).setVisibility(View.INVISIBLE);
            mAdView.setVisibility(View.INVISIBLE);
        }
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
            case R.id.instructions:
                i = new Intent(getApplicationContext(), InstructionActivity.class);
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
        view.setTypeface(face);
        view.setText(loadHighScore());
    }

    private String loadHighScore() {
        long fastestTime = prefs.getLong("highestPoints", 0);
        String highText = getResources().getString(R.string.highscore);
        return highText + Constants.DOTS + fastestTime;
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void changeTypeFace(final int id) {
        Object o = findViewById(id);

        if (o instanceof Button) {
            final Button button = (Button) o;
            button.setTypeface(face);
        } else if (o instanceof TextView) {
            final TextView textview = (TextView) o;
            textview.setTypeface(face);
        }
    }
}

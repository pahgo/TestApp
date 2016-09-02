package com.imonguer.monguerspace;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

public class MainActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    private static int RC_SIGN_IN = 9001;
    private SharedPreferences prefs;
    private Typeface face;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton signIn;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

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
        signIn = (ImageButton) findViewById(R.id.gApiSingIn);


        play.setTypeface(face);
        credits.setTypeface(face);

        play.setOnClickListener(this);
        credits.setOnClickListener(this);
        instructions.setOnClickListener(this);
        signIn.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        writeHighScore((TextView) findViewById(R.id.textHighScore));
        if (getIntent().getExtras() != null && getIntent().getExtras().getInt("POINTS") != 0) {
            AdRequest request;
            if (Constants.DEBUG_ENABLED) {
                request = new AdRequest.Builder()
                        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("819F9C3E1A68FE85E1F7134B04075AF1")
                        .addTestDevice("0711074D087F102FE82A725C4FEEC265")
                        .build();
                try {//// FIXME: 02/09/2016 no es necesario el try-catch
                    Log.d(TAG, "onCreate: submitting");
                    Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard), 1337);
                    Log.d(TAG, "onCreate: submited");
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                break;
            case R.id.gApiSingIn:
                signIn();
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

    /*INI google api*/

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

    private void signIn() {
        Log.d(TAG, "signIn: vamos a crear el google api");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: conectado?" + bundle.toString());

        signIn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getResources().getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        signIn.setVisibility(View.VISIBLE);
        Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + intent.toString());
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.unknown_error);
            }
        }
    }

}

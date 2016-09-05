package com.imonguer.monguerspace;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;


public class MainActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    private static int RC_SIGN_IN = 9001;
    private static SignInButton signIn;
    private SharedPreferences prefs;
    private Typeface face;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private GoogleApiClient mGoogleApiClient;
    private Button leaderboards;
    private Button achievements;

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
        leaderboards = (Button) findViewById(R.id.leaderboards);
        achievements = (Button) findViewById(R.id.achievements);
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        signIn = (SignInButton) findViewById(R.id.sign_in_button);

        play.setTypeface(face);
        credits.setTypeface(face);
        leaderboards.setTypeface(face);
        achievements.setTypeface(face);

        play.setOnClickListener(this);
        credits.setOnClickListener(this);
        instructions.setOnClickListener(this);
        signIn.setOnClickListener(this);
        leaderboards.setOnClickListener(this);
        achievements.setOnClickListener(this);

        if (MyGoogleApi.getInstance(mGoogleApiClient) != null && MyGoogleApi.getInstance(mGoogleApiClient).getClient().isConnected()) {
            signIn.setVisibility(View.INVISIBLE);
        }

        prefs = getSharedPreferences("HiScores", MODE_PRIVATE);
        writeHighScore((TextView) findViewById(R.id.textHighScore));
        final int points = getIntent().getExtras() == null ? 0 : getIntent().getExtras().getInt("POINTS");
        if (getIntent().getExtras() != null && points != 0) {
            AdRequest request;
            if (!Constants.ALPHA_MODE) {
                if (Constants.DEBUG_ENABLED) {
                    request = new AdRequest.Builder()
                            //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice("819F9C3E1A68FE85E1F7134B04075AF1")
                            .addTestDevice("0711074D087F102FE82A725C4FEEC265")
                            .build();
                } else {
                    request = new AdRequest.Builder().build();
                }

                mAdView.loadAd(request);
                TextView tv = (TextView) findViewById(R.id.lastPoints);
                tv.setTypeface(face);
                tv.setText(getResources().getString(R.string.last_points) + " " + points);
                findViewById(R.id.lastPoints).setVisibility(View.VISIBLE);
                mAdView.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!Constants.ALPHA_MODE) {
                findViewById(R.id.lastPoints).setVisibility(View.INVISIBLE);
                mAdView.setVisibility(View.INVISIBLE);
            }
        }
        Log.i(TAG, "onCreate points: %i" + points);
        if (MyGoogleApi.getInstance(mGoogleApiClient) != null &&
                MyGoogleApi.getInstance(mGoogleApiClient).getClient() != null && MyGoogleApi.getInstance(mGoogleApiClient).getClient().isConnected()) {
            Log.i(TAG, "onCreate: Submitting score");
            if (points != 0)
                Games.Leaderboards.submitScore(MyGoogleApi.getInstance(mGoogleApiClient).getClient(), getResources().getString(R.string.leaderboard_best_scores), points);
            leaderboards.setVisibility(View.VISIBLE);
            achievements.setVisibility(View.VISIBLE);

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
            case R.id.sign_in_button:
                if (MyGoogleApi.getInstance(mGoogleApiClient) == null ||
                        MyGoogleApi.getInstance(mGoogleApiClient).getClient() == null ||
                        !MyGoogleApi.getInstance(mGoogleApiClient).getClient().isConnected()) {
                    signIn();
                } else if (MyGoogleApi.getInstance(mGoogleApiClient).getClient() != null) {
                    MyGoogleApi.getInstance(mGoogleApiClient).getClient().disconnect();
                }
                break;
            case R.id.leaderboards:
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(MyGoogleApi.getInstance(mGoogleApiClient).getClient(),
                        getResources().getString(R.string.leaderboard_best_scores)), 666);
                break;
            case R.id.achievements:
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                        777);
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
    /*INI google api*/

    private void signIn() {
        Log.d(TAG, "signIn: vamos a crear el google api");
        mSignInClicked = true;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        MyGoogleApi.getInstance(mGoogleApiClient).getClient().connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: conectado?" + (bundle == null ? "null" : bundle.toString()));
        Log.d(TAG, "onConnected: submitting");
        final Long points = prefs.getLong("highestPoints", 0);
        Games.Leaderboards.submitScore(MyGoogleApi.getInstance(mGoogleApiClient).getClient(), getResources().getString(R.string.leaderboard_best_scores), points);
        if (MyGoogleApi.getInstance(null) != null && MyGoogleApi.getInstance(null).getClient() != null && MyGoogleApi.getInstance(null).getClient().isConnected()) {
            Log.i(TAG, "onConnected: submitting achievements: " + points);
            if (points > 9000) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000));
            }
            if (points > 90000) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_ii));
            }
            if (points > 900000) {
                MyGoogleApi.getInstance(null).setAchievement(getResources().getString(R.string.achievement_its_over_nine_9000_iii));
            }
        }

        Log.d(TAG, "onConnected: submited");
//        signIn.setText(R.string.signout);
        signIn.setVisibility(View.INVISIBLE);
        leaderboards.setVisibility(View.VISIBLE);
        achievements.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                    MyGoogleApi.getInstance(mGoogleApiClient).getClient(), connectionResult,
                    RC_SIGN_IN, getResources().getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
//        signIn.setText(R.string.common_signin_button_text);
        Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        MyGoogleApi.getInstance(mGoogleApiClient).getClient().connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                MyGoogleApi.getInstance(mGoogleApiClient).getClient().connect();
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

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        9000).show();
            }
            return false;
        }
        return true;
    }

}

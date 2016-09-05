package com.imonguer.monguerspace;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francisco on 05/09/2016.
 */
public class MyGoogleApi {
    private static MyGoogleApi ourInstance;
    private GoogleApiClient mGoogleApiClient;
    private List<String> achievements = new ArrayList<>();

    private MyGoogleApi(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public static MyGoogleApi getInstance(GoogleApiClient mGoogleApiClient) {
        if (mGoogleApiClient != null) {
            ourInstance = new MyGoogleApi(mGoogleApiClient);
        }
        return ourInstance;
    }

    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }

    public boolean setAchievement(String achievement) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (!achievements.contains(achievement)) {
                Games.Achievements.unlock(mGoogleApiClient, achievement);
                achievements.add(achievement);
                return true;
            }
        }
        return false;
    }

    public void revealAchievement(String achievement) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Achievements.reveal(mGoogleApiClient, achievement);
        }
    }
}

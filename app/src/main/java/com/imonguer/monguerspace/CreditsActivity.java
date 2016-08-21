package com.imonguer.monguerspace;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        /* Dispositivo de prueba. */
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("0711074D087F102FE82A725C4FEEC265")  // An example device ID
                .build();
        mAdView.loadAd(request);
    }

}

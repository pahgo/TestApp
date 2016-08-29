package com.imonguer.monguerspace;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CreditsActivity extends Activity {

    private Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        face = Typeface.createFromAsset(getAssets(), "fonts/android_7.ttf");

        changeTypeFace(R.id.companyCredits);
        changeTypeFace(R.id.textGameCredits);
        changeTypeFace(R.id.programming);
        changeTypeFace(R.id.monguers);
        changeTypeFace(R.id.music);
        changeTypeFace(R.id.compositor);
        changeTypeFace(R.id.thanks);
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

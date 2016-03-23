package edu.engagement.application;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;

/**
 * Created by alex on 7/27/15.
 */
public class App extends Application {
    public static final String NAME = "MindMapper";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(NAME, "INITIALIZING FLURRY (" + FlurryAgent.getReleaseVersion() + ")");

        // configure Flurry
        FlurryAgent.setLogEnabled(true);

        // init Flurry
        FlurryAgent.init(this, "B8PDQPHRM3TW74QS2ZHS");
    }
}

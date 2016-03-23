package edu.engagement.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by alex on 7/27/15.
 */
@ReportsCrashes(
        formUri = "https://collector.tracepot.com/3e0ff4c0"
)
public class App extends Application {
    public static final String NAME = "MindMapper";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.d(NAME, "Starting ACRA");

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}

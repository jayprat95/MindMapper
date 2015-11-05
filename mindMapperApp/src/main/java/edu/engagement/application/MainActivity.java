package edu.engagement.application;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import edu.engagement.application.Eeg.EegListener;
import edu.engagement.application.Eeg.EegState;
import edu.engagement.application.Fragments.MapFrag;
import edu.engagement.application.Fragments.RealTimeDataFragment;
import edu.engagement.application.Fragments.StatusDialogFragment;
import edu.engagement.application.SlidingTab.SlidingTabLayout;
import edu.engagement.application.SlidingTab.ViewPagerAdapter;

/**
 * MainActivity June 16
 */
public class MainActivity extends FragmentActivity implements EegListener, RealTimeDataFragment.RealTimeListener, StatusDialogFragment.ConnectionLostDialogListener, DialogInterface.OnDismissListener {

    //Onboarding Screen
    private ViewPager onboardPager;
    private List<View> onboardList;
    private View onboardPager1;
    private View onboardPager2;
    private View onboardPagerStart;

    public static final String BASELINE_AVG_KEY = "avg";
    public final String MAP_TAG = "MAP_FRAGMENT";
    public final String REAL_TIME_TAG = "REAL_TIME_FRAGMENT";
    public final String REFLECTION_GRAPH_TAG = "REFLECTION_GRAPH_FRAGMENT";
    public final String SUMMARY_TAG = "SUMMARY_FRAGMENT";
    /* Having issues with android
     * Packaging all functions here for now so I can change easy if a mistake is found
     */
    public final String BASELINE_TAG = "BASELINE_FRAGMENT";
    /**
     * Sliding Tabs variables
     */
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    //private SlidingDrawer drawer;
    private CharSequence Titles[] = { "Activities" , "Map"};
    private int Numboftabs = 2;

    private Location mLocation = null;

    // Define a listener that responds to locationName updates
//    LocationListener locationListener = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            // Called when a new locationName is found by the network locationName provider.
//
//            Toast.makeText(getApplicationContext(), "The Location is initialized", Toast.LENGTH_SHORT);
//
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            System.out.println("Status Changed");
//        }
//
//        public void onProviderEnabled(String provider) {
//        }
//
//        public void onProviderDisabled(String provider) {
//        }
//    };

    LocationManager locationManager;
    // location from place picker
    private String location = "";
    /**
     * Fab Button variables
     */
    private FloatingActionButton fab;
    private boolean fabClicked;

    /**
     * Container for the annotations view
     */
    private FrameLayout frameLayout;
    private int gpsKey = 0;
    private CharSequence mTitle;
    private MapFrag mapFragment;
    private RealTimeDataFragment realFragment;

    private boolean realTimeInstantiated;

    private MindwaveService mindwaveService;

    // Method to start the service
    public void startService() {
        Log.d(App.NAME, "Starting MindwaveService!!");

        if (mindwaveService == null) {
            startService(new Intent(getBaseContext(), MindwaveService.class));
            bindService(new Intent(getBaseContext(), MindwaveService.class), mindwaveConnection, 0);
        }
    }

    // Method to stop the service
    public void stopService() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int sessionId = prefs.getInt("sessionId", RealTimeDataFragment.sessionId);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("sessionId", ++sessionId);
        Log.v("The sessionId", "The end session id: " + sessionId);
        edit.commit();
        stopService(new Intent(getBaseContext(), MindwaveService.class));
    }

    /**
     * Get the current location from place picker
     *
     * @return locatioin name
     */
    public String getLocationName() {
        return this.location;
    }

    public Location getLocation(){
        return mLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean boardingStart = prefs.getBoolean("first", true);

        if(boardingStart) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("first", false);
            edit.commit();
            Intent intent = new Intent(this, OnboardActivity.class);
            startActivity(intent);
        }
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        realTimeInstantiated = false;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        /** End of Sliding Tabs Shit */

        /* Fab Button Shit */
        fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fabClicked = false;
        final Activity thisActivity = this; // Use this for the toast
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        //requestLocationUpdates(LocationManager.GPS_PROVIDER, 600,
                        //100, locationListener);
                Log.d(App.NAME, "Showing Place Picker");
                showPlacePicker();
            }
        });
        /* End of Fab Button Shit */

        System.out.println("Opened data source (DB)");
    }

    /**
     * Changes the apps's gobal ApplicationState, and all
     * relevant GUI will change to reflect on the ApplicationState*
     *
     * @param someState a new ApplicationState
     */
    public void changeState(ApplicationState someState) {

        if (someState == ApplicationState.RECORDING) {
            pager.setVisibility(View.INVISIBLE);
            tabs.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);

            switchToFragment("REAL_TIME_FRAGMENT");

        } else {    // Changed from realtime to sliding tab
            pager.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
        }
    }

    /*
     * Callback method that is called after user selects a location.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                location = PlacePicker.getPlace(data, this).getName().toString();

                if(mLocation == null){
                    mLocation = new Location("");
                }
                mLocation.setLatitude(PlacePicker.getPlace(data, this).getLatLng().latitude);
                mLocation.setLongitude(PlacePicker.getPlace(data, this).getLatLng().longitude);
                Log.d("location manager", "get current location");


                PlacePicker.getLatLngBounds(data);

                changeState(ApplicationState.RECORDING);

            } else {
                Log.d(App.NAME, "PlacePicker cancelled!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only do this if it is the reflection view
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle ApplicationState after onRestoreInstanceState has occurred.
        //drawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //drawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        if (drawer.getDrawerToggle().onOptionsItemSelected(item)) {
//            return true;
//        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void switchToFragment(String FRAGMENT_TAG) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        if (getFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
            if (FRAGMENT_TAG.equals(REAL_TIME_TAG)) {
                if (realFragment == null)
                    realFragment = new RealTimeDataFragment();
                fragmentTransaction.replace((R.id.content_frame), realFragment,
                        REAL_TIME_TAG).commit();
            }
        }
    }

    /**
     * Show place picker, and return the location name as a string
     * @return the name of the location selected
     */
    public void showPlacePicker() {
        /* Place Picker Experiment */
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(builder.build(this),
                    PLACE_PICKER_REQUEST);
            // PlacePicker.getPlace(builder, context);

        } catch (GooglePlayServicesRepairableException e1) {
            e1.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Change the sliding tab to focus on the desired tab
     *
     * @param pageNum
     */
    public void pagerChange(int pageNum) {
        pager.setCurrentItem(pageNum);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mindwaveConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MindwaveService.MindwaveBinder binder = (MindwaveService.MindwaveBinder) service;
            mindwaveService = binder.getService();

            mindwaveService.addEegListener(MainActivity.this);

            Log.d(App.NAME, "MainActivity connected to MindwaveService");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mindwaveService = null;
            Log.d(App.NAME, "MainActivity disconnected from MindwaveService");
        }
    };

    @Override
    public void onEegStateChange(EegState state) {
        Log.d(App.NAME, "MainActivity received eeg state change: " + state.name());

        if (state == EegState.DISCONNECTED) {
            realFragment.onEegDisconnect();
        } else if (state == EegState.CONNECTED) {
            realFragment.onEegConnect();
        } else if (state == EegState.NOT_FOUND) {
            realFragment.onEegNotFound();
        }
    }

    @Override
    public void onEegAttentionReceived(int attention) {
        Log.d(App.NAME, "MainActivity received attention data: " + attention);
        realFragment.setAttention(attention);
    }

    @Override
    public void onRecordingStarted() {
        if (mindwaveService != null) {
            mindwaveService.startRecording();
        }
    }

    @Override
    public void onRecordingStopped() {
        if (mindwaveService != null) {
            mindwaveService.stopRecording();
        }
    }

    @Override
    public void onClickReconnect() {
        Log.d(App.NAME, "Trying to reconnect...");
        startService();
    }

    @Override
    public void onClickResume() {
        Log.d(App.NAME, "Resuming session...");
    }

    @Override
    public void onClickEndSession() {
        Log.d(App.NAME, "Ending session...");

        stopService();

        // Move back to the graph view
        changeState(MainActivity.ApplicationState.REFLECTION);
        pagerChange(1);
    }

    @Override
    public void onTimeout() {
        Log.d(App.NAME, "Dialog timed out...");
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        realFragment.startRecording();
    }

    public enum ApplicationState {
        PRE_RECORDING, RECORDING, REFLECTION
    }
}

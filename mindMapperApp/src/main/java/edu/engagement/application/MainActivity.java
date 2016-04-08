package edu.engagement.application;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.Eeg.EegListener;
import edu.engagement.application.Eeg.EegState;
import edu.engagement.application.Fragments.EndRecordingDialogFragment;
import edu.engagement.application.Fragments.RecordingFragment;
import edu.engagement.application.Fragments.ReflectionFragment;
import edu.engagement.application.Fragments.StatusDialogFragment;
import edu.engagement.application.Fragments.UnfinishedRecordingDialogFragment;

/**
 * MainActivity June 16
 */
public class MainActivity extends FragmentActivity implements EndRecordingDialogFragment.OnFragmentInterfaceListener, UnfinishedRecordingDialogFragment.OnFragmentInterfaceListener,EegListener, RecordingFragment.RealTimeListener, StatusDialogFragment.ConnectionLostDialogListener {

    public static final String RECORDING_TAG = "REAL_TIME_FRAGMENT";
    public static final String REFLECTION_TAG = "REFLECTION_FRAGMENT";

    private static final int STARTUP_ACTIVITY_REQUEST = 0;
    private static final int PLACE_PICKER_ACTIVITY_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST = 2;

    private MindwaveService mindwaveService;
    private String activityName = "";

    private GoogleApiClient apiClient;

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

        // Didn't finish recording, so ask to continue or delete the recording
        if (!prefs.getBoolean("finishedRecording", true)) {
            Log.d(App.NAME, "Unfinished recording detected!!");
            Log.v("The sessionId", "The end session id: " + prefs.getInt("sessionId",1));
            UnfinishedRecordingDialogFragment dialogFragment = new UnfinishedRecordingDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "unfinishedRecordingDialog");
        }else{
            showFragment(REFLECTION_TAG, null);
        }

        apiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).build();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                } else {
                    //force require location
                    requestLocationPermission();
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocationPermission();
    }

    public void requestLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        apiClient.connect();
        Log.d(App.NAME, "Connected to Google API");
    }

    @Override
    protected void onStop() {
        super.onStop();

        apiClient.disconnect();
        Log.d(App.NAME, "Disconnected from Google API");
    }

    public void showFragment(String tag, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // The fragment is not being shown right now
        if (fragmentManager.findFragmentByTag(tag) == null) {
            Fragment f;
            switch (tag) {
                case RECORDING_TAG:
                    System.out.println("------new recording------");
                    f = new RecordingFragment();
                    break;
                case REFLECTION_TAG:
                    System.out.println("------new reflection------");
                    f = new ReflectionFragment();
                    break;
                default:
                    Log.e("MindMapper", "Unrecognized Fragment", new IllegalArgumentException());
                    return;
            }
            f.setArguments(bundle);
            transaction.replace(R.id.content_frame, f, tag).commit();
        }
    }

    /*
     * Callback method that is called after user selects a location.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //return result from recording startup
        if(requestCode == STARTUP_ACTIVITY_REQUEST){
            if(resultCode == RESULT_OK){
                activityName = data.getStringExtra(RecordingStartup.ACTIVITY_DESCRIPTION);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("activityName", activityName);
                edit.commit();
                showPlacePicker();
            }
        }
        //return result from place picker
        if (requestCode == PLACE_PICKER_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                LatLng location = place.getLatLng();

                String locationName = place.getName().toString();

                if(locationName.length() > 16){
                    locationName = locationName.substring(0,17);
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                int sessionId = prefs.getInt("sessionId", 1);
//
//                Log.d(App.NAME, "Trying to save image for sessionId (" + sessionId + ")");
                new SessionPhotoSaveTask(locationName, 400, 200).execute(place.getId());

                // Start recording fragment
                Bundle bundle = new Bundle();
                Log.v("activity name: ", activityName);
                bundle.putString(RecordingFragment.ACTIVITY_NAME_KEY, activityName);
                bundle.putString(RecordingFragment.LOCATION_NAME_KEY, locationName);
                bundle.putDouble(RecordingFragment.LOCATION_LAT_KEY, location.latitude);
                bundle.putDouble(RecordingFragment.LOCATION_LONG_KEY,location.longitude);

                showFragment(RECORDING_TAG, bundle);
            } else {
                Log.d(App.NAME, "PlacePicker cancelled!");
            }
        }
    }

    public void showStartupActivity(){
        /* Place Picker Experiment */
        Intent intent = new Intent(this, RecordingStartup.class);
            // identified by a request code.
        startActivityForResult(intent, STARTUP_ACTIVITY_REQUEST);
    }

    /**
     * Show place picker, and return the location name as a string
     * @return the name of the location selected
     */
    public void showPlacePicker() {
        /* Place Picker Experiment */
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(builder.build(this),
                    PLACE_PICKER_ACTIVITY_REQUEST);
            // PlacePicker.getPlace(builder, context);

        } catch (GooglePlayServicesRepairableException e1) {
            e1.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e1) {
            e1.printStackTrace();
        }
    }

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

        Log.d(App.NAME, "Stopping MindwaveService!!");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int sessionId = prefs.getInt("sessionId", 1);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("finishedRecording", true);
        edit.putInt("sessionId", ++sessionId);
        Log.v("The sessionId", "The end session id: " + sessionId);
        edit.commit();

        stopService(new Intent(getBaseContext(), MindwaveService.class));
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

        RecordingFragment rFrag = (RecordingFragment)getSupportFragmentManager().findFragmentByTag(RECORDING_TAG);

        if (rFrag != null) {
            if (state == EegState.DISCONNECTED) {
                rFrag.onEegDisconnect();
            } else if (state == EegState.CONNECTED) {
                rFrag.onEegConnect();
            } else if (state == EegState.NOT_FOUND) {
                rFrag.onEegNotFound();
            }
        }
    }

    @Override
    public void onEegAttentionReceived(int attention) {
        Log.d(App.NAME, "MainActivity received attention data: " + attention);

        RecordingFragment rFrag = (RecordingFragment)getSupportFragmentManager().findFragmentByTag(RECORDING_TAG);

        rFrag.setAttention(attention);
    }

    @Override
    public void onRecordingStarted() {
        if (mindwaveService != null) {
            Log.v("Recording", "Recording started from main");
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
        Log.d(App.NAME, "Resuming session from disconnect dialog...");
        RecordingFragment rFrag = (RecordingFragment)getSupportFragmentManager().findFragmentByTag(RECORDING_TAG);
        rFrag.startRecording();
    }

    @Override
    public void onClickEndSession() {
        Log.d(App.NAME, "Ending session from disconnect dialog...");

        stopService();
        showFragment(REFLECTION_TAG, null);
    }

    @Override
    public void onTimeout() {
        Log.d(App.NAME, "Dialog timed out...");
    }

    //callback method from unfinishedrecordingDialog
    @Override
    public void onButtonClicked() {
        EndRecordingDialogFragment endDialog = new EndRecordingDialogFragment();

        Bundle args = new Bundle();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        args.putString("activity", prefs.getString("activityName",""));
        endDialog.setArguments(args);
        endDialog.show(this.getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onEndCall() {
        System.out.println("-------------onEndCall---------");
        this.showFragment(MainActivity.REFLECTION_TAG, null);
    }

    public class SessionPhotoSaveTask extends AsyncTask<String, Void, Void> {

        private String locationName;
        private int height, width;

        public SessionPhotoSaveTask(String locationName, int width, int height) {
            this.locationName = locationName;
            this.height = height;
            this.width = width;
        }

        @Override
        protected Void doInBackground(String... params) {

            if (params.length < 1) {
                return null;
            }

            String placeId = params[0];

            Log.d(App.NAME, "Place ID: " + placeId);

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(apiClient, placeId).await();

            Log.d(App.NAME, result.getStatus().toString());

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    Log.d(App.NAME, "An image was returned");
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();

                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(apiClient, width, height).await()
                            .getBitmap();

                    DataPointSource dpSource = new DataPointSource(MainActivity.this);
                    dpSource.open();
                    dpSource.saveSessionPhoto(locationName, image);
                    dpSource.close();
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }

            return null;
        }
    }
}

package edu.engagement.application.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.utils.AttentionColor;

public class RecordingFragment extends Fragment implements OnClickListener {

    public static final String LOCATION_NAME_KEY = "LOCATION_NAME";
    public static final String LOCATION_LAT_KEY = "LOCATION_LATITUDE";
    public static final String LOCATION_LONG_KEY = "LOCATION_LONGITUDE";

    private MainActivity activity;

    public static int sessionId = 0;
    private RealTimeListener realTimeListener;

    private TextView attentionText;

    private ImageView eegStatusImage;

    // Attention circle
    private TextView locationName;

    // Annotation
    private SeekBar annotationBar = null;
    private Button notesButton;

    private Button startButton;
    private Button pauseButton;

    // The elapsed timer
    private Chronometer timer;
    private long elapsedTime = 0L;

    private StatusDialogFragment sdf;

    private boolean recordingCurrentState;
    private boolean recordingSavedState;
    DataPointSource mDataPointSource = null;

    private LatLng location;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        this.realTimeListener = (RealTimeListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle b = getArguments();

        String locationStr = b.getString(LOCATION_NAME_KEY, "Location error");
        double latitude = b.getDouble(LOCATION_LAT_KEY, 0);
        double longitude = b.getDouble(LOCATION_LONG_KEY, 0);

        location = new LatLng(latitude, longitude);

        mDataPointSource = new DataPointSource(this.getActivity().getApplicationContext());
        mDataPointSource.open();
        View view = inflater.inflate(R.layout.real_time_fragment_layout, container, false);

        // Set up initial screen layout and button listeners
        attentionText = (TextView) view.findViewById(R.id.attentionCircle);
        locationName = (TextView) view.findViewById(R.id.locationTextView);

        locationName.setText(locationStr);
        locationName.setSingleLine(true);
        locationName.setEllipsize(TextUtils.TruncateAt.END);

        startButton = (Button) view.findViewById(R.id.start);

        pauseButton = (Button) view.findViewById(R.id.pause);
        //make note: annotation button
        notesButton = (Button) view.findViewById(R.id.makeNoteButton);
        //submitButton = (Button) view.findViewById(R.id.submit);
        timer = (Chronometer) view.findViewById(R.id.elapsedTime);

        eegStatusImage = (ImageView)view.findViewById(R.id.statusView);


        startButton.setOnClickListener(this);

        pauseButton.setOnClickListener(this);

        notesButton.setOnClickListener(this);

        showEEGConnectionLoadingIcon();

        activity.startService();

        hideButtons(startButton);

        sdf = new StatusDialogFragment();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //
        hideButtons(pauseButton, notesButton);
        showButtons(startButton);
        stopTimer();
        // Stop reading data from EEG
        stopService();

        realTimeListener.onRecordingStopped();

        // Move back to the summary view
        activity.showFragment(MainActivity.REFLECTION_TAG, null);

    }

    @Override
    public void onClick(View view) {

            switch (view.getId()) {
                case R.id.makeNoteButton:

                    //display dialog to make note for annotation
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//                if(prev != null){
//                    transaction.remove(prev);
//                }
                    RecordingDialogFragment dialog = new RecordingDialogFragment();
                    dialog.show(activity.getSupportFragmentManager(), "dialog");
                    break;
                case R.id.start:

                    if(location != null){
                        //create sharedPreferences with init value 1, increase everytime when user presses "End Session"
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                        sessionId = prefs.getInt("sessionId", 1);
                        Log.v("The sessionId", "The init session id: " + sessionId);
                        String currentLocationName = locationName.getText().toString();

                        //saving data tp GPS table and Session table when this is a new location:
                        if (!MapFrag.locationTable.containsKey(currentLocationName)) {
                        mDataPointSource.createDataPointGps(location.latitude, location.longitude, currentLocationName);
                        }
                        //saving data to session table
                        mDataPointSource.createDataPointSession(sessionId, currentLocationName);
                        // Change button states to pause and stop
                        hideButtons(startButton);
                        showButtons(pauseButton, notesButton);

                        timer.setFormat("[Total Time: %s]");

                        startRecording();
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), "The Location is initializing...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.pause:

                    stopRecording();

                    // display confirm dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    dialogBuilder.setTitle("You are currently paused. ");
                    dialogBuilder.setMessage("No data is currently being logged.");
                    dialogBuilder.setPositiveButton("End Activity", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            realTimeListener.onRecordingStopped();

                            EndRecordingDialogFragment endDialog = new EndRecordingDialogFragment();
                            endDialog.setTargetFragment(RecordingFragment.this, 1);
                            endDialog.show(activity.getSupportFragmentManager(), "dialog");
                            
                        }
                    });
                    dialogBuilder.setNegativeButton("Resume Activity", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            startRecording();
                        }
                    });

                    dialogBuilder.create().show();
                    break;
            }

    }

    public void restoreRecordingState() {
        if (recordingSavedState) {
            startRecording();
        }
    }

    public void saveRecordingState() {
        recordingSavedState = recordingCurrentState;
    }

    public void startRecording() {
        startTimer();
        realTimeListener.onRecordingStarted();
        recordingCurrentState = true;
    }

    public void stopRecording() {
        stopTimer();
        realTimeListener.onRecordingStopped();
        recordingCurrentState = false;
    }

    public void setAttention(int attention) {
        attentionText.setText(String.valueOf(attention));
        GradientDrawable backgroundGradient = (GradientDrawable)attentionText.getBackground();
        backgroundGradient.setColor(AttentionColor.getAttentionColor(attention));
    }



    public void onEegNotFound() {
        if (sdf.isVisible()) {
            sdf.onReconnectFail();
        } else {
            sdf.show(activity.getSupportFragmentManager(), "statusDialog");
        }
    }

    public void onEegDisconnect() {

        saveRecordingState();

        if (recordingCurrentState) {
            stopRecording();
        }

        eegStatusImage.setImageResource(R.drawable.disconnected);

        sdf.show(activity.getSupportFragmentManager(), "statusDialog");
    }

    public void onEegConnect() {

        // The dialog fragment is being shown, so send updates to it
        if (sdf.isVisible()) {
            sdf.onReconnectSuccess();
        } else {
            hideEEGConnectionLoadingIcon();
            eegStatusImage.setImageResource(R.drawable.connected);

            showButtons(startButton);
        }
    }

    private void startTimer() {
        timer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        timer.start();
    }

    private void stopTimer() {
        elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
    }

    private void hideEEGConnectionLoadingIcon() {
        if (eegStatusImage.getDrawable() instanceof  AnimationDrawable) {
            ((AnimationDrawable) eegStatusImage.getDrawable()).stop();
            eegStatusImage.clearAnimation();
            eegStatusImage.setImageResource(0);
        }
    }

    private void showEEGConnectionLoadingIcon() {
        eegStatusImage.setBackgroundResource(R.drawable.connecting_animation);
        ((AnimationDrawable) eegStatusImage.getDrawable()).start();
    }

    private void hideButtons(Button... buttons) {
        for (Button button : buttons)
            button.setVisibility(View.GONE);
    }

    private void showButtons(Button... buttons) {
        for (Button button : buttons)
            button.setVisibility(View.VISIBLE);
    }

    /**
     * Stop EEG service
     */
    private void stopService() {
        activity.stopService();
    }

    public interface RealTimeListener {
        void onRecordingStarted();
        void onRecordingStopped();
    }
}



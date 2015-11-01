package edu.engagement.application.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;

public class RealTimeDataFragment extends Fragment implements OnClickListener {

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

    // Text Button remove later when eeg status detecable TODO
    private Button testButton;

    private StatusDialogFragment sdf;

    private boolean recordingCurrentState;
    private boolean recordingSavedState;
    DataPointSource mDataPointSource = null;

    private Location mLocation = null;





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        this.realTimeListener = (RealTimeListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mDataPointSource = new DataPointSource(this.getActivity().getApplicationContext());
        mDataPointSource.open();
        View view = inflater.inflate(R.layout.real_time_fragment_layout, container, false);

        // Set up initial screen layout and button listeners
        attentionText = (TextView) view.findViewById(R.id.attentionCircle);
        locationName = (TextView) view.findViewById(R.id.locationTextView);

        locationName.setText(activity.getLocationName());
        locationName.setSingleLine(true);
        locationName.setEllipsize(TextUtils.TruncateAt.END);

        startButton = (Button) view.findViewById(R.id.start);

        pauseButton = (Button) view.findViewById(R.id.pause);
        //make note: annotation button
        notesButton = (Button) view.findViewById(R.id.makeNoteButton);
        //submitButton = (Button) view.findViewById(R.id.submit);
        timer = (Chronometer) view.findViewById(R.id.elapsedTime);

        eegStatusImage = (ImageView)view.findViewById(R.id.statusView);

        //TODO text Button remover later
        testButton = (Button) view.findViewById(R.id.testButton);
        testButton.setOnClickListener(this);

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



                    mLocation = activity.getLocation();

                    if(mLocation != null){
                        //create sharedPreferences with init value 1, increase everytime when user presses "End Session"
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                        sessionId = prefs.getInt("sessionId", 1);
                        Log.v("The sessionId", "The init session id: " + sessionId);
                        String currentLocationName = locationName.getText().toString();

                        //saving data tp GPS table and Session table when this is a new location:
                        if (!MapFrag.locationTable.containsKey(currentLocationName)) {
                        mDataPointSource.createDataPointGps(mLocation.getLatitude(), mLocation.getLongitude(), currentLocationName);
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
                            //
                            hideButtons(pauseButton, notesButton);
                            showButtons(startButton);
                            stopTimer();
                            // Stop reading data from EEG
                            stopService();


                            realTimeListener.onRecordingStopped();

                            // Move back to the summary view
                            activity.changeState(MainActivity.ApplicationState.REFLECTION);
                            activity.pagerChange(1);
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



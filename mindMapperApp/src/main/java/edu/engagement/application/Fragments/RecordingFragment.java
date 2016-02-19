package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.utils.AttentionColor;

public class RecordingFragment extends Fragment implements OnClickListener {

    public static final String LOCATION_NAME_KEY = "LOCATION_NAME";
    public static final String LOCATION_LAT_KEY = "LOCATION_LATITUDE";
    public static final String LOCATION_LONG_KEY = "LOCATION_LONGITUDE";
    public static final String ACTIVITY_NAME_KEY = "ACTIVITY_NAME";
    public static final int END_ACTIVITY_REQUEST = 1;
    public static final int MAKE_NOTE_REQUEST = 2;
    public static final int PAUSE_REQUEST = 3;


    private Boolean connectionSuccessful = false;

    private MainActivity activity;

    public static int sessionId = 0;
    private RealTimeListener realTimeListener;
    private TextView attentionText;
    private ImageView messageIcon1;
    private ImageView messageIcon2;
    private ImageView messageIcon3;
    private TextView dots;
    private int messagesIconNumber = 0;
    private TextView focusLabel;

    private LinearLayout mLayout;

    //connecting layout
    private TextView connectionText;
    private ProgressBar spinner;

    //connection successful layout
    private TextView readyText;
    private Button startButton;
    private ImageView checkmarkImage;
    private TextView iAmLabel;
    private TextView atLabel;
    private TextView activityLabel;
    private TextView locationLabel;
    private TextView connectoionLabel;


    //on connection layout
    private Button retryButton;
    private TextView noConnectionText;
    private TextView noConnectionTip;
    private  ImageView bluetoothImage;


    private ImageView eegStatusImage;

    // Attention circle
    private TextView locationName;

    // Annotation
    private SeekBar annotationBar = null;
    private ImageButton notesButton;
    private ImageButton pauseButton;


    // The elapsed timer
    private Chronometer timer;
    private long elapsedTime = 0L;

    private StatusDialogFragment sdf;

    private boolean recordingCurrentState;
    private boolean recordingSavedState;
    DataPointSource mDataPointSource = null;

    private LatLng location;

    private String activityName;
    private String locationStr;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        this.realTimeListener = (RealTimeListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle b = getArguments();

        activityName = b.getString(ACTIVITY_NAME_KEY, "_ _");
        locationStr = b.getString(LOCATION_NAME_KEY, "Location error");
        double latitude = b.getDouble(LOCATION_LAT_KEY, 0);
        double longitude = b.getDouble(LOCATION_LONG_KEY, 0);

        location = new LatLng(latitude, longitude);

        mDataPointSource = new DataPointSource(this.getActivity().getApplicationContext());
        mDataPointSource.open();
        View view = inflater.inflate(R.layout.real_time_fragment_layout, container, false);

        mLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        // Set up initial screen layout and button listeners
        attentionText = (TextView) view.findViewById(R.id.attentionCircle);
        locationName = (TextView) view.findViewById(R.id.locationTextView);

        if(activityName.length() == 0){
            locationName.setText(locationStr);
        }else{
            locationName.setText(activityName + " at " + locationStr);
        }

        locationName.setSingleLine(true);
        locationName.setEllipsize(TextUtils.TruncateAt.END);
        pauseButton = (ImageButton) view.findViewById(R.id.pause);
        //make note: annotation button
        notesButton = (ImageButton) view.findViewById(R.id.makeNoteButton);
        //submitButton = (Button) view.findViewById(R.id.submit);
        timer = (Chronometer) view.findViewById(R.id.elapsedTime);
        //eegStatusImage = (ImageView)view.findViewById(R.id.statusView);
        messageIcon1 = (ImageView) view.findViewById(R.id.messageIcon1);
        messageIcon2 = (ImageView) view.findViewById(R.id.messageIcon2);
        messageIcon3 = (ImageView) view.findViewById(R.id.messageIcon3);
        dots = (TextView) view.findViewById(R.id.dots);
        focusLabel = (TextView) view.findViewById(R.id.focusLabel);

        //connecting
        connectionText = (TextView) view.findViewById(R.id.connectionText);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar);

        //connection successful weidgets
        startButton = (Button) view.findViewById(R.id.startButton);
        readyText = (TextView) view.findViewById(R.id.readyText);
        checkmarkImage = (ImageView) view.findViewById(R.id.checkmarkImage);
        connectoionLabel = (TextView) view.findViewById(R.id.connectLabel);
        iAmLabel = (TextView) view.findViewById(R.id.iAmLabel);
        atLabel = (TextView) view.findViewById(R.id.atLabel);
        activityLabel = (TextView) view.findViewById(R.id.activityLabel);
        activityLabel.setText(activityName);
        locationLabel = (TextView) view.findViewById(R.id.locationLabel);
        locationLabel.setText(locationStr);

        //connection fail weidgets
        retryButton = (Button) view.findViewById(R.id.retryConnectButton);
        noConnectionText = (TextView) view.findViewById(R.id.noConnectionText);
        noConnectionTip = (TextView) view.findViewById(R.id.noConnectionTips);
        bluetoothImage = (ImageView) view.findViewById(R.id.bluetoothImage);

        activityLabel.setOnClickListener(this);
        locationLabel.setOnClickListener(this);

        retryButton.setOnClickListener(this);

        startButton.setOnClickListener(this);

        pauseButton.setOnClickListener(this);

        notesButton.setOnClickListener(this);

        //showEEGConnectionLoadingIcon();

        activity.startService();

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
        connectionSuccessful = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == END_ACTIVITY_REQUEST && resultCode == 3){
            hideButtons(pauseButton, notesButton);
            //showButtons(startButton);
            stopTimer();
            // Stop reading data from EEG
            stopService();

            realTimeListener.onRecordingStopped();

            // Move back to the summary view
            activity.showFragment(MainActivity.REFLECTION_TAG, null);
        }
        if(requestCode == MAKE_NOTE_REQUEST && resultCode == 3){
            Log.v("messageIconNumber", messagesIconNumber + "");
            if(messagesIconNumber == 3){
                dots.setVisibility(View.VISIBLE);
            }
            if(messagesIconNumber == 2){
                messageIcon3.setVisibility(View.VISIBLE);
                messagesIconNumber += 1;
            }
            if(messagesIconNumber == 1){
                messageIcon2.setVisibility(View.VISIBLE);
                messagesIconNumber += 1;
            }
            if(messagesIconNumber == 0){
                messageIcon1.setVisibility(View.VISIBLE);
                messagesIconNumber += 1;
            }
        }
        if(requestCode == PAUSE_REQUEST && resultCode == PauseDialogFragment.PAUSE_RESULT_RESUME){
            startRecording();
        }
        if(requestCode == PAUSE_REQUEST && resultCode == PauseDialogFragment.PAUSE_RESULT_END){
            realTimeListener.onRecordingStopped();
            EndRecordingDialogFragment endDialog = new EndRecordingDialogFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("activity", activityName);
            endDialog.setArguments(args);
            endDialog.setCancelable(false);

            endDialog.setTargetFragment(RecordingFragment.this, END_ACTIVITY_REQUEST);
            endDialog.show(activity.getSupportFragmentManager(), "dialog");

        }
    }

    @Override
    public void onClick(View view) {

            switch (view.getId()) {
                case R.id.retryConnectButton:
                        activity.startService();
                        hideConnecttionFail();
                        showConnectionStatus();
                    break;
                case R.id.makeNoteButton:

                    //display dialog to make note for annotation
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//                if(prev != null){
//                    transaction.remove(prev);
//                }
                    RecordingDialogFragment dialog = new RecordingDialogFragment();
                    dialog.setTargetFragment(RecordingFragment.this, MAKE_NOTE_REQUEST);
                    dialog.setCancelable(false);
                    dialog.show(activity.getSupportFragmentManager(), "dialog");
                    break;
                case R.id.startButton:
                    if(location != null){
                        //create sharedPreferences with init value 1, increase everytime when user presses "End Session"
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                        sessionId = prefs.getInt("sessionId", 1);
                        //log
                        Log.v("The sessionId", "The init session id: " + sessionId);

                        //saving data tp GPS table and Session table when this is a new location:
                        if (!MapFrag.locationTable.containsKey(locationStr)) {
                            mDataPointSource.createDataPointGps(location.latitude, location.longitude, locationStr);
                        }

                        //saving data to session table
                        mDataPointSource.createDataPointSession(sessionId, activityName, locationStr);

                        // Change button states to pause and stop
                        hideConnecttionSuccessful();
                        showRecordingLayout();

                        Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR);
                        int AorP = c.get(Calendar.AM_PM);
                        if(AorP == 1){
                            timer.setFormat(hour + " PM " + " for %s");
                        }else{
                            timer.setFormat(hour + " AM " + " for %s");
                        }


                        startRecording();
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), "The Location is initializing...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.pause:
                    stopRecording();
                    // display confirm dialog
                    PauseDialogFragment pauseDialog = new PauseDialogFragment();
                    pauseDialog.setTargetFragment(RecordingFragment.this, PAUSE_REQUEST);
                    pauseDialog.setCancelable(false);
                    pauseDialog.show(activity.getSupportFragmentManager(), "dialog");
//
//                    LayoutInflater factory = LayoutInflater.from(activity);
//                    final View dialogView = factory.inflate(R.layout.pause_alert_dialog, null);
//                    dialogBuilder.setView(dialogView);
//                    dialogBuilder.setCancelable(false);
//                    dialogBuilder.setPositiveButton("      End Activity     ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//
//                        }
//                    });
//                    dialogBuilder.setNegativeButton("     Resume      ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                        }
//                    });
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

        if(connectionSuccessful == false){
            hideConnectionStatus();
            showConnecttionFail();
        }else{
            Log.v("Recording", "eeg not found, situation needs to handle");
        }
    }


    public void onEegDisconnect() {

        saveRecordingState();

        if (recordingCurrentState) {
            stopRecording();
        }
        //eegStatusImage.setImageResource(R.drawable.disconnected);
        if(connectionSuccessful == true){
            sdf.show(activity.getSupportFragmentManager(), "statusDialog");
        }
    }

    public void onEegConnect() {

        // The dialog fragment is being shown, so send updates to it
        if (sdf.isVisible()) {
            sdf.onReconnectSuccess();
        } else {
            //hideEEGConnectionLoadingIcon();
            //eegStatusImage.setImageResource(R.drawable.connected);
            connectionSuccessful = true;
            hideConnectionStatus();
            showConnecttionSuccessful();
        }
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

    private void startTimer() {
        timer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        timer.start();
    }

    private void stopTimer() {
        elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
    }

    /* Layout change methods */

    private void showRecordingLayout() {
        mLayout.setBackgroundColor(Color.WHITE);
        pauseButton.setVisibility(View.VISIBLE);
        notesButton.setVisibility(View.VISIBLE);
        attentionText.setVisibility(View.VISIBLE);
        locationName.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        focusLabel.setVisibility(View.VISIBLE);
    }

    private void showConnecttionFail() {
        mLayout.setBackgroundColor(Color.parseColor("#FFEB3B"));
        retryButton.setVisibility(View.VISIBLE);
        noConnectionText.setVisibility(View.VISIBLE);
        noConnectionTip.setVisibility(View.VISIBLE);
        bluetoothImage.setVisibility(View.VISIBLE);
    }
    private void hideConnecttionFail() {
        retryButton.setVisibility(View.GONE);
        noConnectionText.setVisibility(View.GONE);
        noConnectionTip.setVisibility(View.GONE);
        bluetoothImage.setVisibility(View.GONE);
    }

    private void showConnecttionSuccessful() {
        mLayout.setBackgroundColor(Color.WHITE);
        readyText.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        iAmLabel.setVisibility(View.VISIBLE);
        connectoionLabel.setVisibility(View.VISIBLE);
        atLabel.setVisibility(View.VISIBLE);
        activityLabel.setVisibility(View.VISIBLE);
        locationLabel.setVisibility(View.VISIBLE);
        checkmarkImage.setVisibility(View.VISIBLE);
    }

    private void hideConnecttionSuccessful() {

        readyText.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        iAmLabel.setVisibility(View.GONE);
        connectoionLabel.setVisibility(View.GONE);
        atLabel.setVisibility(View.GONE);
        activityLabel.setVisibility(View.GONE);
        locationLabel.setVisibility(View.GONE);
        checkmarkImage.setVisibility(View.GONE);
    }

    private void showConnectionStatus() {
        mLayout.setBackgroundColor(Color.parseColor("#FFEB3B"));
        spinner.setVisibility(View.VISIBLE);
        connectionText.setVisibility(View.VISIBLE);
    }
    private void hideConnectionStatus() {
        spinner.setVisibility(View.GONE);
        connectionText.setVisibility(View.GONE);
    }


//    private void hideEEGConnectionLoadingIcon() {
//        if (eegStatusImage.getDrawable() instanceof  AnimationDrawable) {
//            ((AnimationDrawable) eegStatusImage.getDrawable()).stop();
//            eegStatusImage.clearAnimation();
//            eegStatusImage.setImageResource(0);
//        }
//    }

//    private void showEEGConnectionLoadingIcon() {
//        eegStatusImage.setBackgroundResource(R.drawable.connecting_animation);
//        ((AnimationDrawable) eegStatusImage.getDrawable()).start();
//    }

    private void hideButtons(ImageButton... buttons) {
        for (ImageButton button : buttons)
            button.setVisibility(View.GONE);
    }

    private void showButtons(ImageButton... buttons) {
        for (ImageButton button : buttons)
            button.setVisibility(View.VISIBLE);
    }

}



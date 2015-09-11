package edu.engagement.application.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import edu.engagement.application.App;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;

public class RealTimeDataFragment extends Fragment implements OnClickListener {

    private MainActivity activity;

    private RealTimeListener realTimeListener;

    private TextView attentionText;
    private ImageView drawingImageView;

    // Fab Button
    private TextView fabButton;

    // Attention circle
    private TextView location;

    // Annotation
    private SeekBar annotationBar = null;
    private Button mMakeNotes;

    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;

    // The elapsed timer
    private Chronometer timer;
    private long elapsedTime = 0L;

    // Text Button remove later when eeg status detecable TODO
    private Button testButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        this.realTimeListener = (RealTimeListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /* Place Picker Experiment */
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = this.getActivity().getApplicationContext();
        try {
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(builder.build(context),
                    PLACE_PICKER_REQUEST);
            // PlacePicker.getPlace(builder, context);

        } catch (GooglePlayServicesRepairableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        View view = inflater.inflate(R.layout.real_time_fragment_layout, container, false);


        //activity.switchToFragment(REAL_TIME_FRAG);

        // Set up initial screen layout and button listeners
        attentionText = (TextView) view.findViewById(R.id.attentionCircle);
        location = (TextView) view.findViewById(R.id.locationTextView);
        location.setText(activity.getLocation());
        location.setSingleLine(true);
        location.setEllipsize(TextUtils.TruncateAt.END);
        System.out.println("attention text view initialized");
        fabButton = (TextView) activity.findViewById(R.id.fabButton);

        startButton = (Button) view.findViewById(R.id.start);

        pauseButton = (Button) view.findViewById(R.id.pause);
        resumeButton = (Button) view.findViewById(R.id.resume);
        //make note: annotation button
        mMakeNotes = (Button) view.findViewById(R.id.makeNoteButton);
        //submitButton = (Button) view.findViewById(R.id.submit);
        timer = (Chronometer) view.findViewById(R.id.elapsedTime);

        //TODO text Button remover later
        testButton = (Button) view.findViewById(R.id.testButton);
        testButton.setOnClickListener(this);

        startButton.setOnClickListener(this);

        pauseButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);

        mMakeNotes.setOnClickListener(this);

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
                // TODO start recording and timer
                // Change button states to pause and stop
                startButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                mMakeNotes.setVisibility(View.VISIBLE);

                // TODO: Alex I added the startService() method, which actually starts the EEG recording. If you think it makes sense to start the timer in that method you can move it there
                timer.setFormat("[Total Time: %s]");
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                realTimeListener.onRecordingStarted();
                break;
            case R.id.pause:
                Toast.makeText(getActivity(), "recording paused", Toast.LENGTH_SHORT).show();

                // Hide pause button, and show resume button
                pauseButton.setVisibility(View.GONE);
                resumeButton.setVisibility(View.VISIBLE);

                stopTimer();

                realTimeListener.onRecordingStopped();

                // display confirm dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle("You are currently paused. ");
                dialogBuilder.setMessage("No date is currently being log.");
                dialogBuilder.setPositiveButton("End Activity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "recording stopped", Toast.LENGTH_SHORT).show();
                        // Stop reading data from EEG
                        stopService();

                        realTimeListener.onRecordingStopped();

                        // Move back to the graph view
                        activity.changeState(MainActivity.ApplicationState.REFLECTION);
                        activity.pagerChange(1);
                    }
                });
                dialogBuilder.setNegativeButton("Resume Activity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startTimer();
                        realTimeListener.onRecordingStarted();
                    }
                });

                dialogBuilder.create().show();
                break;
            case R.id.resume:
                Toast.makeText(getActivity(), "recording resumed", Toast.LENGTH_SHORT).show();

                // Hide resume button, show pause button
                resumeButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);

                startTimer();
                break;
            case R.id.testButton:
                StatusDialogFragment statusDialog = new StatusDialogFragment();
                statusDialog.show(activity.getSupportFragmentManager(), "statusDialog");
                break;
        }
    }

    /*
     * Callback method that is called after user selects a location.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == activity.RESULT_OK) {
                // Load the bundle from the activity's intent
                Bundle bundle = activity.getIntent().getExtras();

                // Initialize new bundle if it does not exist
                if (bundle == null) {
                    bundle = new Bundle();
                }
                Place place = PlacePicker.getPlace(data,
                        activity.getApplicationContext());

                // Save the location into the bundle
                bundle.putString("Location", "" + place.getName());
                location.setText(place.getName().toString());

                // Save the bundle into the activity
                activity.getIntent().putExtras(bundle);
            } else {
                Log.d(App.NAME, "PlacePicker cancelled!");

                // Stop EEG service
                stopService();

                // Move back to the graph view
                activity.changeState(MainActivity.ApplicationState.REFLECTION);
                activity.pagerChange(1);
            }
        }
    }

    public void setAttention(int attention) {
        attentionText.setText(String.valueOf(attention));
    }


    private void startTimer() {
        timer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        timer.start();
    }

    private void stopTimer() {
        elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
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

    public void doNegativeCLick(){

    }
}



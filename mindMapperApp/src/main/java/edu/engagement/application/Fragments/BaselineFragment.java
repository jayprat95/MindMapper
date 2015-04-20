package edu.engagement.application.Fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import edu.engagement.application.MainActivity;
import edu.engagement.application.MindwaveService;
import edu.engagement.application.R;
import edu.engagement.application.fabbutton.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaselineFragment extends Fragment implements Runnable,
        OnClickListener, FloatingActionButton.OnCheckedChangeListener{

    private MainActivity activity;

    private Button startButton;

    private ImageView circle;
    private TextView text;
    private TextView score;
    private int clicks = 0;

    // ----------------- Button -------------------
    private Button connect;
    private Button start;
    private Button stop;
    private Button annotate;
    private Button disconnect;

    /*
     * Change these values to set: 1) How many taps are required 2) The delay in
     * seconds between the circle appearing
     */
    public final static int REQUIRED = 4;
    public final static int DELAY = 2;
    private Thread timer;
    boolean delay;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstancedState) {
        View v = inflator.inflate(R.layout.baseline, container, false);

        timer = new Thread(this);

        // Set up initial screen layout
        text = (TextView) v.findViewById(R.id.b_instructions);

        // ------------------- Buttons
        connect = (Button) v.findViewById(R.id.btnStartService);
        start = (Button) v.findViewById(R.id.start);
        stop = (Button) v.findViewById(R.id.stop);
        annotate = (Button) v.findViewById(R.id.annotate);
        disconnect = (Button) v.findViewById(R.id.btnStopService);

        connect.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        annotate.setOnClickListener(this);
        disconnect.setOnClickListener(this);

        // startButton = (Button) v.findViewById(R.id.b_start);
        // score = (TextView) v.findViewById(R.id.b_score);
        // score.setVisibility(View.GONE);
        // circle = (ImageView) v.findViewById(R.id.b_circle);
        // circle.setVisibility(View.GONE);

        // startButton.setOnClickListener(this);
        // circle.setOnClickListener(this);
        //
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // case R.id.b_start:
            // start();
            // break;
            // case R.id.b_circle:
            // clicks++;
            // delay = true;
            // hideCircle();
            // break;
            case R.id.btnStartService:
                connectClicked();
                break;
            case R.id.start:
                // start.setVisibility(View.INVISIBLE);
                // puase.setVisibility(View.VISIBLE);
                startClicked();
                break;
            case R.id.stop:
                stopClicked();
                break;
            case R.id.btnStopService:
                // stop.setVisibility(View.INVISIBLE);
                disconnectClicked();
                break;
            case R.id.annotate:
                onClickAnnotate();
                // TODO for annotation
                break;
        }
    }

    /* ------------ states ------------ */
    private void connectClicked() {
        startService();
    }

    private void startClicked() {
        start.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);
        // disconnect.setVisibility(View.INVISIBLE);
        annotate.setVisibility(View.VISIBLE);

        String toastMsg = String.format("Recording Started");
        Toast.makeText(this.getActivity().getApplicationContext(), toastMsg,
                Toast.LENGTH_LONG).show();

//		Bundle bundle = this.getActivity().getIntent().getExtras();
//		System.out.println("WEEEEE");


    }

    private void stopClicked() {
        //
        // if (stop.getText().equals("Stop")) {
        // stop.setText("Resume");
        // start.setVisibility(View.VISIBLE);
        // } else {
        // pause.setText("Pause");
        // start.setVisibility(View.INVISIBLE);
        // }

        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        disconnect.setVisibility(View.VISIBLE);
        annotate.setVisibility(View.INVISIBLE);
        String toastMsg = String.format("Recording Stopped");
        Toast.makeText(this.getActivity().getApplicationContext(), toastMsg,
                Toast.LENGTH_LONG).show();
    }

    private void disconnectClicked() {
        connect.setVisibility(View.VISIBLE);
        start.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
        disconnect.setVisibility(View.INVISIBLE);
        annotate.setVisibility(View.INVISIBLE);
        stopService();
    }

    // TODO Not sure if we use this
    private void start() {
        startButton.setVisibility(View.GONE);
        // text.setVisibility(View.GONE);
        showCircle();

        System.out
                .println("=======================================================================WAHOOOO");

        // Start taking baseline data
        activity.startBaselineMode();

        timer.setDaemon(true);
        timer.start();

    }

    // Method to start the service
    public void startService() {

		/* Place Picker Experiment */
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = activity.getApplicationContext();
        try {
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
            // PlacePicker.getPlace(builder, context);

        } catch (GooglePlayServicesRepairableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        activity.startService(new Intent(activity.getBaseContext(),
                MindwaveService.class));

    }

    // Method to stop the service, called after clicking disconnect
    public void stopService() {

        String toastMsg = "Stopped Service";
        Toast.makeText(this.getActivity().getApplicationContext(), toastMsg,
                Toast.LENGTH_LONG).show();
        activity.stopService(new Intent(activity.getBaseContext(),
                MindwaveService.class));
    }

    /*
     * Callback method that is called after user selects a location(from start
     * service call).
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == this.getActivity().RESULT_OK) {
                // Load the bundle from the activity's intent
                Bundle bundle = this.getActivity().getIntent().getExtras();

                // Initialize new bundle if it does not exist
                if (bundle == null) {
                    bundle = new Bundle();
                }
                Place place = PlacePicker.getPlace(data, this.getActivity()
                        .getApplicationContext());

                // Save the location into the bundle
                bundle.putString("Location", "" + place.getName());

                // Save the bundle into the activity
                this.getActivity().getIntent().putExtras(bundle);

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this.getActivity().getApplicationContext(),
                        toastMsg, Toast.LENGTH_LONG).show();

//				// Set up buttons for the screen later
//				connect.setVisibility(View.INVISIBLE);
//				start.setVisibility(View.VISIBLE);
//				stop.setVisibility(View.INVISIBLE);
//				disconnect.setVisibility(View.VISIBLE);
//				annotate.setVisibility(View.INVISIBLE);

            }

            // Set up buttons for the screen later
            connect.setVisibility(View.INVISIBLE);
            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.INVISIBLE);
            disconnect.setVisibility(View.VISIBLE);
            annotate.setVisibility(View.INVISIBLE);

        }
    }

    /*
     * Brings up a dialog that prompts the user to select either High, Medium,
     * or Low, or custom message. This annotation will be saved and used later
     * for the time graph
     */
    public void onClickAnnotate() {

        final CharSequence[] items = {"High", "Medium", "Low", "Custom"};
        String result = "";

        // Setting up the first dialog (High, Medium, Low)
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this.getActivity());
        builder.setTitle("Make your selection");

        // Setting up second dialog (Custom Annotations)
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(
                this.getActivity());
        builder2.setTitle("Enter Custom Annotation");
        final EditText textField = new EditText(this.getActivity());
        builder2.setView(textField);
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        builder2.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                // Custom button was pressed
                if (item == 3) {
                    // Show the Custom annotations dialog
                    AlertDialog alert = builder2.create();
                    alert.show();
                } else {
                    // result = items[item].toString();
                    // save the
                }
            }

        });

        // Create and show the dialog message
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void finish() {
        circle.setVisibility(View.GONE);
        text.setText(R.string.baseline_instruction_finish);
        text.setVisibility(View.VISIBLE);
        startButton.setText(R.string.baseline_finish);
        // Set new listener for button
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.switchToFragment(activity.REAL_TIME_TAG);
            }
        });
        startButton.setVisibility(View.VISIBLE);
        try {
            timer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Save baseline average
        // exit baseline mode in activity
        clicks = 0;
        activity.exitBaselineMode();
    }

    public void setScore(String avg) {
        score.setText(avg);
        score.setVisibility(View.VISIBLE);
    }

    public void showCircle() {
        delay = false;
        circle.setVisibility(View.VISIBLE);
    }

    public void hideCircle() {
        circle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void run() {
        while (clicks < REQUIRED) {
            if (delay) {
                try {
                    Thread.sleep(DELAY * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                delay = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCircle();
                    }
                });
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    /**
     * Called when the checked state of a FAB has changed.
     *
     * @param fabView   The FAB view whose state has changed.
     * @param isChecked The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(FloatingActionButton fabView, boolean isChecked) {
        switch (fabView.getId()){
            case R.id.fab_1:
//                Log.d(TAG, String.format("FAB 1 was %s.", isChecked ? "checked" : "unchecked"));
                Toast.makeText(getActivity(),String.format("FAB 1 was %s.", isChecked ? "checked" : "unchecked"),Toast.LENGTH_SHORT).show();
                break;
//            case R.id.fab_2:
//                Toast.makeText(getActivity(),String.format("FAB  was %s.", isChecked ? "checked" : "unchecked"),Toast.LENGTH_SHORT).show();
//                break;
            default:
                break;
        }
    }
}
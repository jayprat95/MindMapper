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
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Queue;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;

public class RealTimeDataFragment extends Fragment implements OnClickListener {

    private MainActivity activity;

    private TextView attentionText;
    private ImageView drawingImageView;

    // Fab Button
    private TextView fabButton;

    // Attention circle
    private TextView location;
    // Annotation shit
    private SeekBar annotationBar = null;
    private TextView annotationValue;
    private EditText annotationInput;

    // CLOSE button
    private Button closeButton;
    private Button startButton;
    private Button stopButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button submitButton;

    // The elapsed timer
    private Chronometer timer;
    private long elapsedTime = 0L;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
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
        annotationInput = (EditText) view.findViewById(R.id.annotationInput);
        startButton = (Button) view.findViewById(R.id.start);
        stopButton = (Button) view.findViewById(R.id.stop);
        pauseButton = (Button) view.findViewById(R.id.pause);
        resumeButton = (Button) view.findViewById(R.id.resume);
        submitButton = (Button) view.findViewById(R.id.submit);
        timer = (Chronometer) view.findViewById(R.id.elapsedTime);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);


        // Close Button
        closeButton = (Button) view.findViewById(R.id.real_time_fragment_close);
        closeButton.setOnClickListener(this);

//         Might not need this stuff since we're not displaying the slide bar value
//         Annotation Bar Stuff
        annotationBar = (SeekBar) view.findViewById(R.id.annotation_bar);
        annotationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress *= 25;
                if(progress <= 50){
                    setProgressBarColor(annotationBar,Color.rgb( 255 - (255/100 * (100 - progress*2)), 255, 0));

                }else{
                    setProgressBarColor(annotationBar,Color.rgb( 255, 255 - (255/100 * (progress - 50)*2), 0));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        fabButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.real_time_fragment_close:
                activity.changeState(MainActivity.state.SLIDING_TABS_STATE);
                break;
            case R.id.start:
                // TODO start recording and timer
                Toast.makeText(getActivity(), "recording started", Toast.LENGTH_SHORT).show();

                // Change button states to pause and stop
                startButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);

                timer.setFormat("[Total Time: %s]");
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();


                break;
            case R.id.pause:
                Toast.makeText(getActivity(), "recording paused", Toast.LENGTH_SHORT).show();

                // Hide pause button, and show resume button
                pauseButton.setVisibility(View.GONE);
                resumeButton.setVisibility(View.VISIBLE);

                stopTimer();
                break;
            case R.id.resume:
                Toast.makeText(getActivity(), "recording resumed", Toast.LENGTH_SHORT).show();

                // Hide resume button, show pause button
                resumeButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);

                startTimer();
                break;
            case R.id.stop:
                // TODO display confirmation message before stopping recording

                stopTimer();

                // display confirm dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setMessage("Do you want to finish your current recording session?");
                dialogBuilder.setPositiveButton("Yes, I'm done.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "stopped recording", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setNegativeButton("No, take me back.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startTimer();
                        Toast.makeText(getActivity(), "resumed recording", Toast.LENGTH_SHORT).show();
                    }
                });

                dialogBuilder.create().show();

                break;
            case R.id.submit:
                // TODO submit annotation
                Toast.makeText(getActivity(), "Annotation saved", Toast.LENGTH_SHORT).show();
                annotationInput.setText("");
                annotationBar.setProgress(0);
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

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(activity.getApplicationContext(), toastMsg,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setAttention(Queue<Integer> attentionLevels) {
        System.out.println("attention text view initialized");

//        Integer[] levels = attentionLevels.toArray();

        if (attentionText != null) {
            if (!attentionLevels.isEmpty()) {
//        		attentionText.setText(attentionLevels.peek().toString());
                attentionText.setText(attentionLevels.toString());
                attentionText.setVisibility(View.VISIBLE);

                Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                // Circle

//      		    Paint paint = new Paint();
//      		    paint.setColor(Color.GREEN);
//      		    paint.setStyle(Paint.Style.STROKE);
//      		    paint.setStrokeWidth(5);
                float x = 100;
                float y = 100;
                //float radius = 20;

                while (!attentionLevels.isEmpty()) {
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#0A96D1"));//Color.GREEN);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setAntiAlias(true);
                    paint.setStrokeWidth((float) (Math.random() * 6));
                    //added a +5 to test without the headset on aka 0
                    canvas.drawCircle(x, y, attentionLevels.poll().intValue() + 5, paint);
//      		    	canvas.drawCircle(x, y, attentionLevels.peek().intValue()+5, paint);
                }

                drawingImageView.setImageBitmap(bitmap);
            }
//			attentionText.setText(attention);
//			attentionText.setVisibility(View.VISIBLE);

//		    Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//		    Canvas canvas = new Canvas(bitmap);
//		    drawingImageView.setImageBitmap(bitmap);
//
//		    // Circle
//
//		    Paint paint = new Paint();
//		    paint.setColor(Color.GREEN);
//		    paint.setStyle(Paint.Style.STROKE);
//		    paint.setStrokeWidth(5);
//		    float x = 100;
//		    float y = 100;
//		    //float radius = 20;
//		    canvas.drawCircle(x, y, Integer.parseInt(attention), paint);
        }
    }


    public void setAttText(String str)
    {
        this.attentionText.setText(str);
    }

    private void setProgressBarColor(SeekBar seakBar, int newColor){
        LayerDrawable ld = (LayerDrawable) seakBar.getProgressDrawable();
        ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progressShape);
        d1.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);

    }

    private void startTimer() {
        timer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
        timer.start();
    }

    private void stopTimer() {
        elapsedTime = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
    }
}



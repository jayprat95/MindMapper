package edu.engagement.application.Fragments;

import java.util.Queue;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

public class RealTimeDataFragment extends Fragment implements OnClickListener{
	
	private MainActivity activity;
	
	private TextView attentionText;
	private ImageView drawingImageView;

    // Fab Button
    private TextView fabButton;

    // Annotation bar
    private SeekBar annotationBar = null;

    // Annotation value
    private TextView annotationValue;

    // CLOSE button
    private Button closeButton;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


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

        // Close Button
        closeButton = (Button)view.findViewById(R.id.real_time_fragment_close);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeState(MainActivity.state.SLIDING_TABS_STATE);
            }
        });

        //activity.switchToFragment(REAL_TIME_FRAG);
        
        // Set up initial screen layout
        attentionText = (TextView) view.findViewById(R.id.attentionCircle);
        System.out.println("attention text view initialized");
        fabButton = (TextView)activity.findViewById(R.id.fabButton);
        annotationValue = (TextView)view.findViewById(R.id.annotationValue);

        // Annotation Bar Stuff

        annotationBar = (SeekBar) view.findViewById(R.id.annotation_bar);
        annotationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                annotationValue.setText(progressChanged + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                annotationValue.setText(progressChanged + "%");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

//                Toast.makeText(activity,"seek bar progress:"+progressChanged,
//                        Toast.LENGTH_SHORT).show();

            }
        });










        
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
//        fabButton.setVisibility(View.INVISIBLE);
    }
    
    @Override
	public void onClick(View view) {
		switch (view.getId()) {
//		case R.id.b_start:
//			start();
//			break;
//		case R.id.b_circle:
//			clicks++;
//			delay = true;
//			hideCircle();
//			break;
		}
		
	}
    
    public void setAttention(Queue<Integer> attentionLevels) {
        System.out.println("attention text view initialized");
        
//        Integer[] levels = attentionLevels.toArray();
        
        if(attentionText != null){
        	if(!attentionLevels.isEmpty()){
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
      		    
      		    while(!attentionLevels.isEmpty()){
      		    	Paint paint = new Paint();
          		    paint.setColor(Color.parseColor("#0A96D1"));//Color.GREEN);
          		    paint.setStyle(Paint.Style.STROKE);
          		    paint.setAntiAlias(true);
          		    paint.setStrokeWidth((float) (Math.random()*6));
      		    	//added a +5 to test without the headset on aka 0
      		    	canvas.drawCircle(x, y, attentionLevels.poll().intValue()+5, paint);
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

}



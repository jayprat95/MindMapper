package edu.engagement.application.Fragments;

import java.util.Queue;
import java.util.Set;

import zephyr.android.HxMBT.BTClient;

import com.neurosky.thinkgear.TGDevice;

import edu.engagement.application.MainActivity;
import edu.engagement.application.NewConnectedListener;
import edu.engagement.application.R;
import edu.engagement.application.R.layout;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RealTimeDataFragment extends Fragment implements OnClickListener{

	private MainActivity activity;

	private TextView attentionText;
	private ImageView drawingImageView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.real_time_fragment_layout, container, false);

        //activity.switchToFragment(REAL_TIME_FRAG);

        // Set up initial screen layout
        attentionText = (TextView) view.findViewById(R.id.EEGText);
        System.out.println("attention text view initialized");
        drawingImageView = (ImageView) view.findViewById(R.id.attentionCircle);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
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



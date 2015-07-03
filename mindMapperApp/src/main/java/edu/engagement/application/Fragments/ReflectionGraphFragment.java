package edu.engagement.application.Fragments;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.Database.DataPointSource;

public class ReflectionGraphFragment extends Fragment implements OnChartValueSelectedListener {
    private MainActivity activity;
    private LineChart mChart;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reflection_graphs, container, false);

        try {
            DataPointSource dataSource = new DataPointSource(this.getActivity()
                    .getApplicationContext());
            dataSource.open();
//			loadReflectionData(dataSource);
            System.out.println("Trying to graph load data points ");

            mChart = (LineChart) view.findViewById(R.id.chart);
            mChart.setOnChartValueSelectedListener(this);

            // no description text
            mChart.setDescription("AVG Attention vs gpsKey");
            //mChart.setUnit(" $");

            // enable value highlighting
            mChart.setHighlightEnabled(true);

            // enable touch gestures
            mChart.setTouchEnabled(true);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(true);

            mChart.setDrawGridBackground(false);

            mChart.getXAxis().setDrawGridLines(false);
            mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

//	        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
//	        mChart.setValueTypeface(tf);

//	        XLabels x = mChart.getXLabels();
//	        x.setTypeface(tf);
//
//	        YLabels y = mChart.getYLabels();
//	        y.setTypeface(tf);
//	        y.setLabelCount(5);

            // add data
            //setData(45, 100);
            loadReflectionData(dataSource);

            mChart.animateXY(2000, 2000);

            // dont forget to refresh the drawing
            mChart.invalidate();

        } catch (Exception e) {
            System.out.println("Reflection Graph Fragment: " + e.getMessage());
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        Toast.makeText(this.activity, entry.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    private void loadReflectionData(DataPointSource dpSource) {
        List<double[]> results = dpSource.getDayGraphDataset();

        //timestamp starts 1417546011230
        long currentTime = System.currentTimeMillis();

    	/* 
		 * 0 - timestamp
		 * 1 - Attention
		 */

        ArrayList<String> xVals = new ArrayList<String>();
        //for(double[] pointArray : results)
        for (int i = results.size() - 1; i >= 0; i--) {
            double[] pointArray = results.get(i);
            if (pointArray[1] != 0) {
                xVals.add(i/*pointArray[0]-currentTime*/ + "");
                System.out.println("Add xVal: " + i/*pointArray[0]-currentTime*/);
            }
        }

        ArrayList<Entry> vals1 = new ArrayList<Entry>();

        //for(double[] pointArray : results)
        for (int i = results.size() - 1; i >= 0; i--) {
            double[] pointArray = results.get(i);
            if (pointArray[1] != 0) {
                vals1.add(new Entry((float) pointArray[1], (int) (i/*pointArray[0]-currentTime*/)));
                System.out.println("Add vals1: (" + pointArray[1] + ", " + (i/*pointArray[0]-currentTime*/) + ")");
            }
        }


        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setDrawCubic(false);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);
        set1.setDrawCircles(true);
        set1.setLineWidth(2f);
        set1.setCircleSize(5f);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

//    	List<double[]> results = dpSource.getMapDataset();
//
//		for(int i=0; i < results.size(); i++){
//			double[] data = results.get(i);
//
//			System.out.println("\nDay Graph Dataset results: " + i);
//			for(int j=0; j < data.length; j++){
//				System.out.print(data[j] + ", ");
//			}
//		}

//    	for(double[] pointArray : results)
//		{
//		//	addPoint(pointArray[2], pointArray[3], radius, getEngagementColor(pointArray[1], 100));
//		}
    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((1990 + i) + "");
        }

        ArrayList<Entry> vals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            vals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(2f);
        set1.setCircleSize(5f);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }
}

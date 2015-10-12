package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.Database.DataPointSource;

public class GraphActivity extends Activity implements OnChartValueSelectedListener {

    List<GraphAnnotation> mAnnotations;
    ListView mListView;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mAnnotations = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.listView);


            mChart = (LineChart) findViewById(R.id.chart);
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

            mChart.animateXY(2000, 2000);

            // dont forget to refresh the drawing
            mChart.invalidate();


        new GraphLoadTask(this).execute();

    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }



    private class GraphLoadTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public GraphLoadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            // idk, man, this just works
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /* ---------------------- load data begin ---------------------- */
            try {
                DataPointSource dataSource = new DataPointSource(context);
                dataSource.open();

                // Load values to cards
                loadAnnotationPoints(dataSource);
                loadReflectionData(dataSource);

            } catch (Exception e) {
                // sqlite db locked - concurrency issue
                System.out
                        .println("Graph - sqlite db locked - concurrency issue ");
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GraphListViewAdpter adapter = new GraphListViewAdpter(mAnnotations);
            mListView.setAdapter(adapter);
        }

        private void loadAnnotationPoints(DataPointSource dbSource) {

            //List<double[]> results = dbSource.getMapDataset();
            //List<EventSummary> events = new ArrayList<>(results.size());


            GraphAnnotation annotation1 = new GraphAnnotation("Annotation #1", "at 8.30am", "I am now coding at Mcb student lounge.");
            GraphAnnotation annotation2 = new GraphAnnotation("Annotation #2", "at 10.00am", "I am now do HW at at Mcb student lounge.");
            GraphAnnotation annotation3 = new GraphAnnotation("Annotation #3", "at 12.05am", "I am reading book at Mcb student lounge.");
            GraphAnnotation annotation4 = new GraphAnnotation("Annotation #4", "at 1.05pm", "I am talking with friends at Mcb student lounge.");
            mAnnotations.add(annotation1);
            mAnnotations.add(annotation2);
            mAnnotations.add(annotation3);
            mAnnotations.add(annotation4);
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
    }

}

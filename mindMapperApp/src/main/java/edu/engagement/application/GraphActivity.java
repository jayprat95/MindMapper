package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.Session;

public class GraphActivity extends Activity implements OnChartValueSelectedListener {

    List<Annotation> mAnnotations;
    ListView mListView;
    private CombinedChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mAnnotations = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.listView);


            mChart = (CombinedChart) findViewById(R.id.chart);
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

                Session s = dataSource.loadSessionData(1);


            } catch (Exception e) {
                // sqlite db locked - concurrency issue
                System.out.println("Graph - sqlite db locked - concurrency issue ");
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GraphListViewAdpter adapter = new GraphListViewAdpter(mAnnotations);
            mListView.setAdapter(adapter);

            CombinedData data = new CombinedData();
        }

        private void loadSession(DataPointSource dbSource) {

            //List<double[]> results = dbSource.getMapDataset();
            //List<EventSummary> events = new ArrayList<>(results.size());


            Annotation annotation1 = new Annotation("I am now coding at Mcb student lounge.", AttentionLevel.HIGH, 0);
            Annotation annotation2 = new Annotation("I am now do HW at at Mcb student lounge.", AttentionLevel.MEDIUM, 0);
            Annotation annotation3 = new Annotation("I am reading book at Mcb student lounge.", AttentionLevel.MEDIUM, 0);
            Annotation annotation4 = new Annotation("I am talking with friends at Mcb student lounge.", AttentionLevel.LOW, 0);
            mAnnotations.add(annotation1);
            mAnnotations.add(annotation2);
            mAnnotations.add(annotation3);
            mAnnotations.add(annotation4);
        }
    }
}

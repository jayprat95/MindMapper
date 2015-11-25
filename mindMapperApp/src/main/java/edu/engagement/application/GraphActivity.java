package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.SessionLocation;

public class GraphActivity extends Activity implements OnChartValueSelectedListener, AbsListView.OnScrollListener {

    public static final String SESSION_ID_TAG = "SessionId";

    private ListView mListView;
    private CombinedChart mChart;
    private List<Annotation> mAnnotations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        mAnnotations = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnScrollListener(this);

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

        mChart.animateXY(2000, 2000);

        // dont forget to refresh the drawing
        mChart.invalidate();

        int id = getIntent().getExtras().getInt(SESSION_ID_TAG);

        new GraphLoadTask(this).execute(id);
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    private class GraphLoadTask extends AsyncTask<Integer, Void, Session> {

        private Context context;

        private Session s;

        public GraphLoadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Session doInBackground(Integer... params) {
            try {
                int id = params[0];

                DataPointSource dataSource = new DataPointSource(context);
                dataSource.open();
                s = dataSource.loadSessionData(id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(Session session) {
            GraphListViewAdpter adapter = new GraphListViewAdpter(session.getAnnotations());
            mListView.setAdapter(adapter);

            drawGraph(session);
        }

        private Session getFakeSession() {
            Session s = new Session(1, "Study", new SessionLocation("McBryde Hall", 5.3, 2.3));

            Random r = new Random(SystemClock.elapsedRealtime());
            for (int i = 0; i <= 60; i++) {
                float attention = r.nextInt(50) + 50;
                s.addDataPoint(1000 * 60 * i, attention);
            }
//
//            s.addAnnotation("Annotation 1", AttentionLevel.MEDIUM, 1000*60*5);
//            s.addAnnotation("Annotation 2", AttentionLevel.MEDIUM_HIGH, 1000*60*22);
//            s.addAnnotation("Annotation 3", AttentionLevel.HIGH, 1000 * 60 * 30);
//            s.addAnnotation("Annotation 4", AttentionLevel.HIGH, 1000 * 60 * 39);
//            s.addAnnotation("Annotation 5", AttentionLevel.MEDIUM_HIGH, 1000 * 60 * 49);
//            s.addAnnotation("Annotation 6", AttentionLevel.MEDIUM_LOW, 1000 * 60 * 55);

            return s;
        }

        private void loadSession(Session s) {

            List<Annotation> annotations = s.getAnnotations();
            int size = annotations.size();
            for (int i = 0; i < size; i++) {
                Annotation annotation = new Annotation(annotations.get(i).getAnnotation(), annotations.get(i).getAttentionLevel(), annotations.get(i).getTimeStamp());
                mAnnotations.add(annotation);
            }
        }
//
//            Annotation annotation1 = new Annotation("I am now coding at Mcb student lounge.", AttentionLevel.HIGH, 0);
//            Annotation annotation2 = new Annotation("I am now do HW at at Mcb student lounge.", AttentionLevel.MEDIUM, 0);
//            Annotation annotation3 = new Annotation("I am reading book at Mcb student lounge.", AttentionLevel.MEDIUM, 0);
//            Annotation annotation4 = new Annotation("I am talking with friends at Mcb student lounge.", AttentionLevel.LOW, 0);

        private void drawGraph(Session session) {

            List<EEGDataPoint> dataPoints = session.getEEGData();
            List<Annotation> annotations = session.getAnnotations();
            List<String> xLabels = getXLabels(dataPoints);

            CombinedData combinedData = new CombinedData(xLabels);

            LineData lineData = new LineData();
            BarData barData = new BarData();

            List<Entry> lineEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();

            int annotationIndex = 0;
            float sum = 0;
            for (int i = 0; i < xLabels.size(); i++) {
                EEGDataPoint dataPoint = dataPoints.get(i);

                float attention = dataPoint.attention;
                sum += attention;

                lineEntries.add(new Entry(attention, i));

                if (annotationIndex < annotations.size()) {
                    Annotation annotation = annotations.get(annotationIndex);
                    if (annotation.getTimeStamp() < dataPoint.timeStamp) {

                        float selfReport = annotation.getAttentionLevel().ordinal() * 25;

                        barEntries.add(new BarEntry(selfReport, i - 1));
                        annotationIndex++;
                    }
                }
            }

            float avg = sum / lineEntries.size();

            List<Entry> avgLineEntries = new ArrayList<>();
            avgLineEntries.add(new Entry(avg, 0));
            avgLineEntries.add(new Entry(avg, lineEntries.size()));

            LineDataSet dataPointSet = new LineDataSet(lineEntries, "EEG Data");
            dataPointSet.setDrawCubic(true);
            dataPointSet.setColor(Color.MAGENTA);
            dataPointSet.setCircleSize(3f);
            dataPointSet.setLineWidth(5f);
            dataPointSet.setDrawCircles(false);
            dataPointSet.setDrawValues(false);

            LineDataSet avgEegSet = new LineDataSet(avgLineEntries, "Avg EEG Data");
            avgEegSet.enableDashedLine(10, 10, 0);
            avgEegSet.setLineWidth(3f);
            avgEegSet.setColor(Color.BLACK);
            avgEegSet.setDrawValues(false);
            avgEegSet.setDrawCircles(false);

            BarDataSet barDataSet = new BarDataSet(barEntries, "Annotations");
            barDataSet.setColor(Color.GRAY);
            barDataSet.setHighLightColor(Color.WHITE);
            barDataSet.setDrawValues(false);

            lineData.addDataSet(dataPointSet);
            lineData.addDataSet(avgEegSet);
            barData.addDataSet(barDataSet);

            combinedData.setData(lineData);
            combinedData.setData(barData);

            mChart.setData(combinedData);
            mChart.invalidate();
        }

        private List<String> getXLabels(List<EEGDataPoint> dataPoints) {
            List<String> xLabels = new ArrayList<>();

            for (EEGDataPoint dataPoint : dataPoints) {
                Log.d("AHHHHHHHHH", dataPoint.timeStampFormatted());
                xLabels.add(dataPoint.timeStampFormatted());
            }
            return xLabels;
        }
    }
}

package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.SessionLocation;

public class GraphActivity extends Activity implements OnChartValueSelectedListener, AbsListView.OnScrollListener {

    public static final String SESSION_ID_TAG = "SessionId";

    private ListView mListView;
    private CombinedChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

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

        public GraphLoadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Session doInBackground(Integer... params) {

            Session s = null;

//            try {
//                DataPointSource dataSource = new DataPointSource(context);
//                dataSource.open();
//
//                int id = params[0];
//
//                s = dataSource.loadSessionData(id);
//            } catch (Exception e) {
//                // sqlite db locked - concurrency issue
//                System.out.println("Graph - sqlite db locked - concurrency issue ");
//                System.out.println(e.toString());
//            }

            // THIS WILL HAVE TO DO WHILE I DON'T HAVE ACCESS TO THE EEG
            s = getFakeSession();

            return s;
        }

        @Override
        protected void onPostExecute(Session session) {
            GraphListViewAdpter adapter = new GraphListViewAdpter(session.getAnnotations());
            mListView.setAdapter(adapter);

            drawGraph(session);
        }

        private Session getFakeSession() {
            Session s = new Session(1, new SessionLocation("McBryde Hall", 5.3, 2.3));

            s.addDataPoint(1, 75);
            s.addDataPoint(1000 * 60 * 1, 67);
            s.addDataPoint(1000 * 60 * 2, 50);
            s.addDataPoint(1000 * 60 * 3, 59);
            s.addDataPoint(1000 * 60 * 4, 82);
            s.addDataPoint(1000 * 60 * 5, 71);
            s.addDataPoint(1000 * 60 * 6, 62);
            s.addDataPoint(1000 * 60 * 7, 58);
            s.addDataPoint(1000 * 60 * 8, 76);
            s.addDataPoint(1000 * 60 * 9, 86);

            s.addAnnotation("Annotation 1", AttentionLevel.MEDIUM_HIGH, 5000);
            s.addAnnotation("Annotation 2", AttentionLevel.HIGH, 1000 * 60 * 6);
            s.addAnnotation("Annotation 3", AttentionLevel.MEDIUM_LOW, 1000 * 60 * 8);

            return s;
        }

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
            for (int i = 0; i < xLabels.size(); i++) {
                EEGDataPoint dataPoint = dataPoints.get(i);
                Annotation annotation = annotations.get(annotationIndex);

                lineEntries.add(new Entry(dataPoint.attention, i));

                if (annotation.getTimeStamp() < dataPoint.timeStamp) {

                    float attention = annotation.getAttentionLevel().ordinal() * 25;

                    barEntries.add(new BarEntry(attention, i-1));
                    annotationIndex++;
                }
            }

            LineDataSet lineDataSet = new LineDataSet(lineEntries, "EEG Data");
            lineDataSet.setColor(Color.MAGENTA);
            lineDataSet.setCircleSize(3f);
            lineDataSet.setLineWidth(5f);

            BarDataSet barDataSet = new BarDataSet(barEntries, "Annotations");
            barDataSet.setColor(Color.GRAY);
            barDataSet.setHighLightColor(Color.WHITE);
            barDataSet.setDrawValues(false);

            lineData.addDataSet(lineDataSet);
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

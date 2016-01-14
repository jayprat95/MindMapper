package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.ColorUtils;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.RecyclerViewPositionHelper;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.TimeUtils;

public class GraphActivity extends Activity implements OnChartValueSelectedListener {

    public static final String SESSION_ID_TAG = "SessionId";

    private CombinedChart mChart;

    private TextView title, time;

    private RoundCornerProgressBar averageFocus, overallFeel;

    private RecyclerView rv;
    private TextView noCommentsText;

    private List<Annotation> mAnnotations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mAnnotations = new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.graph_recycler_view);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerViewPositionHelper helper = RecyclerViewPositionHelper.createHelper(recyclerView);

                int i = helper.findFirstCompletelyVisibleItemPosition();

                if (i >= 0) {
                    ScatterDataSet sds = mChart.getScatterData().getDataSetByLabel("Annotations", false);

                    Entry e = sds.getYVals().get(i);

                    highlightIndex(sds, i, ColorUtils.getAttentionColor(e.getVal()));
                }
            }
        });

        noCommentsText = (TextView)findViewById(R.id.no_comments_text);

        title = (TextView) findViewById(R.id.graph_titlebar);
        title.setText("");

        time = (TextView)findViewById(R.id.graph_time);

        averageFocus = (RoundCornerProgressBar)findViewById(R.id.graph_average_focus);
        overallFeel = (RoundCornerProgressBar)findViewById(R.id.graph_overall_feel);

        mChart = (CombinedChart) findViewById(R.id.chart);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");

        // enable value highlighting
        mChart.setHighlightIndicatorEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        mChart.getLegend().setEnabled(false);

        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setLabelCount(2);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setValueFormatter(new IntegerAxisValueFormatter());
        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMaxValue(100);

        int id = getIntent().getExtras().getInt(SESSION_ID_TAG);

        new GraphLoadTask(this).execute(id);
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        Object entryData = entry.getData();
        if (entryData instanceof Integer) {
            int a = (int)entryData;

            ScatterDataSet sds = mChart.getScatterData().getDataSetByLabel("Annotations", false);

            LinearLayoutManager llm  = (LinearLayoutManager)rv.getLayoutManager();

            llm.scrollToPositionWithOffset(a, 0);

            int color = ColorUtils.getAttentionColor(entry.getVal());

            highlightIndex(sds, a, color);
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private void highlightIndex(ScatterDataSet set, int index, int highlightColor) {

        List<Integer> colors = new ArrayList<>(set.getEntryCount());

        for (int i = 0; i < set.getEntryCount(); i++) {
            colors.add(i, Color.parseColor("#44BECDD7"));
        }

        colors.set(index, highlightColor);

        set.setColors(colors);

        mChart.invalidate();
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

            // Setting title
            title.setText(session.getActivityName() + " at " + session.getLocation().getName());

            // Setting time
            time.setText(TimeUtils.getSessionTimeFormatted(session));

            // Setting average focus bar
            float eegAverage = session.getEEGAverage();
            averageFocus.setProgress(eegAverage);
            averageFocus.setProgressColor(ColorUtils.getAttentionColor(eegAverage));

            // Setting average
            float selfReportAverage = (float)session.getOverallIFeltScore();
            overallFeel.setProgress(selfReportAverage);
            overallFeel.setProgressColor(ColorUtils.getAttentionColor(selfReportAverage));

            AnnotationListAdapter adapter = new AnnotationListAdapter(session.getAnnotations());

            LinearLayoutManager llm = new LinearLayoutManager(GraphActivity.this);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);

            rv.setLayoutManager(llm);
            rv.setAdapter(adapter);

            if (session.getAnnotations().isEmpty()) {
                rv.setVisibility(View.GONE);
                noCommentsText.setVisibility(View.VISIBLE);
            }

            drawGraph(session);
        }

        private void drawGraph(Session session) {

            List<EEGDataPoint> dataPoints = session.getEEGData();
            List<Annotation> annotations = session.getAnnotations();
            List<String> xLabels = getXLabels(dataPoints);

            CombinedData combinedData = new CombinedData(xLabels);

            LineData lineData = new LineData();
            ScatterData scatterData = new ScatterData();

            List<Entry> lineEntries = new ArrayList<>();
            List<Entry> scatterEntries = new ArrayList<>();

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

                        float selfReport = (float)annotation.getAttentionLevel();

                        Entry e = new Entry(selfReport, i-1, annotationIndex);

                        scatterEntries.add(e);
                        annotationIndex++;
                    }
                }
            }

            float avg = sum / lineEntries.size();

            LineDataSet dataPointSet = new LineDataSet(lineEntries, "EEG Data");
            dataPointSet.setDrawCubic(true);
            dataPointSet.setColor(Color.parseColor("#B5D9AF"));
            dataPointSet.setFillColor(Color.parseColor("#B5D9AF"));
            dataPointSet.setFillAlpha(255);
            dataPointSet.setLineWidth(0f);
            dataPointSet.setDrawCircles(false);
            dataPointSet.setDrawValues(false);
            dataPointSet.setDrawFilled(true);

            ScatterDataSet scatterDataSet = new ScatterDataSet(scatterEntries, "Annotations");
            scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            scatterDataSet.setScatterShapeSize(14f);
            scatterDataSet.setColor(Color.parseColor("#BECDD7"));
            scatterDataSet.setHighLightColor(Color.GREEN);
            scatterDataSet.setDrawValues(false);

            lineData.addDataSet(dataPointSet);
            scatterData.addDataSet(scatterDataSet);

            combinedData.setData(lineData);
            combinedData.setData(scatterData);

            LimitLine focusAvg = new LimitLine(avg, "Focus Average");
            focusAvg.setLineColor(Color.parseColor("#778490"));
            focusAvg.setLineWidth(2f);
            focusAvg.setTextColor(Color.parseColor("#778490"));
            focusAvg.setTextSize(12f);
            focusAvg.setLabelPosition(LimitLine.LimitLabelPosition.POS_LEFT);

            LimitLine overallFelt = new LimitLine((float)session.getOverallIFeltScore(), "Overall I Felt");
            overallFelt.setLineColor(Color.parseColor("#778490"));
            overallFelt.setLineWidth(2f);
            overallFelt.setTextColor(Color.parseColor("#778490"));
            overallFelt.setTextSize(12f);
            overallFelt.setLabelPosition(LimitLine.LimitLabelPosition.POS_LEFT);

            mChart.getAxisLeft().addLimitLine(focusAvg);
            mChart.getAxisLeft().addLimitLine(overallFelt);
            mChart.setData(combinedData);
            mChart.invalidate();
        }

        private List<String> getXLabels(List<EEGDataPoint> dataPoints) {
            List<String> xLabels = new ArrayList<>();

            for (EEGDataPoint dataPoint : dataPoints) {
                xLabels.add(dataPoint.timeStampFormatted());
            }
            return xLabels;
        }
    }
}

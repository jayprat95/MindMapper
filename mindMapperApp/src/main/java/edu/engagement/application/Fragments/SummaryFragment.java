package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.engagement.application.App;
import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.EventSummary;

import edu.engagement.application.GraphActivity;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.RVAdapter;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.Session;

public class SummaryFragment extends Fragment {

    private MainActivity activity;

    private RecyclerView rv;
    private List<EventSummary> eventSummaryList;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.card_recycle_view_activity, container, false);
        final FragmentActivity fragActivity = getActivity();

        // random access needed, so arraylist is better choice
        eventSummaryList = new ArrayList<>();

        rv = (RecyclerView) view.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(fragActivity);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        rv.setAdapter(new RVAdapter(eventSummaryList));

        new SummaryLoadTask(activity.getApplicationContext()).execute();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class SummaryLoadTask extends AsyncTask<Void, EventSummary, Void> {

        private Context context;

        public SummaryLoadTask(Context context) {
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
                loadPoints(dataSource);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("AAAAAAAAAAAAA", String.valueOf(eventSummaryList.size()));
        }

        @Override
        protected void onProgressUpdate(EventSummary... values) {
            EventSummary summary = values[0];
            eventSummaryList.add(summary);

            rv.getAdapter().notifyDataSetChanged();
        }

        private void loadPoints(DataPointSource dbSource) {

            // time range of day and month

            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);

            // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
            int month = (c.get(Calendar.MONTH) + 1);
            List<Session> sessions = dbSource.getSessionsInTimeRange(day);

            Log.d(App.NAME, "Sessions returned: " + sessions.size());

            for (Session s : sessions) {
                Log.d(App.NAME, "Sanity Check");
                int id = s.getId();
                String locationName = s.getLocation().getName();
                Log.d(App.NAME, locationName);


                // Getting start and stop times
                List<EEGDataPoint> data = s.getEEGData();
                int dataLength = data.size();
                long startTime = data.get(0).timeStamp;
                long stopTime = data.get(dataLength-1).timeStamp;
                float avgEEG = s.getEEGAverage();
                AttentionLevel avgSelf = s.getSelfReportAverage();

                publishProgress(new EventSummary(id, locationName, startTime, stopTime, avgSelf, avgEEG));
            }


        }
    }
}

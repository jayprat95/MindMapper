package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.util.Random;

import edu.engagement.application.App;
import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.EventSummary;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.SummaryCardAdapter;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.SessionLocation;

public class SummaryFragment extends Fragment {

    private MainActivity activity;

    private RecyclerView rv;
    private List<Session> sessionList;


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
        sessionList = new ArrayList<>();

        rv = (RecyclerView) view.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(fragActivity);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        rv.setAdapter(new SummaryCardAdapter(sessionList));

        new SummaryLoadTask(activity.getApplicationContext()).execute();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class SummaryLoadTask extends AsyncTask<Void, Session, Void> {

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

        }

        @Override
        protected void onProgressUpdate(Session... values) {
            sessionList.add(values[0]);

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

            sessions.add(getFakeSession("McBryde Hall", "Studying", 25));
            sessions.add(getFakeSession("Torgerson Hall", "Reading", 48));
            sessions.add(getFakeSession("Home", "Programming", 72));
            sessions.add(getFakeSession("Library", "Doing homework", 72));
            sessions.add(getFakeSession("McBryde Hall", "Research", 72));

            for (Session s : sessions) {
                publishProgress(s);
            }
        }

        private Session getFakeSession(String locName, String des, int mins) {
            Session s = new Session(1, des, new SessionLocation(locName, 5.3, 2.3));

            Random r = new Random(SystemClock.elapsedRealtime());
            for (int i = 0; i <= mins; i++) {
                float attention = r.nextInt(50) + 50;
                s.addDataPoint(1000 * 60 * i, attention);
            }

            s.addAnnotation("Annotation 1", r.nextInt(100), 1000 * 60 * 5);
            s.addAnnotation("Annotation 2", r.nextInt(100), 1000 * 60 * 22);
            s.addAnnotation("Annotation 3", r.nextInt(100), 1000 * 60 * 30);
            s.addAnnotation("Annotation 4", r.nextInt(100), 1000 * 60 * 39);
            s.addAnnotation("Annotation 5", r.nextInt(100), 1000 * 60 * 49);
            s.addAnnotation("Annotation 6", r.nextInt(100), 1000 * 60 * 55);

            return s;
        }
    }
}

package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.EventSummary;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.RVAdapter;

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


    private class SummaryLoadTask extends AsyncTask<Void, Void, Void> {

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
                // sqlite db locked - concurrency issue
                System.out
                        .println("Cardview - sqlite db locked - concurrency issue ");
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            rv.getAdapter().notifyDataSetChanged();
        }

        private void loadPoints(DataPointSource dbSource) {

            //List<double[]> results = dbSource.getMapDataset();
            //List<EventSummary> events = new ArrayList<>(results.size());

            EventSummary card = new EventSummary(1, "McBryde Hall", 1444482000000L, 1444486450000L, AttentionLevel.MEDIUM, 63);
            EventSummary card2 = new EventSummary(2, "Torgerson Hall", 1444490524000L, 1444492984000L, AttentionLevel.MEDIUM_HIGH, 71);
            EventSummary card3 = new EventSummary(3, "Newman Library", 1444496164000L, 1444498204000L, AttentionLevel.HIGH, 83);
            EventSummary card4 = new EventSummary(4, "Chipotle", 1444502704000L, 1444504564000L, AttentionLevel.MEDIUM_LOW, 38);
            EventSummary card5 = new EventSummary(5, "McBryde Hall", 1444509004000L, 1444510864000L, AttentionLevel.MEDIUM, 54);
            EventSummary card6 = new EventSummary(6, "Terrace View Apartments", 1444516564000L, 1444519324000L, AttentionLevel.LOW, 15);


            eventSummaryList.add(card);
            eventSummaryList.add(card2);
            eventSummaryList.add(card3);
            eventSummaryList.add(card4);
            eventSummaryList.add(card5);
            eventSummaryList.add(card6);
        }
    }
}

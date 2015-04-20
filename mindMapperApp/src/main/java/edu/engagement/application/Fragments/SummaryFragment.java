package edu.engagement.application.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.LinkedList;

import edu.engagement.application.CardData;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.RVAdapter;
import edu.engagement.application.fabbutton.FloatingActionButton;

public class SummaryFragment extends Fragment{

    private MainActivity activity;

    private RecyclerView rv;
    private List<CardData> cardDataList;


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

        cardDataList = new LinkedList<CardData>();
        /* ---------------------- load data begin ---------------------- */
        try {
            DataPointSource dataSource = new DataPointSource(this.getActivity()
                    .getApplicationContext());
            dataSource.open();

            // Load values to cards
            loadPoints(dataSource);

        } catch (Exception e) {
            // sqlite db locked - concurrency issue
            System.out
                    .println("Cardview - sqlite db locked - concurrency issue ");
            System.out.println(e.toString());
        }
        /* ---------------------- load data end ---------------------- */

        rv = (RecyclerView) view.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(fragActivity);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        //initializeData();
        initializeAdapter();

        return view;
    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(cardDataList);
        rv.setAdapter(adapter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void loadPoints(DataPointSource dpSource) {

        List<double[]> results = dpSource.getMapDataset();

        // TODO remove these later
        /** --------------- Test points for card View begin------------------- **/
        double[] testPoint = new double[4];
        testPoint[0] = 0;
        testPoint[1] = 1;
        testPoint[2] = 37.230632; // Latitude
        testPoint[3] = -80.421675; // Longitude

        results.add(testPoint);

        /** --------------- Test points for card View end------------------- **/

        CardData card = new CardData();
//        CardData card2 = new CardData(5, 10, "1034", "32423");
        double mean = getMean(results, 1);
        double variance = getVariance(results, 1, mean);
        card.setAverage(mean);
        card.setVariance(variance);

        cardDataList.add(card);
    }

    /**
     * Get mean
     *
     * @param data       data from DB
     * @param indexOfAtt the column index of attention in each row
     * @return mean
     */
    double getMean(List<double[]> data, int indexOfAtt) {
        double sum = 0.0;
        // For each row of data, retrieve the attention from indexOfAtt and calculate desired value
        for (double[] a : data)
            sum += a[indexOfAtt];
        return sum / data.size();
    }

    /**
     * Get variance
     *
     * @param data       data from DB
     * @param indexOfAtt the column index of attention in each row
     * @return Variance
     */
    double getVariance(List<double[]> data, int indexOfAtt, double mean) {
        double temp = 0;
        for (double[] a : data)
            temp += (mean - a[indexOfAtt]) * (mean - a[indexOfAtt]);
        return temp / data.size();
    }

}

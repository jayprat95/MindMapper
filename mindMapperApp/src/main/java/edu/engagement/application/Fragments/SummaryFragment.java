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

import java.util.List;
import java.util.LinkedList;

import edu.engagement.application.CardData;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.RVAdapter;

public class SummaryFragment extends Fragment {

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


		CardData card = new CardData(5, 10, "1034", "32423");
		CardData card2 = new CardData(5, 10, "1034", "32423");
		cardDataList = new LinkedList<CardData>();
		cardDataList.add(card);
		cardDataList.add(card2);
        rv= (RecyclerView)view.findViewById(R.id.rv);


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
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
	
	
	
	
	
}

package edu.engagement.application.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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

		
		//setContentView(R.layout.recyclerview_activity);

        rv= (RecyclerView)this.getActivity().findViewById(R.id.rv);


        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
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

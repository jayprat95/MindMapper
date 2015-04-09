package edu.engagement.application.Fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.thrift.AllDataset;

public class DatabaseFragment extends Fragment implements OnClickListener{
	
	private MainActivity activity;
	
	private TextView rowText;
	private ArrayList<AllDataset> allDatasets = new ArrayList();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.database, container, false);
                
        // Set up initial screen layout
        rowText = (TextView) view.findViewById(R.id.rowData);
        
//        System.out.println("attention text view initialized");
//        drawingImageView = (ImageView) view.findViewById(R.id.attentionCircle);
        
        try{
			DataPointSource dataSource = new DataPointSource(this.getActivity()
					.getApplicationContext());
			dataSource.open();
			System.out.println("Trying to load data points ");
			loadDatabasePoints(dataSource);
			
			System.out.println("Size of AllDatasets: " + allDatasets.size());
			
			ListView list = (ListView)view.findViewById(R.id.databaseList);
			
			System.out.println("About to set the list items");

			ArrayAdapter<AllDataset> mArrayAdapter = new ArrayAdapter<AllDataset>(this.activity, android.R.layout.simple_list_item_1,/* android.R.id.text1,*/ allDatasets);
			list.setAdapter(mArrayAdapter);

		}catch(Exception e){
			// sqlite db locked - concurrency issue
			System.out.println("Database Fragment - sqlite db locked - concurrency issue ");
			System.out.println(e.getMessage());
		}
        
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
	public void onClick(View view) {
		switch (view.getId()) {
//		case R.id.b_start:
//			start();
//			break;
//		case R.id.b_circle:
//			clicks++;
//			delay = true;
//			hideCircle();
//			break;
		}
		
	}
    
    private void loadDatabasePoints(DataPointSource dpSource){
		System.out.println("loadDatabasePoints");

    	List<double[]> results = dpSource.getMaxDataset();
			
		for(int i=0; i < results.size(); i++){
			double[] data = results.get(i);
			AllDataset set = new AllDataset(data[0], data[1], data[2], data[3]);//, data[4]);
			allDatasets.add(set);
			
			System.out.println("\nFiltered Max Dataset results: " + i);
			for(int j=0; j < data.length; j++){
				System.out.print(data[j] + ", ");
			}
		}
		
		
		for(double[] pointArray : results)
		{
			/*
			 * 0 - Timestamp
			 * 1 - Latitude
			 * 2 - Longitude
			 * 3 - Attention
			 * 4 - Count
			 */
//			if(pointArray[4] == 0)
//			{
//				break;
//			}
//			
//			
//			Log.d("loadPoints",pointArray[1] + ", "+pointArray[2]+" Radius: "+radius);
//			addPoint(pointArray[1], pointArray[2], radius, getEngagementColor(pointArray[3], 100));
		}
    }
}

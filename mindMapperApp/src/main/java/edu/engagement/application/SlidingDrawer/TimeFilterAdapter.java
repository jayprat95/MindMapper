package edu.engagement.application.SlidingDrawer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeFilterAdapter<String> extends ArrayAdapter<String>{


	public TimeFilterAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	}
	
	public TimeFilterAdapter(Context context, int resource, String[] items) {
	    super(context, resource, items);
	}
	
	public TimeFilterAdapter(Context context, int resource, ArrayList<String> items) {
	    super(context, resource, items);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 
	    String p = getItem(position);

        View view = super.getView(position, convertView, parent);
        TextView text = (TextView)view.findViewById(android.R.id.text1);
        text.setTextColor(Color.WHITE);      

        return view;
	}
	 @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent)
	    {
	        View view = super.getView(position, convertView, parent);
	        
            TextView text = (TextView)view.findViewById(android.R.id.text1);
            text.setTextColor(Color.RED);       

	        return view;

	    }
}

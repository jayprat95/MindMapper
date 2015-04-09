package edu.engagement.application.SlidingDrawer;

import java.util.ArrayList;

import edu.engagement.application.R;
import edu.engagement.application.R.id;
import edu.engagement.application.R.layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlidingDrawerAdapter<String> extends ArrayAdapter<String> {
	
	public SlidingDrawerAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	}
	
	public SlidingDrawerAdapter(Context context, int resource, ArrayList<String> items) {
	    super(context, resource, items);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 
	    String p = getItem(position);
	    
	    LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        convertView = vi.inflate(R.layout.drawer_list_header, parent, false);
        
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        switch(position){
        	case 0:	//Connect
                icon.setImageResource(R.drawable.ic_bt);
        		break;
        	case 1:	//Realtime
                icon.setImageResource(R.drawable.ic_time);
                break;
        	case 2:	//Reflection
                icon.setImageResource(R.drawable.ic_meditation);
                break;
        	case 3:	//Knowledge
                icon.setImageResource(R.drawable.ic_lightbulb_shine);
                break;
        	default:
                icon.setImageResource(R.drawable.ic_bargraph_dark);
                break;

        }
        
    	TextView title = (TextView) convertView.findViewById(R.id.header);
    	title.setText(p.toString());
	    
//	    if(p.equals("Review") || p.equals("Filters")){ 							// headers
//	        LayoutInflater vi;
//	        vi = LayoutInflater.from(getContext());
//	        convertView = vi.inflate(R.layout.drawer_list_header, parent, false);
//	        
//	    	TextView title = (TextView) convertView.findViewById(R.id.header);
//	    	title.setText(p.toString());
//	    	
//	    }else if(p.equals("Map") || p.equals("Graph")){		// selectable items
//
//	        LayoutInflater vi;
//	        vi = LayoutInflater.from(getContext());
//	        convertView = vi.inflate(R.layout.drawer_list_item, parent, false);
//	        
//	    	TextView title = (TextView) convertView.findViewById(R.id.drawer_item);
//	    	title.setText(p.toString());
//	    	
//	    }else if(p.equals("High Engagement") || p.equals("Low Engagement")){		// selectable items
//
//	        LayoutInflater vi;
//	        vi = LayoutInflater.from(getContext());
//	        convertView = vi.inflate(R.layout.drawer_list_item_selectable, parent, false);
//	        
//	    	TextView title = (TextView) convertView.findViewById(R.id.drawer_item);
//	    	title.setText(p.toString());
//	    	
//	    }else if(p.equals("Time")){												// time selector
//
//	        LayoutInflater vi;
//	        vi = LayoutInflater.from(getContext());
//	        convertView = vi.inflate(R.layout.drawer_list_time, parent, false);
//
//	        SlidingDrawer.initSpinner(convertView);
//	    	
//	    }else if(p.equals("Apply") || p.equals("Reset")){ 						// buttons
//
//	        LayoutInflater vi;
//	        vi = LayoutInflater.from(getContext());
//	        convertView = vi.inflate(R.layout.drawer_list_button, parent, false);
//	        
//	    	TextView button = (TextView) convertView.findViewById(R.id.button);
//	    	button.setText(p.toString());
//	    	
//	    }
	    return convertView;
	}
}

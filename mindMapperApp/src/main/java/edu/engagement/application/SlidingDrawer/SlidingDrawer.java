package edu.engagement.application.SlidingDrawer;

import java.util.ArrayList;
import java.util.Arrays;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.R.array;
import edu.engagement.application.R.drawable;
import edu.engagement.application.R.id;
import edu.engagement.application.R.string;
import edu.engagement.application.Database.DataFilter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

public class SlidingDrawer{
	private static MainActivity listener;
    private ActionBarDrawerToggle mDrawerToggle;
	
	private ArrayList<String> menuItems;
    private DrawerLayout mDrawerLayout;
    private static ListView mDrawerList;
    private static Spinner spinner;
    
    public SlidingDrawer(MainActivity main){
    	listener = main;
		 
        menuItems = new ArrayList<String>();
//        menuItems.addAll(Arrays.asList(new String[]{"Review", "Map", "Graph", "Filters", 
//        		"High Engagement", "Low Engagement", "Time", "Apply", "Reset"}));
        
        menuItems.addAll(Arrays.asList(new String[]{"Connect", "Realtime", "Reflection", "Knowledge", "Summary"/*, "Database"*/}));


       // mDrawerLayout = (DrawerLayout) listener.findViewById(R.id.main_layout);
        //mDrawerList = (ListView) listener.findViewById(R.id.left_drawer);
 
        // Set the adapter for the list view
        mDrawerList.setAdapter(new SlidingDrawerAdapter<String>(listener,
            	android.R.layout.simple_list_item_1,
            	menuItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
 
        mDrawerToggle = new ActionBarDrawerToggle(
        		listener,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.hack,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
 
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            	listener.getActionBar().setTitle(listener.getTitle());
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            	listener.getActionBar().setTitle(listener.getTitle());
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    public static void initSpinner(View parent){
        
        spinner = (Spinner)parent.findViewById(R.id.spinner1);
        String [] spin_array = listener.getResources().getStringArray(R.array.time_filter_presets); 
        spinner.setAdapter(new TimeFilterAdapter<String>(listener, 
        		android.R.layout.simple_list_item_1, spin_array));
        spinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());
    }
	 
    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        switch(position){
        case 0:					// Connect
        	mDrawerList.setItemChecked(position, true);
            listener.setTitle(menuItems.get(position));
            //change this to connection and calibration page
            listener.switchToFragment(listener.BASELINE_TAG);
//            listener.connectToMindwave();
            mDrawerLayout.closeDrawer(mDrawerList);
        	break;
        case 1:					// Realtime
            mDrawerList.setItemChecked(position, true);
            listener.setTitle(menuItems.get(position));
            listener.switchToFragment(listener.REAL_TIME_TAG);
            mDrawerLayout.closeDrawer(mDrawerList);
        	break;
        case 2:					// Reflection (Map & Graph)
            mDrawerList.setItemChecked(position, true);
            listener.setTitle(menuItems.get(position));
            listener.switchToFragment(listener.MAP_TAG);
            //GO TO GRAPH
            //listener.switchToFragment(listener.GRAPH_LIST_TAG);
            mDrawerLayout.closeDrawer(mDrawerList);
        	break;
        case 3:					// Knowledge (Info, About, How To)
        	//temporarily change this to graph fragment for testing
        	mDrawerList.setItemChecked(position, true);
            listener.setTitle(menuItems.get(position));
            listener.switchToFragment(listener.REFLECTION_GRAPH_TAG);
            mDrawerLayout.closeDrawer(mDrawerList);
        	break;
//        case 4:					//Database testing
//        	mDrawerList.setItemChecked(position, true);
//            listener.setTitle(menuItems.get(position));
//            listener.switchToFragment(listener.DATABASE_TAG);
//            mDrawerLayout.closeDrawer(mDrawerList);
//            break;
        case 4:
        	mDrawerList.setItemChecked(position, true);
            listener.setTitle(menuItems.get(position));
            listener.switchToFragment(listener.SUMMARY_TAG)	;
            mDrawerLayout.closeDrawer(mDrawerList);
        default:
        	break;
//        case 4:					// High Engagement
//        	//DataFilter.setFilter(DataFilter.HIGH_ENGAGEMENT);
////        	listener.redrawGraphs();					// TODO: remove when Apply/Reset are functional
////            mDrawerLayout.closeDrawer(mDrawerList);		// TODO: remove when Apply/Reset are functional
//        	break;
//        case 5:					// Low Engagement
////        	DataFilter.setFilter(DataFilter.LOW_ENGAGEMENT);
////        	listener.redrawGraphs();					// TODO: remove when Apply/Reset are functional
////            mDrawerLayout.closeDrawer(mDrawerList);		// TODO: remove when Apply/Reset are functional
//        	break;
//        case 6:					// Time
//        	break;
//        case 7:					// Apply
////            mDrawerList.setItemChecked(position, true);
////            listener.setTitle(menuItems.get(position));
////            mDrawerLayout.closeDrawer(mDrawerList);
//        	break;
//        case 8:					// Reset
////            mDrawerList.setItemChecked(position, true);
////            listener.setTitle(menuItems.get(position));
////            mDrawerLayout.closeDrawer(mDrawerList);		// TODO: needs lastFilter applied cached
//        	break;
        }
    }
        
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    public ActionBarDrawerToggle getDrawerToggle(){
    	return mDrawerToggle;
    }
}

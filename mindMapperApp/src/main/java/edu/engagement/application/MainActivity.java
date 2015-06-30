package edu.engagement.application;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;


//import zephyr.android.HxMBT.BTClient;
//import zephyr.android.HxMBT.ZephyrProtocol;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import javax.xml.datatype.Duration;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.Fragments.BaselineFragment;
import edu.engagement.application.Fragments.DatabaseFragment;
import edu.engagement.application.Fragments.GraphListFragment;
import edu.engagement.application.Fragments.MapFrag;
import edu.engagement.application.Fragments.RangeGraphFragment;
import edu.engagement.application.Fragments.RealTimeDataFragment;
import edu.engagement.application.Fragments.ReflectionGraphFragment;
import edu.engagement.application.Fragments.SummaryFragment;
import edu.engagement.application.Fragments.XYGraphFragment;
import edu.engagement.application.SlidingTab.*;

/**
 * MainActivity June 16
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener{


    public static final String BASELINE_AVG_KEY = "avg";
    private static final int PORT = 7911;
    public final String MAP_TAG = "MAP_FRAGMENT";
    public final String REAL_TIME_TAG = "REAL_TIME_FRAGMENT";
    public final String GRAPH_LIST_TAG = "GRAPH_LIST_FRAGMENT";
    public final String XY_GRAPH_TAG = "XY_GRAPH_FRAGMENT";
    public final String RANGE_GRAPH_TAG = "RANGE_GRAPH_FRAGMENT";
    public final String DATABASE_TAG = "DATABASE_FRAGMENT";
    public final String REFLECTION_GRAPH_TAG = "REFLECTION_GRAPH_FRAGMENT";
    public final String SUMMARY_TAG = "SUMMARY_FRAGMENT";
    /* Having issues with android
     * Packaging all functions here for now so I can change easy if a mistake is found
     */
    public final String BASELINE_TAG = "BASELINE_FRAGMENT";
    final boolean rawEnabled = true;
    ActionMode mActionMode = null;
    /**
     * Sliding Tabs variables
     */
    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    //private SlidingDrawer drawer;
    private CharSequence Titles[] = {"Map", "Graph", "Summary"};
    private int Numboftabs = 3;
    // location from place picker
    private String location = "";
    /**
     * Fab Button variables
     */
    private TextView fab;
    private boolean fabClicked;
    private boolean serviceStarted;
    /**
     * Container for the annotations view
     */
    private FrameLayout frameLayout;
    private int gpsKey = 0;
    private CharSequence mTitle;
    private MapFrag mapFragment;
    private RealTimeDataFragment realFragment;
    private GraphListFragment graphFragment;
    private XYGraphFragment xyGraphFragment;
    private RangeGraphFragment rangeGraphFragment;
    private DatabaseFragment databaseFragment;
    private ReflectionGraphFragment reflectionGraphFragment;
    private SummaryFragment summaryFragment;
    private BaselineFragment baselineFragment;
    private double baselineTotal;
    private double baselineNum;
    private boolean baselineMode = false;
    //testing why queue is only 1 item
    private int tempCounter = 0;
    private boolean realTimeInstantiated;
    //if the realtime fragment is running. need to change this. sloppy way to know current fragment
    private boolean realTime = false;
    private Queue<Integer> recentAttentionLevels = new LinkedList<Integer>();
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.reflection, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after
        // onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

//
            int itemId = item.getItemId();
            if (itemId == R.id.action_connect) {
                return true;
            } else {
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //mActionMode = null;
            MainActivity.this.onBackPressed();
        }
    };

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    // Method to start the service
    public void startService() {

        if (!serviceStarted) {
            serviceStarted = true;


            startService(new Intent(getBaseContext(), MindwaveService.class));

        }

    }

    /**
     * Get the current location from place picker
     *
     * @return locatioin name
     */
    public String getLocation() {
        return this.location;
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), MindwaveService.class));
    }

    public void startBaselineMode() {
        baselineTotal = 0;
        baselineNum = 0;
        baselineMode = true;
    }

    public void exitBaselineMode() {
        baselineMode = false;
        storeBaseline((float) (baselineTotal / baselineNum));
        baselineFragment.setScore(Double.toString(baselineTotal / baselineNum));
    }

    // Store baseline in persistent storage
    public void storeBaseline(float avg) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putFloat(BASELINE_AVG_KEY, (float) 10.0);
        editor.commit();
        System.out.println("Storing baseline value: " + baselineTotal / baselineNum);
    }

    // Read baseline from persistent storage
    public float readBaseline() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        float avg = prefs.getFloat(BASELINE_AVG_KEY, -1);
        System.out.println("Received baseline value: " + avg);
        return avg;
    }

    // Method is used to reset the baseline
    // Used when user wants to re-establish their baseline
    public void resetBaseline() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putFloat(BASELINE_AVG_KEY, (float) -1);
        editor.commit();
    }

    /* END */

    public void setAttentionText(int attention) {
        recentAttentionLevels.add(new Integer(attention));
        recentAttentionLevels.add(new Integer(tempCounter));
        System.out.println("RecentAttentionLevels: " + recentAttentionLevels.toString());
        System.out.println("RecentAttentionLevels size: " + recentAttentionLevels.size());
        if (recentAttentionLevels.size() > 5) {
            recentAttentionLevels.remove();
        }
        if (realTime && realFragment != null) {
            Queue<Integer> copyAttention = recentAttentionLevels;
//			realFragment.setAttention(recentAttentionLevels);
            realFragment.setAttention(copyAttention);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        realTimeInstantiated = false;


        /** Sliding Tabs Shit */

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.sliding_tab_tool_bar);
        //setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        /** End of Sliding Tabs Shit */

        /** Fab Button Shit */
        fab = (TextView) findViewById(R.id.fabButton);
        fabClicked = false;
        serviceStarted = false;
        final Activity thisActivity = this; // Use this for the toast
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabClicked == false) {
                    fabClicked = true;
                    fab.setText("Annotation");
                    Toast.makeText(thisActivity, "Connecting to Mind wave device", Toast.LENGTH_LONG).show();
                }
                // Move to real time fragment
//                pager.setCurrentItem(1, true);
                //switchToFragment("REAL_TIME_FRAGMENT");
                changeState(state.ANNOTATION_STATE);

                /** If Jayanth wants to work on the real time eeg data uncommon this line to start the service */
//                startService();
            }
        });
        /** End of Fab Button Shit */


        System.out.println("Opened data source (DB)");

        initActionBar();

        /** Start of loading debug dataset from src/main/assets and populate GPS data**/
        // TODO: remove these after finishing the project
        DataPointSource dataSource = new DataPointSource(this);
        dataSource.open();
        if (dataSource.doWeNeedMoreDebugData()) {
            try {
                dataSource.loadDebugAttentionDataSets(getResources().getAssets().open("EEG_table_attention_data.csv"));
                dataSource.loadDebugGPSDataSets(getResources().getAssets().open("EEG_table_gps_data.csv"));
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(DataPointSource.class.getName(), "!!!IO exception in mainActivity about the csv file");
            }
        }
        /** End of loading debug dataset from src/main/assets and pupulate GPS data**/


        //	drawer = new SlidingDrawer(this);

        //switchToFragment(MAP_TAG);
        //switchToFragment(DATABASE_TAG);
    }

    /**
     * Changes the apps's gobal state, and all
     * relevant GUI will change to reflect on the state*
     *
     * @param someState a new state
     */
    public void changeState(state someState) {

        if (someState == state.ANNOTATION_STATE) {
            toolbar.setVisibility(View.INVISIBLE);
            pager.setVisibility(View.INVISIBLE);
            tabs.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);

            if (realTimeInstantiated == false) {
                switchToFragment("REAL_TIME_FRAGMENT");
                realTimeInstantiated = true;
            }


        } else {
            toolbar.setVisibility(View.VISIBLE);
            pager.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);

        }

    }

    private void initActionBar() {
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Creates a contextual action bar that allows the user to connect
        mActionMode = startActionMode(mActionModeCallback);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //only do this if it is the reflection view
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //drawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //drawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        if (drawer.getDrawerToggle().onOptionsItemSelected(item)) {
//            return true;
//        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void switchToFragment(String FRAGMENT_TAG) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        if (getFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
            realTime = false;
            if (FRAGMENT_TAG.equals(REAL_TIME_TAG)) {
                if (realFragment == null)
                    realFragment = new RealTimeDataFragment();
                fragmentTransaction.replace((R.id.content_frame), realFragment,
                        REAL_TIME_TAG).commit();
            }
        }
//
//			} else if (FRAGMENT_TAG.equals(REAL_TIME_TAG)) {
//                if (realFragment == null)
//                    realFragment = new RealTimeDataFragment();
//                fragmentTransaction.replace(R.id.content_frame, realFragment,
//						REAL_TIME_TAG).commit();
//				realTime = true;
//			} else if (FRAGMENT_TAG.equals(GRAPH_LIST_TAG)) {
//				graphFragment = new GraphListFragment();
//				fragmentTransaction.replace(R.id.content_frame, graphFragment,
//						GRAPH_LIST_TAG).commit();
//
//				xyGraphFragment = new XYGraphFragment();
//				fragmentTransaction = fragmentManager.beginTransaction();
//				fragmentTransaction.replace(R.id.fragment1, xyGraphFragment,
//						XY_GRAPH_TAG).commit();
//
//				rangeGraphFragment = new RangeGraphFragment();
//				fragmentTransaction = fragmentManager.beginTransaction();
//				fragmentTransaction.replace(R.id.fragment2, rangeGraphFragment,
//						RANGE_GRAPH_TAG).commit();
//
//			} else if (FRAGMENT_TAG.equals(BASELINE_TAG)) {
//				if (baselineFragment == null)
//					baselineFragment = new BaselineFragment();
//				fragmentTransaction.replace(R.id.content_frame,
//						baselineFragment, BASELINE_TAG).commit();
//			} else if (FRAGMENT_TAG.equals(DATABASE_TAG)) {
//				if (databaseFragment == null)
//					databaseFragment = new DatabaseFragment();
//				fragmentTransaction.replace(R.id.content_frame,
//						databaseFragment, DATABASE_TAG).commit();
//			} else if (FRAGMENT_TAG.equals(REFLECTION_GRAPH_TAG)) {
//				if (reflectionGraphFragment == null)
//					reflectionGraphFragment = new ReflectionGraphFragment();
//				fragmentTransaction.replace(R.id.content_frame,
//						reflectionGraphFragment, REFLECTION_GRAPH_TAG).commit();
//			} else if (FRAGMENT_TAG.equals(SUMMARY_TAG)) {
//				if (summaryFragment == null)
//					summaryFragment = new SummaryFragment();
//				fragmentTransaction.replace(R.id.content_frame,
//						summaryFragment, SUMMARY_TAG).commit();
//			}
//		}
    }


    //Generate temporary test data
//	public void generateRandomData()
//	{
//		double lat;
//		double lon;
//		int accuracy;
//		
//		gpsKey = (int)(Math.random() * 1000);
//		
//		lat = 37.2250322 + (Math.random() * 0.006);
//		lon = -80.428961 + (Math.random() * 0.01429);
//		accuracy = 1; //Temporary (isn't this whole function temporary? :) )
//		dataSource.createDataPointGps(System.currentTimeMillis(), gpsKey, lat, lon, accuracy);
//		System.out.println("Added GPS Data Point key: " + gpsKey);
//		
//		
//		//#######WHY CLEAR HERE?
//		
////		dataSource.clearDatabase();
////		Thread thread = new Thread()
////		{
////		    @Override
////		    public void run() {
//////		        try {
//////		        	double lat;
//////		    		double lon;
//////		    		int accuracy;
//////		    		double engagement;
//////		    		
//////		    		//#######Collect realtime datapoint
//////		    		
//////		    		for(int x = 0; x < 12; x++)
//////		    		{
//////		    			
//////		    			/* Generate points between:
//////						 * 37.231579, -80.428961
//////						 *          AND
//////						 * 37.225035, -80.414671
//////						 */
//////		    			
//////		    			
//////		    			//Blacksburg
//////						//lat = 37.225035 + (Math.random() * 0.006544);
//////						lat = 37.2250322 + (Math.random() * 0.006);
//////						lon = -80.428961 + (Math.random() * 0.01429);
//////						accuracy = 1; //Temporary (isn't this whole function temporary? :) )
//////						dataSource.createDataPointGps(System.currentTimeMillis(), gpsKey, lat, lon, accuracy);
//////						
//////						/*
//////						 * Generate random engagement data between:
//////						 * 0 AND 200
//////						 * With a random count at that GPS point between:
//////						 * 1 AND 201
//////						 */
//////						int randomUpperLimit = (int) (Math.random() * 200 + 1);
//////						for(int y = 0; y < randomUpperLimit; y++)
//////						{
//////							engagement = Math.random() * 200;
//////							
//////							dataSource.createDataPointAttention(System.currentTimeMillis(), gpsKey, engagement);
//////						}
////						gpsKey++;
//////					}
//////		        } catch (Exception e) {
//////		            e.printStackTrace();
//////		        }
//////
////			    MainActivity.this.runOnUiThread(new Runnable(){
////			        public void run(){
////						System.out.println("Loading points to map");
////			            mapFragment.loadPoints(dataSource);
////			        }
////			    });
////		    }
////		};
//////
////		thread.start();
//			
//	}
//	public List<double[]> getData(){
//		List<double[]> results = dataSource.getFilteredDataset();
//		
//		for(int i = 0; i < results.size(); i++){
//			System.out.println("results:" + results.get(i));
//		}
//		
//		return results;
//	}
    @Override
    public void onStart() {
        super.onStart();
        //generateRandomData();
        //start timer to check location every minute

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void redrawGraphs() {

        if (xyGraphFragment != null) {
            xyGraphFragment.redraw();
        }
        if (rangeGraphFragment != null) {
            rangeGraphFragment.redraw();
        }
    }

    public boolean fabClicked() {
        return this.fabClicked;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Toast.makeText(this, v.getClass().getName().toString(), Toast.LENGTH_SHORT);
    }

    public enum state {
        ANNOTATION_STATE, SLIDING_TABS_STATE;

    }
}

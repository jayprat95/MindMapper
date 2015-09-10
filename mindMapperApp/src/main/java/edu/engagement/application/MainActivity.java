package edu.engagement.application;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.engagement.OnboardActivity;
import edu.engagement.application.Fragments.BaselineFragment;
import edu.engagement.application.Fragments.DatabaseFragment;
import edu.engagement.application.Fragments.GraphListFragment;
import edu.engagement.application.Fragments.MapFrag;
import edu.engagement.application.Fragments.RangeGraphFragment;
import edu.engagement.application.Fragments.RealTimeDataFragment;
import edu.engagement.application.Fragments.ReflectionGraphFragment;
import edu.engagement.application.Fragments.SummaryFragment;
import edu.engagement.application.Fragments.XYGraphFragment;
import edu.engagement.application.SlidingTab.SlidingTabLayout;
import edu.engagement.application.SlidingTab.ViewPagerAdapter;

//import zephyr.android.HxMBT.BTClient;
//import zephyr.android.HxMBT.ZephyrProtocol;

/**
 * MainActivity June 16
 */
public class MainActivity extends FragmentActivity{
    private BroadcastReceiver receiver;

    //Onboarding Screen
    private ViewPager onboardPager;
    private List<View> onboardList;
    private View onboardPager1;
    private View onboardPager2;
    private View onboardPagerStart;

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
    private TextView attentionView;
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


        Intent intent = new Intent(this, OnboardActivity.class);
        startActivity(intent);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        realTimeInstantiated = false;


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
//                    fab.setText("Annotation");
                    Toast.makeText(thisActivity, "Connecting to Mind wave device", Toast.LENGTH_LONG).show();
                }
                changeState(state.ANNOTATION_STATE);

                /** If Jayanth wants to work on the real time eeg data uncommon this line to start the service */
//                startService();
            }
        });
        /** End of Fab Button Shit */


        System.out.println("Opened data source (DB)");

        initActionBar();


    }

    /**
     * Changes the apps's gobal state, and all
     * relevant GUI will change to reflect on the state*
     *
     * @param someState a new state
     */
    public void changeState(state someState) {

        if (someState == state.ANNOTATION_STATE) {

            if (realTimeInstantiated) { // If PlacePicker is called, shows the realtime fragment immediately
//                toolbar.setVisibility(View.INVISIBLE);
                pager.setVisibility(View.INVISIBLE);
                tabs.setVisibility(View.INVISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
            } else {    // PlacePicker will be called for the first time, will change fragment in PlacePicker's callback function, not at here
                switchToFragment("REAL_TIME_FRAGMENT");
                realTimeInstantiated = true;
            }

        } else {    // Changed from realtime to sliding tab
//            toolbar.setVisibility(View.VISIBLE);
            pager.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);

        }

    }

    private void initActionBar() {
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.hide();
        // Creates a contextual action bar that allows the user to connect
        //mActionMode = startActionMode(mActionModeCallback);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        //generateRandomData();
        //start timer to check location every minute

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.MAIN");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("some_msg");

                attentionView.setText(msg_for_me);
//
//                //log our message value
//                Toast.makeText(getApplicationContext(), msg_for_me, Toast.LENGTH_SHORT).show();
//                        Log.i("InchooTutorial", msg_for_me);
            }
        };
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(this.receiver);
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
     * Change the sliding tab to focus on the desired tab
     *
     * @param pageNum
     */
    public void pagerChange(int pageNum) {
        pager.setCurrentItem(pageNum);
    }

    public void instantiateView(TextView tv) {
        attentionView = tv;
    }

    public enum state {
        ANNOTATION_STATE, SLIDING_TABS_STATE;


    }

}

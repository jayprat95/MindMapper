package edu.engagement.application.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.engagement.application.App;
import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.SlidingTab.SlidingTabLayout;
import edu.engagement.application.SlidingTab.ViewPagerAdapter;

/**
 * Created by alex on 11/5/15.
 */
public class ReflectionFragment extends Fragment {

    private MainActivity activity;

    /**
     * Sliding Tabs variables
     */
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence titles[] = { "Activities" , "Map" };
    private int numTabs = 2;

    /**
     * Fab Button variables
     */
    private FloatingActionButton fab;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reflection, container, false);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), titles, numTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
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

        /* Fab Button Shit */
        fab = (FloatingActionButton) view.findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(App.NAME, "Showing Place Picker");
                activity.showStartupActivity();
            }
        });
        /* End of Fab Button Shit */

        return view;
    }

    /**
     * Change the sliding tab to focus on the desired tab
     *
     * @param pageNum
     */
    public void pagerChange(int pageNum) {
        pager.setCurrentItem(pageNum);
    }
}

package edu.engagement.application.SlidingTab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.engagement.application.Fragments.MapFrag;
import edu.engagement.application.Fragments.RealTimeDataFragment;
import edu.engagement.application.Fragments.*;

/**
 * Created by ducky on 4/18/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private MapFrag mapFragment;
    private RealTimeDataFragment realFragment;
    private GraphListFragment graphFragment;
    private ReflectionGraphFragment reflectionGraphFragment;
    private SummaryFragment summaryFragment;

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            if (mapFragment == null)
                mapFragment = new MapFrag();
            return mapFragment;
        } else if (position == 1) {
            realFragment = new RealTimeDataFragment();            //				if (summaryFragment == null)
//					summaryFragment = new SummaryFragment();
            return realFragment;
        } else if (position == 2) {
            if (reflectionGraphFragment == null)
                reflectionGraphFragment = new ReflectionGraphFragment();
            return reflectionGraphFragment;
        } else {
            if (summaryFragment == null)
                summaryFragment = new SummaryFragment();
            return summaryFragment;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {

        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}

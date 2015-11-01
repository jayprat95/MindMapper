package edu.engagement.application.SlidingTab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.Fragments.MapFrag;
import edu.engagement.application.Fragments.ReflectionGraphFragment;
import edu.engagement.application.Fragments.SummaryFragment;

/**
 * Created by ducky on 4/18/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private MapFrag mapFragment;
    private SummaryFragment summaryFragment;
    private List<Fragment> mFragmentList;

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.mFragmentList = new ArrayList<>();
        mapFragment = new MapFrag();
        summaryFragment = new SummaryFragment();
        mFragmentList.add(mapFragment);
        mFragmentList.add(summaryFragment);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);

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

package edu.engagement.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OnboardActivity extends Activity {

    //Onboarding Screen
    private ViewPager onboardPager;
    private List<View> onboardList;
    private View onboardPager1;
    private View onboardPager2;
    private View onboardPager3;
    private View onboardPager4;
    private View onboardPager5;
    private View onboardPagerStart;
    private TextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        //Onboarding viewpager init
        onboardPager = (ViewPager) findViewById(R.id.onboardPager);
        //onboardPager.setPageTransformer(true, new ZoomOutPageTransformer());

        onboardList = new ArrayList<View>();

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        onboardPager1 = inflater.inflate(R.layout.onboard_pager_1, null);
        onboardPager2 = inflater.inflate(R.layout.onboard_pager_2, null);
        onboardPager3 = inflater.inflate(R.layout.onboard_pager_3, null);
        onboardPager4 = inflater.inflate(R.layout.onboard_pager_4, null);
        onboardPager5 = inflater.inflate(R.layout.onboard_pager_5, null);

        onboardPagerStart = inflater.inflate(R.layout.onboard_pager_start, null);

        onboardPagerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        onboardList.add(onboardPager1);
        onboardList.add(onboardPager2);
        onboardList.add(onboardPager3);
        onboardList.add(onboardPager4);
        onboardList.add(onboardPager5);
        onboardList.add(onboardPagerStart);

        onboardPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = onboardList.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(onboardList.get(position));
            }

            @Override
            public int getCount() {
                return onboardList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    @Override
    public void onBackPressed() { }


}

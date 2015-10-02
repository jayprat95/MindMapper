package edu.engagement.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.R;

public class OnboardActivity extends Activity {
    //Onboarding Screen
    private ViewPager onboardPager;
    private List<View> onboardList;
    private View onboardPager1;
    private View onboardPager2;
    private View onboardPagerStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        //Onboarding viewpager init
        onboardPager = (ViewPager) findViewById(R.id.onboardPager);
        onboardPager.setPageTransformer(true, new ZoomOutPageTransformer());

        onboardList = new ArrayList<View>();

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        onboardPager1 = inflater.inflate(R.layout.onboard_pager_1, null);
        onboardPager2 = inflater.inflate(R.layout.onboard_pager_2, null);
        onboardPagerStart = inflater.inflate(R.layout.onboard_pager_start, null);

        onboardList.add(onboardPager1);
        onboardList.add(onboardPager2);
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

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


}

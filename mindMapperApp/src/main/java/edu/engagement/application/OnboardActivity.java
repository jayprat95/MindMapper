package edu.engagement.application;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

public class OnboardActivity extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.onboard_pager_3));
        addSlide(SampleSlide.newInstance(R.layout.onboard_pager_4));
        addSlide(SampleSlide.newInstance(R.layout.onboard_pager_start));
        // OPTIONAL METHODS
        // Override bar/separator color
        //setBarColor(Color.parseColor("#3F51B5"));


        // Hide Skip/Done button
        showSkipButton(false);
        showDoneButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(false);

    }

    @Override
    public void onSkipPressed() {

    }

    @Override
    public void onDonePressed() {
        finish();
    }

//    //Onboarding Screen
//    private ViewPager onboardPager;
//    private List<View> onboardList;
//    private View onboardPager1;
//    private View onboardPager2;
//    private View onboardPager3;
//    private View onboardPager4;
//    private View onboardPager5;
//    private View onboardPagerStart;
//    private TextView start;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_onboard);
//
//        //Onboarding viewpager init
//        onboardPager = (ViewPager) findViewById(R.id.onboardPager);
//        //onboardPager.setPageTransformer(true, new ZoomOutPageTransformer());
//
//        onboardList = new ArrayList<View>();
//
//        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//        onboardPager1 = inflater.inflate(R.layout.onboard_pager_1, null);
//        onboardPager2 = inflater.inflate(R.layout.onboard_pager_2, null);
//        onboardPager3 = inflater.inflate(R.layout.onboard_pager_3, null);
//        onboardPager4 = inflater.inflate(R.layout.onboard_pager_4, null);
//        onboardPager5 = inflater.inflate(R.layout.onboard_pager_5, null);
//
//        onboardPagerStart = inflater.inflate(R.layout.onboard_pager_start, null);
//
//        onboardPagerStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                finish();
//            }
//        });
//
//
//        onboardList.add(onboardPager1);
//        onboardList.add(onboardPager2);
//        onboardList.add(onboardPager3);
//        onboardList.add(onboardPager4);
//        onboardList.add(onboardPager5);
//        onboardList.add(onboardPagerStart);
//
//        onboardPager.setAdapter(new PagerAdapter() {
//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//                View view = onboardList.get(position);
//                container.addView(view);
//                return view;
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView(onboardList.get(position));
//            }
//
//            @Override
//            public int getCount() {
//                return onboardList.size();
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object object) {
//                return view == object;
//            }
//        });
//    }
//
//    @Override
//    public void onBackPressed() { }


}

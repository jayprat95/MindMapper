package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.MapListAdapter;
import edu.engagement.application.R;
import edu.engagement.application.utils.MapListData;
import edu.engagement.application.utils.RecyclerViewPositionHelper;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.TimeUtils;

public class MapFrag extends Fragment implements OnMapReadyCallback {

    private static final int FOCUSRADIO = 16;
    private int testFlag = 0;

    public static HashMap<String, Location> locationTable;
	private GoogleMap map; // The map from within the map fragment.
	private MapView mapView;
    private Button eegButton;
    private MapLoadTask task;
    private Button reportButton;
    private double eegEngagement;
    private List<double[]> mapData = new ArrayList<double[]>();
    //keep track of status of buttons
    // 1 for eeg; 2 for report; 3 for pope
    private static int status = 1;
    private double eegAverage;
    private double selfAverage;

    private RecyclerView rv;


	DataFilter filter;
	Intent intent;

	@Override
	public void onAttach(Activity activity) {
        super.onAttach(activity);
		intent = activity.getIntent();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.map, container, false);

        rv = (RecyclerView) view.findViewById(R.id.map_recycler_view);
//        rv.addOnItemTouchListener();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerViewPositionHelper helper = RecyclerViewPositionHelper.createHelper(recyclerView);

                int i = helper.findFirstCompletelyVisibleItemPosition();



                if (i >= 0) {
                    if(task != null){
                        //task.setOptionsColor();
                        task.setFocus(i);
                    }
                }
            }
        });

        locationTable = new HashMap<>();
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

//        eegButton = (Button) view.findViewById(R.id.eegButton);
//
//        eegButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if( MapFrag.status != 1 && task.optionsList != null){
//                    //
//
//                    for(int i = 0; i < task.optionsList.size(); i++){
//                        Double sessions = task.optionsData.get(i)[2];
//                        task.setOptionsColor(task.optionsList.get(i), task.optionsData.get(i)[0], 100.0, (int)Math.round(sessions));
//                    }
//                    MapFrag.status = 1;
//                }
//            }
//        });
//
//        reportButton = (Button) view.findViewById(R.id.reportButton);
//
//        reportButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(  MapFrag.status != 2 && task.optionsList != null){
//                    //
//                    Log.d("This is from Map frag", "report clicked");
//                    for(int i = 0; i < task.optionsList.size(); i++){
//                        Double sessions = task.optionsData.get(i)[2];
//                        task.setOptionsColor(task.optionsList.get(i), task.optionsData.get(i)[1], 5.0, (int)Math.round(sessions));
//                    }
//                    MapFrag.status = 2;
//                }
//            }
//        });

        // Grab the map from the map fragment.
        // TODO: Change to async?
        mapView.getMapAsync(this);

		return view;
	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map != null) {
            		MapsInitializer.initialize(this.getActivity());
            map.setMyLocationEnabled(true);

            //set info window for map marker
//            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info_window, null);
//                    TextView locationLabel = (TextView) v.findViewById(R.id.locationLabel);
//                    TextView eegLabel = (TextView) v.findViewById(R.id.eegLabel);
//                    TextView reportLabel = (TextView) v.findViewById(R.id.reportLabel);
//
//                    locationLabel.setText(marker.getTitle());
//                    String[] texts = marker.getSnippet().split("/");
//                    eegLabel.setText("AVE EEG: " + texts[0]);
//                    reportLabel.setText("AVE Report: " + texts[1]);
//
//                    return v;
//                }
//            });
            //Bundle testBundle = intent.getExtras();

            // For future reference with getting coords, pull up google maps, find
            // your desired location, then steal the coords from the url.
            LatLng blacksburg = new LatLng(37.23, -80.417778);

            // Updates the location and zoom of the MapView
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));


            filter = new DataFilter(7, null, 100, this.getClass().toString());

            task = new MapLoadTask(getActivity().getApplicationContext());
//            set marker on click listener
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    rv.scrollToPosition(Integer.parseInt(marker.getSnippet()));
                    return true;
                }
            });

            map.clear();

            task.execute();
        }

    }

    public void cameraFocusOnMap(LatLng focus){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(focus, FOCUSRADIO), 10, null);

    }

    private class MapLoadTask extends AsyncTask<Void, Void, List<MarkerOptions>> {

        private Context context;

        private List<MarkerOptions> optionsList;
        //private List<Double[]> optionsData;
        private HashMap<String, MarkerInfo> markerInfoMap;
        private List<MapListData> lists;
        private int selectedItemPosition = 0;


        public MapLoadTask (Context context) {
            this.context = context;
            optionsList = new ArrayList<>();
            //optionsData = new ArrayList<>();
            markerInfoMap = new HashMap<>();
            lists = new ArrayList<>();
        }

        @Override
        protected List<MarkerOptions> doInBackground(Void... params) {

            // idk, man, this just works
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                DataPointSource dataSource = new DataPointSource(context);
                dataSource.open();
                // TODO load marker
                loadData(dataSource);
                System.out.println("Trying to load data points ");
            } catch (Exception e) {
                // sqlite db locked - concurrency issue
                System.out
                        .println("MagFrag - sqlite db locked - concurrency issue ");
                System.out.println(e.toString());
            }
            return optionsList;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> optionsList) {
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv.setLayoutManager(llm);


            MapListAdapter adapter = new MapListAdapter(lists);
            rv.setAdapter(adapter);


            if(optionsList.size() > 0){
                cameraFocusOnMap(optionsList.get(0).getPosition());
            }


            for (MarkerOptions mo : optionsList) {
                map.addMarker(mo);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            map.clear();

            rv.getAdapter().notifyDataSetChanged();

            cameraFocusOnMap(optionsList.get(selectedItemPosition).getPosition());
            for (MarkerOptions mo : optionsList) {
                map.addMarker(mo);
            }
        }

        public void loadData(DataPointSource dpSource) {
            //

            //group data by session Id to count the number of sessions
            //compute ave attention for all sessions
            // time range of day and month

            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);

            // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
            int month = (c.get(Calendar.MONTH) + 1);
            List<Session> sessions = dpSource.getSessionsInTimeRange(day);
            //List<String[]> results = dpSource.getMapDataset();

            /** --------------- Test points for place picker------------------- **/
//            double[] testPoint = new double[5];
//            testPoint[0] = 0;
//            testPoint[1] = 40; //average eeg attention
//            testPoint[2] = 37.230632; // Latitude
//            testPoint[3] = -80.423106; // Longitude
//            testPoint[4] = 5.0; //average self report
//
//            double[] testPoint1 = new double[5];
//            testPoint1[0] = 0;
//            testPoint1[1] = 60;
//            testPoint1[2] = 37.233061; // Latitude
//            testPoint1[3] = -80.423106; // Longitude
//            testPoint[4] = 1.0; //average self report
//
//            mapData.add(testPoint);
//            mapData.add(testPoint1);

            /** --------------- Test points for place picker------------------- **/

            for (Session session : sessions) {

//                System.out.println("-------------- inside loop -----------");
//                if (Double.parseDouble(pointArray[1]) == 0) {
//                    System.out.println("-------------- for loop break -----------");
//                    //break if there is no eeg attention
//                    break;
//                }


//                String test = "session id: " + pointArray[0] + ", Attention: " + pointArray[1]
//                        + ", location name: " + pointArray[2] + ", lat - long: " + pointArray[3] + ", " + pointArray[4] + ", " + pointArray[5];
//
//                Log.v("Map frg", test);



                if(locationTable.containsKey(session.getLocation().getName())){
                    MarkerInfo info = markerInfoMap.get(session.getLocation().getName());

                    info.setSessions(info.getSessions() + 1);
                    info.setAttention(info.getAttention() + session.getEEGAverage());

                    String list = session.getActivityName() + " for "+ TimeUtils.getElapsedTimeFormatted(session);

                    info.setActivityList(list);
                }
                else{
                    Location location = new Location("");
                    location.setLatitude(session.getGPSData().latitude);
                    location.setLongitude(session.getGPSData().longitude);
                    locationTable.put(session.getLocation().getName(), location);

                    String list = session.getActivityName() + " for "+ TimeUtils.getElapsedTimeFormatted(session);

                    MarkerInfo marker = new MarkerInfo(session.getGPSData().latitude, session.getGPSData().longitude, session.getEEGAverage(), session.getLocation().getName(), session.getSelfReportAverage(), list);
                    markerInfoMap.put(session.getLocation().getName(), marker);
                    //
                }
            }

            lists.clear();
            //two important infomation before adding marker: 1. AVE attetion for all sessions; 2. the number
            // of session to be shown on marker
            for(Map.Entry<String, MarkerInfo> entry : markerInfoMap.entrySet()){

                System.out.println("----loop----");
                MarkerInfo info = entry.getValue();
                addMarker(info.getLatitue(), info.getLongitude(), info.getAttention(), info.getSelfReport(), info.getLocation(), info.getSessions(), 4.0);

                double eegPercent = info.getAttention()/ info.getSessions()  / 4.0;
                double reportPercent = info.getSelfReport() / info.getSessions() / 4.0;;

                MapListData data = new MapListData(info.getLocation(), AttentionLevel.fromInt((int) eegPercent), AttentionLevel.fromInt((int)reportPercent), info.getActivityList());

                lists.add(data);
            }
        }

        /*
     * addMarker to the map
     * color: 0-bule; 1-cyan; 2-green; 3-Yellow; 4-orange
     */
        private void addMarker(double latitude, double longitude,
                               double eegEngagement, double selfReportEngagement, String location, int sessions, double baselineEngagement) {

            MarkerOptions opt = new MarkerOptions();
            LatLng lat = new LatLng(latitude, longitude);
            opt.position(lat);
            opt.title(location);
            opt.snippet(optionsList.size() - 1 + "");

            System.out.println("------add marker-----");

            double eegPercent = eegEngagement/ sessions  / baselineEngagement;
            double reportPercent = selfReportEngagement / sessions / baselineEngagement;;

            IconGenerator iconFactory = new IconGenerator(getActivity());
            //left half ciecle for eeg
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.circle);
            GradientDrawable leftShape = (GradientDrawable)drawable;
            leftShape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(eegPercent)).getColor());
            leftShape.setStroke(5, Color.BLACK);
            //right half ciecle for eeg
            LayerDrawable layers = (LayerDrawable)getActivity().getResources().getDrawable(R.drawable.semicircle);
            GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.rightHalfLevel1));
            shape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(reportPercent)).getColor());
            shape.setStroke(5, Color.BLACK);
            layers.setLevel(5000);
            iconFactory.setBackground(layers);

            opt.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
            optionsList.add(opt);
        }


        private void setFocus(int selectedItemPosition){

            if (optionsList.size() <= selectedItemPosition){
                return;
            }

            MarkerOptions opt = optionsList.get(selectedItemPosition);
            String key = opt.getTitle();

            for (MarkerOptions mo : optionsList) {
                String title = mo.getTitle();
                MarkerInfo info = markerInfoMap.get(title);
                double eegPercent = info.getAttention() / info.getSessions()  / 4.0;
                double reportPercent = info.getSelfReport() / info.getSessions() / 4.0;

//                IconGenerator iconFactory = new IconGenerator(getActivity());
//                //left half ciecle for eeg
//                Drawable drawable = getActivity().getResources().getDrawable(R.drawable.circle);
//                GradientDrawable leftShape = (GradientDrawable)drawable;
//
//                //right half ciecle for eeg
//                LayerDrawable layers = (LayerDrawable)getActivity().getResources().getDrawable(R.drawable.semicircle);
//                GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.rightHalfLevel1));
//
                if(title == key){
                    IconGenerator iconFactory = new IconGenerator(getActivity());
                    //left half ciecle for eeg
                    Drawable drawable = getActivity().getResources().getDrawable(R.drawable.circle);
                    GradientDrawable leftShape = (GradientDrawable)drawable;
                    leftShape.setColor(AttentionLevel.fromInt((int) Math.round(eegPercent)).getColor());
                    leftShape.setStroke(12, Color.BLACK);
                    //right half ciecle for eeg
                    LayerDrawable layers = (LayerDrawable)getActivity().getResources().getDrawable(R.drawable.semicircle);
                    GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.rightHalfLevel1));
                    shape.setColor(AttentionLevel.fromInt((int) Math.round(reportPercent)).getColor());
                    shape.setStroke(12, Color.BLACK);
                    layers.setLevel(5000);
                    iconFactory.setBackground(layers);
                    mo.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
//                    leftShape.setColor(AttentionLevel.fromInt((int) Math.round(eegPercent)).getColor());
//                    leftShape.setStroke(15, Color.BLACK);
//
//                    shape.setColor(AttentionLevel.fromInt((int) Math.round(reportPercent)).getColor());
//                    shape.setStroke(15, Color.BLACK);
                    this.selectedItemPosition = selectedItemPosition;
                }
                else{
                    IconGenerator iconFactory = new IconGenerator(getActivity());
                    //left half ciecle for eeg
                    Drawable drawable = getActivity().getResources().getDrawable(R.drawable.circle);
                    GradientDrawable leftShape = (GradientDrawable)drawable;
                    leftShape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(eegPercent)).getColor());
                    leftShape.setStroke(5, Color.BLACK);
                    //right half ciecle for eeg
                    LayerDrawable layers = (LayerDrawable)getActivity().getResources().getDrawable(R.drawable.semicircle);
                    GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.rightHalfLevel1));
                    shape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(reportPercent)).getColor());
                    shape.setStroke(5, Color.BLACK);
                    layers.setLevel(5000);
                    iconFactory.setBackground(layers);
                    mo.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
//                    leftShape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(eegPercent)).getColor());
//                    leftShape.setStroke(5, Color.BLACK);
//
//                    shape.setColor(AttentionLevel.fromIntTransparent((int) Math.round(reportPercent)).getColor());
//                    shape.setStroke(5, Color.BLACK);
                }
                //layers.setLevel(5000);
                //iconFactory.setBackground(layers);

            }
            publishProgress();
        }
    }

    private class MarkerInfo{
        private double latitue;
        private double longitude;
        private double attention;
        private String location;
        private int sessions;
        private double selfReport;
        private String activityList = "";

        public MarkerInfo(double latitude, double longitude,
                          double attention, String location, double selfReport, String activityList){
            this.latitue = latitude;
            this.longitude = longitude;
            this.attention = attention;
            this.location = location;
            this.sessions = 1;
            this.selfReport = selfReport;
            this.activityList = activityList;
        }

        public double getSelfReport() {
            return this.selfReport;
        }
        public void setSelfReport(double report){
            this.selfReport = report;
        }
        public double getLatitue() {
            return latitue;
        }

        public void setLatitue(double latitue) {
            this.latitue = latitue;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getAttention() {
            return attention;
        }

        public void setAttention(double attention) {
            this.attention = attention;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        public void setActivityList(String activity){
            activityList = activityList + ", " + activity;
        }

        public String getActivityList(){
            return activityList;
        }
    }
}

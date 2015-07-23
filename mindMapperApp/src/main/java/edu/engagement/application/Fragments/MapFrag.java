package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

public class MapFrag extends Fragment implements OnMapReadyCallback {
	private GoogleMap map; // The map from within the map fragment.
	private MapView mapView;
    private Button eegButton;
    private Button reportButton;
    private double eegEngagement;


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

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        eegButton = (Button) view.findViewById(R.id.eegButton);

        reportButton = (Button) view.findViewById(R.id.reportButton);

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
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.marker_info_window, null);
                    TextView locationLabel = (TextView) v.findViewById(R.id.locationLabel);
                    TextView eegLabel = (TextView) v.findViewById(R.id.eegLabel);
                    TextView reportLabel = (TextView) v.findViewById(R.id.reportLabel);

                    locationLabel.setText(marker.getTitle());
                    eegLabel.setText("AVE EEG: " + marker.getSnippet());
                    return v;
                }
            });
            //Bundle testBundle = intent.getExtras();

            // For future reference with getting coords, pull up google maps, find
            // your desired location, then steal the coords from the url.
            LatLng blacksburg = new LatLng(37.23, -80.417778);

            // Updates the location and zoom of the MapView
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));
            System.out.println("Animated camera on map ");

            filter = new DataFilter(7, null, 100, this.getClass().toString());

            //set marker on click listener
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                    } else {
                        marker.showInfoWindow();
                    }
                    return true;
                }
            });

            map.clear();
            new MapLoadTask(getActivity().getApplicationContext()).execute();
        }
    }

    private class MapLoadTask extends AsyncTask<Void, Void, List<MarkerOptions>> {

        private Context context;

        private List<MarkerOptions> optionsList;

        public MapLoadTask (Context context) {
            this.context = context;
            optionsList = new ArrayList<>();
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
            for (MarkerOptions mo : optionsList) {
                map.addMarker(mo);
            }
        }

        public void loadData(DataPointSource dpSource) {
            System.out.println("Cleared map");

            List<double[]> results = dpSource.getMapDataset();

            for (int i = 0; i < results.size(); i++) {
                double[] data = results.get(i);

                System.out.println("\nMap Dataset results: " + i);

                for (double d : data) {
                    System.out.print(d + ", ");
                }
            }

            System.out.println("Exited Dataset for loop");

            // TODO remove these later
            /** --------------- Test points for place picker------------------- **/
            double[] testPoint = new double[4];
            testPoint[0] = 0;
            testPoint[1] = 40;
            testPoint[2] = 37.230632; // Latitude
            testPoint[3] = -80.423106; // Longitude

            double[] testPoint1 = new double[4];
            testPoint1[0] = 0;
            testPoint1[1] = 60;
            testPoint1[2] = 37.233061; // Latitude
            testPoint1[3] = -80.423106; // Longitude

            results.add(testPoint);
            results.add(testPoint1);

            /** --------------- Test points for place picker------------------- **/

            for (double[] pointArray : results) {
			/*
			 * 0 - gps_key
			 * 1 - Attention
			 * 2 - Latitude
			 * 3 - Longitude
			 */
                if (pointArray[1] == 0) {
                    break;
                }

                addMarker(pointArray[2], pointArray[3], pointArray[1], 100.0);
            }
        }

        /*
     * addMarker to the map
     * color: 0-bule; 1-cyan; 2-green; 3-Yellow; 4-orange
     */
        private void addMarker(double latitude, double longitude,
                               double engagement, double baselineEngagement) {

            MarkerOptions opt = new MarkerOptions();
            opt.position(new LatLng(latitude, longitude));
            opt.title("McBryde Hall");
            //snippet carry data from database for infowindow
            opt.snippet(String.valueOf(engagement));

            double percent = engagement / baselineEngagement;
        /*  5 stage:
            very low: 0 - 0.2;
            somewhat low: 0.2 - 0.4;
            medium: 0.4 - 0.6;
            somewhat high: 0.6 - 0.8;
            high: 0.8 - 1;
         */

            if(percent >=0 && percent < 0.2 ){
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            else if(percent >= 0.2 && percent < 0.4) {
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }
            else if(percent >= 0.4 && percent < 0.6){
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            else if(percent >= 0.6 && percent < 0.8){
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            else{
                opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }

            optionsList.add(opt);
        }
    }
}

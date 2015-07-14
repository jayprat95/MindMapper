package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

public class MapFrag extends Fragment {
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
        map = mapView.getMap();

        if (map != null) {
            //		MapsInitializer.initialize(this.getActivity());
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));
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


            try {
                DataPointSource dataSource = new DataPointSource(getActivity()
                        .getApplicationContext());
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
        }

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

    /*
     * addMarker to the map
     * color: 0-bule; 1-cyan; 2-green; 3-Yellow; 4-orange
     */
	private void addMarker(double latitude, double longitude,
                           double engagement, double baselineEngagement) {
//		map.addCircle(new CircleOptions()
//				.center(new LatLng(latitude, longitude)).radius(radius)
//				.strokeWidth(0).fillColor(color));
//
//		map.addCircle(new CircleOptions()
//				.center(new LatLng(latitude, longitude)).radius(8)
//				.strokeWidth(2).strokeColor(9276299).fillColor(color));


        MarkerOptions opt = new MarkerOptions();
        opt.position(new LatLng(latitude, longitude));
        opt.title("McBryde Hall");
        //snippet carry data from database for infowindow
        opt.snippet("" + engagement);

        double percent = engagement / baselineEngagement;
        /*  5 stage:
            very low: 0 - 0.2;
            somewhat low: 0.2 - 0.4;
            middium: 0.4 - 0.6;
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



        Marker marker = map.addMarker(opt);

       // marker.showInfoWindow();
	}

	public void loadData(DataPointSource dpSource) {
		map.clear();
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

			/*
			 * TO-DO: Adjust formula to fit real test data
			 */
			// Radius = sqrt( Area / pi )

			// // The area of the circle represents the number of points at that
			// location
			// double radius = Math.sqrt(pointArray[4] / (Math.PI));

			// The area of the circle represents the attention at that point
			// (dont know if this is the average)
			// added 10 to see the 0'd values
			//double radius = pointArray[1] + 10; // Math.sqrt((pointArray[1]+10)
												// / (Math.PI));

			//radius *= 2.5;

			//Log.d("loadmarker", pointArray[1] + ", " + pointArray[2] + ", " + pointArray[3]);
			//		+ " Radius: " + radius);
            addMarker(pointArray[2], pointArray[3], pointArray[1], 100.0);

//			/* Place Picker Experiment */
//			int PLACE_PICKER_REQUEST = 1;
//			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//			Context context = this.getActivity().getApplicationContext();
//			try {
//				// Start the intent by requesting a result,
//				// identified by a request code.
//				startActivityForResult(builder.build(context),
//						PLACE_PICKER_REQUEST);
//				// PlacePicker.getPlace(builder, context);
//
//			} catch (GooglePlayServicesRepairableException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (GooglePlayServicesNotAvailableException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}


//	/*
//	 * Callback method that is called after user selects a location.
//	 */
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == 1) {
//			if (resultCode == getActivity().RESULT_OK) {
//				// Load the bundle from the activity's intent
//				Bundle bundle = this.intent.getExtras();
//
//				// Initialize new bundle if it does not exist
//				if (bundle == null) {
//					bundle = new Bundle();
//				}
//				Place place = PlacePicker.getPlace(data, this.getActivity()
//						.getApplicationContext());
//
//				// Save the location into the bundle
//				bundle.putString("Location", "" + place.getName());
//
//				// Save the bundle into the activity
//				this.intent.putExtras(bundle);
//
//				String toastMsg = String.format("Place: %s", place.getName());
//				Toast.makeText(this.getActivity().getApplicationContext(),
//						toastMsg, Toast.LENGTH_LONG).show();
//			}
//		}
//	}

	//public int getEngagementColor(double engagement, double baselineEngagement) {

		// double difference = engagement - baselineEngagement;

		// double difference = engagement - 40;

//		int red, green, blue;
//		red = green = blue = 0;
//
//		red = (int) (Math.random() * 255);
//		green = (int) (Math.random() * 255);
//		blue = (int) (Math.random() * 255);

		//
		// // These colors should probably be tweaked.
		// if(difference >= 0)
		// {
		// // Higher than the baseline, add amount to red.
		// red += (int)difference * 30;
		// if(red > 255)
		// {
		// red = 255;
		// }
		// }
		// else
		// {
		// // Lower than baseline, add amount to blue.
		// // At this point, we know the difference is negative
		// blue += (int) Math.abs(difference)*30;
		// if(blue > 255)
		// {
		// blue = 255;
		// }
		// }

		//return Color.argb(100, red, green, blue);

	//}



//    /*
//     * Add marker when map is ready
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        //Locations(coords) and color are defined by databse
//        MarkerOptions marker = new MarkerOptions();
//        marker.position(new LatLng(10,10));
//        googleMap.addMarker(marker);
//    }
}

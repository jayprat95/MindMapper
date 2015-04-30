package edu.engagement.application.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

public class MapFrag extends Fragment {
	GoogleMap map; // The map from within the map fragment.
	SupportMapFragment mapFrag; // The map fragment from the xml file.

	DataFilter filter;
	Intent intent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.map, container, false);
		intent = this.getActivity().getIntent();
		
		
		// Data source

		// Grab the map fragment.
		mapFrag = (SupportMapFragment) getChildFragmentManager()
				.findFragmentById(R.id.mapView);
		// Grab the map from the map fragment.
		map = mapFrag.getMap();

		MapsInitializer.initialize(this.getActivity());
		map.setMyLocationEnabled(true);

		// For future reference with getting coords, pull up google maps, find
		// your desired location, then steal the coords from the url.
		LatLng blacksburg = new LatLng(37.23, -80.417778);

		// Updates the location and zoom of the MapView
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));
		System.out.println("Animated camera on map ");

		filter = new DataFilter(7, null, 100, this.getClass().toString());

		try {
			DataPointSource dataSource = new DataPointSource(this.getActivity()
					.getApplicationContext());
			dataSource.open();
			loadPoints(dataSource);
			System.out.println("Trying to load data points ");

		} catch (Exception e) {
			// sqlite db locked - concurrency issue
			System.out
					.println("MagFrag - sqlite db locked - concurrency issue ");
			System.out.println(e.toString());
		}
		Bundle testBundle = intent.getExtras();

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// Remove the map fragment so we don't reinflate it when the view is
		// recreated.
		try {
			FragmentTransaction ft = getActivity().getSupportFragmentManager()
					.beginTransaction();
			ft.remove((Fragment) mapFrag);
			ft.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addPoint(double latitude, double longitude, double radius,
			int color) {
		map.addCircle(new CircleOptions()
				.center(new LatLng(latitude, longitude)).radius(radius)
				.strokeWidth(0).fillColor(color));
		map.addCircle(new CircleOptions()
				.center(new LatLng(latitude, longitude)).radius(8)
				.strokeWidth(2).strokeColor(9276299).fillColor(color));
	}

	public void loadPoints(DataPointSource dpSource) {
		map.clear();
		System.out.println("Cleared map");

		List<double[]> results = dpSource.getMapDataset();

		for (int i = 0; i < results.size(); i++) {
			double[] data = results.get(i);

			System.out.println("\nMap Dataset results: " + i);
			for (int j = 0; j < data.length; j++) {
				System.out.print(data[j] + ", ");
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
			 * 0 - gps_key 1 - Attention 2 - Latitude 3 - Longitude
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
			double radius = pointArray[1] + 10; // Math.sqrt((pointArray[1]+10)
												// / (Math.PI));

			radius *= 2.5;

			Log.d("loadPoints", pointArray[2] + ", " + pointArray[3]
					+ " Radius: " + radius);
			addPoint(pointArray[2], pointArray[3], radius,
					getEngagementColor(pointArray[1], 100));

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

	public int getEngagementColor(double engagement, double baselineEngagement) {
		// double difference = engagement - baselineEngagement;

		// double difference = engagement - 40;

		int red, green, blue;
		red = green = blue = 0;

		red = (int) (Math.random() * 255);
		green = (int) (Math.random() * 255);
		blue = (int) (Math.random() * 255);

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

		return Color.argb(100, red, green, blue);
	}
}

package edu.engagement.application;

import edu.engagement.application.Database.DataPointSource;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener{
		DataPointSource dataSource = null;

		
		@Override
	    public void onLocationChanged(Location location)
	    {           
//		 	/**** DATABASE STUFF ****/
//			dataSource = new DataPointSource(this.getApplicationContext());
//			dataSource.open();

	          //Set marker here
//	          LatLng pos=new LatLng(location.getLatitude(), location.getLongitude());
		 
//	           map.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(markericon)));

			 int accuracy = 1; 
//			 MainActivity.this.dataSource.createDataPointGps(System.currentTimeMillis(), MainActivity.this.gpsKey, location.getLatitude(), location.getLongitude(), accuracy);
	    }

	    @Override
	    public void onProviderDisabled(String provider)
	    {

	    }

	    @Override
	    public void onProviderEnabled(String provider)
	    {

	    }

	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras)
	    {

	    }                
}

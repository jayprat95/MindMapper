package edu.engagement.thrift;

public class AllDataset {

	double _timestamp;
	double _lat;
	double _long;
	double _attention;
	double _count = 0;
	
	public AllDataset(double timestamp, double lat, double longitude,/*, double count,*/ double attention){
		_timestamp = timestamp;
		_lat = lat;
		_long = longitude;
		//_count = count;
		_attention = attention;
	}
	
	public String toString(){
		return "Attention:" + _attention + " \nTimestamp:" +_timestamp + ", \nLat:" + _lat + ", \nLong:" + _long + ", \nCount:" + _count;
	}
}

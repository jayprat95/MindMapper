package edu.engagement.application.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;

import edu.engagement.thrift.EegPower;
import edu.engagement.thrift.HeartRate;
import edu.engagement.thrift.EegAttention;
import edu.engagement.thrift.EegRaw;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DataPointSource
{

	public static final String SHOW_HIGH_ENGAGEMENT = "HIGH";
	public static final String SHOW_LOW_ENGAGEMENT = "LOW";
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	/*private String[] allColumns =
	{ DatabaseHelper.COLUMN_TIMESTAMP, DatabaseHelper.COLUMN_HEARTRATE, DatabaseHelper.COLUMN_ALPHA,
			DatabaseHelper.COLUMN_BETA, DatabaseHelper.COLUMN_THETA, DatabaseHelper.COLUMN_ATTENTION,
			DatabaseHelper.COLUMN_CH1, DatabaseHelper.COLUMN_CH2, DatabaseHelper.COLUMN_CH3, DatabaseHelper.COLUMN_CH4,
			DatabaseHelper.COLUMN_CH5, DatabaseHelper.COLUMN_CH6, DatabaseHelper.COLUMN_CH7, DatabaseHelper.COLUMN_CH8 };*/

	public DataPointSource(Context context)
	{
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		dbHelper.close();
	}

	public DataPoint createDataPointEEG(long timeStamp, int gpsKey, double alpha, double beta, double theta)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
		values.put(DatabaseHelper.COLUMN_ALPHA, alpha);
		values.put(DatabaseHelper.COLUMN_BETA, beta);
		values.put(DatabaseHelper.COLUMN_THETA, theta);

		database.insert(DatabaseHelper.TABLE_EEG, null, values);

		return new DataPoint(timeStamp, 0, alpha, beta, theta, 0, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0);
	}

	public DataPoint createDataPointHR(long timeStamp, int gpsKey, double hr)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
		values.put(DatabaseHelper.COLUMN_HEARTRATE, hr);

		database.insert(DatabaseHelper.TABLE_HR, null, values);

		return new DataPoint(timeStamp, hr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0);
	}

	public DataPoint createDataPointAttention(long timeStamp, int gpsKey, double attention, int day, int month)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
		values.put(DatabaseHelper.COLUMN_ATTENTION, attention);
		values.put(DatabaseHelper.COLUMN_DAY, day);
		values.put(DatabaseHelper.COLUMN_MONTH, month);

		database.insert(DatabaseHelper.TABLE_ATTENTION, null, values);

		return new DataPoint(timeStamp, 0, 0, 0, 0, attention, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0, day, month);
	}
//	public DataPoint createDataPointAttention(long timeStamp, int gpsKey, double attention)
//	{
//		ContentValues values = new ContentValues();
//		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
//		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
//		values.put(DatabaseHelper.COLUMN_ATTENTION, attention);
//
//		database.insert(DatabaseHelper.TABLE_ATTENTION, null, values);
//
//		return new DataPoint(timeStamp, 0, 0, 0, 0, attention, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0);
//	}

	public DataPoint createDataPointRaw(long timeStamp, int gpsKey, double ch1, double ch2, double ch3, double ch4, double ch5,
			double ch6, double ch7, double ch8)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
		values.put(DatabaseHelper.COLUMN_CH1, ch1);
		values.put(DatabaseHelper.COLUMN_CH2, ch2);
		values.put(DatabaseHelper.COLUMN_CH3, ch3);
		values.put(DatabaseHelper.COLUMN_CH4, ch4);
		values.put(DatabaseHelper.COLUMN_CH5, ch5);
		values.put(DatabaseHelper.COLUMN_CH6, ch6);
		values.put(DatabaseHelper.COLUMN_CH7, ch7);
		values.put(DatabaseHelper.COLUMN_CH8, ch8);

		database.insert(DatabaseHelper.TABLE_ATTENTION, null, values);

		return new DataPoint(timeStamp, 0, 0, 0, 0, 0, ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, gpsKey, 0, 0, 0);
	}

	public DataPoint createDataPointGps(long timeStamp, int gpsKey, double latitude, double longitude, double accuracy)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
		values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
		values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
		values.put(DatabaseHelper.COLUMN_ACCURACY, accuracy);

		long testvalue = database.insert(DatabaseHelper.TABLE_GPS, null, values);

		return new DataPoint(timeStamp, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, latitude, longitude, accuracy);
	}

	public void deleteDataPoint(DataPoint point)
	{
		long timestamp = point.getTimeStamp();
		if (point.getAlpha() != 0)
		{
			database.delete(DatabaseHelper.TABLE_EEG, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
			System.out.println("DataPoint deleted with timestamp: " + timestamp);
		} else if (point.getHeartRate() != 0)
		{
			database.delete(DatabaseHelper.TABLE_HR, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
			System.out.println("DataPoint deleted with timestamp: " + timestamp);
		} else if (point.getAttention() != 0)
		{
			database.delete(DatabaseHelper.TABLE_ATTENTION, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
			System.out.println("DataPoint deleted with timestamp: " + timestamp);
		} else if (point.getCh1() != 0)
		{
			database.delete(DatabaseHelper.TABLE_RAW, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
			System.out.println("DataPoint deleted with timestamp: " + timestamp);
		}

	}

	public List<EegPower> getAllDataPointsEEG()
	{
		List<EegPower> points = new ArrayList<EegPower>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_EEG, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			EegPower point = new EegPower();
			point.setMillisecondTimeStamp(String.valueOf(cursor.getLong(0)));
			point.setAlpha(cursor.getInt(1));
			point.setBeta(cursor.getInt(2));
			point.setTheta(cursor.getInt(3));
			points.add(point);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return points;
	}

	public List<HeartRate> getAllDataPointsHR()
	{
		List<HeartRate> points = new ArrayList<HeartRate>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_HR, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			HeartRate point = new HeartRate();
			point.setMillsecondTimeStamp(String.valueOf(cursor.getLong(0)));
			point.setBpm(cursor.getInt(1));
			points.add(point);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return points;
	}

	public List<EegAttention> getAllDataPointsAttention()
	{
		List<EegAttention> points = new ArrayList<EegAttention>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_ATTENTION, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			EegAttention point = new EegAttention();
			point.setMillisecondTimeStamp(String.valueOf(cursor.getLong(0)));
			point.setAttention(cursor.getInt(2));
			points.add(point);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return points;
	}

	public List<EegRaw> getAllDataPointsRaw()
	{
		List<EegRaw> points = new ArrayList<EegRaw>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_RAW, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			EegRaw point = new EegRaw();
			point.setMillisecondTimeStamp(String.valueOf(cursor.getLong(0)));
			point.setRaw(cursor.getInt(1));//index 1 is gpsKey..
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return points;
	}

	public List<double[]> getAllDataPointsGPS()
	{
		List<double[]> list = new ArrayList<double[]>();
		double[] gps = new double[5];
		Cursor cursor = database.query(DatabaseHelper.TABLE_GPS, null, null, null, null, null, null);
		cursor.moveToLast();
		int counter = 0;
		while (!cursor.isBeforeFirst())
		{
			/*0 - Key
			* 1 - Timestamp
			* 2 - Lat
			* 3 - Lon
			* 4 - Accuracy
			*/
			gps[0] = cursor.getInt(0);
			gps[1] = cursor.getDouble(1);
			gps[2] = cursor.getDouble(2);
			gps[3] = cursor.getDouble(3);
			gps[4] = cursor.getDouble(4);
			list.add(gps);
			cursor.moveToPrevious();
		}
		cursor.close();
		return list;
	}

	public List<double[]> getMaxDataset()
	{
		System.out.println("getMaxDataset");

//		Date date = new Date();
//		int earliestAge = (int) (date.getTime()/1000 - /*DataFilter.getDaysToLoad()*/ 5*86400);
////		
////		String attentionFilter = DataFilter.getFilter();
////		double attentionThreshold = DataFilter.getAttentionThreshold();
////		
		List<double[]> list = new ArrayList<double[]>();
//		
////		String query1 = getFirstFilterQuery(earliestAge);
////		
////		String query2 = getSecondFilterQuery(query1);
//		
//		Cursor cursor = database.rawQuery(getMaxFilterQuery(), null);		
//		
//		cursor.moveToLast();
//		while (!cursor.isBeforeFirst())
//		{
//			double[] data = new double[5];
//			data[0] = cursor.getDouble(0);
//			data[1] = cursor.getDouble(1);
//			data[2] = cursor.getDouble(2);
//			data[3] = cursor.getDouble(3);
//			data[4] = cursor.getInt(4);
//			list.add(data);
//			cursor.moveToPrevious();
//		}
//		cursor.close();


		//test
		double[] data = new double[5];
		data[0] = 10;
		data[1] = 20;
		data[2] = 30;
		data[3] = 40;
		data[4] = 50;
		list.add(data);

		double[] data2 = new double[5];
		data2[0] = 100;
		data2[1] = 200;
		data2[2] = 300;
		data2[3] = 400;
		data2[4] = 500;
		list.add(data2);

		return list;
	}

	public List<double[]> getMapDataset(){
		List<double[]> list = new ArrayList<double[]>();
		String query;

		//selects the lat, long and AVERAGE attention level
		//associated with each unique gpskey
	    query = "SELECT a.gps_key, AVG(a.Attention), b.Latitude, b.Longitude"
				+ " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
				+ " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
				+ " ON a.gps_key = b.gps_key"
				+ " GROUP BY a.gps_key";

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToLast();
		while (!cursor.isBeforeFirst())
		{
			double[] data = new double[4];
			data[0] = cursor.getDouble(0);
			data[1] = cursor.getDouble(1);
			data[2] = cursor.getDouble(2);
			data[3] = cursor.getDouble(3);

			list.add(data);
			cursor.moveToPrevious();
		}
		cursor.close();
		return list;
	}

	public List<double[]> getDayGraphDataset()
	{
		List<double[]> list = new ArrayList<double[]>();
		String query;

		//selects the attention level for every timestamp
		query = "SELECT table_attention.Timestamp, table_attention.Attention, table_gps.Latitude, table_gps.Longitude, table_attention.day, table_attention.month FROM "
				+ DatabaseHelper.TABLE_ATTENTION
				+ "  INNER JOIN table_gps ON table_attention.gps_key=table_gps.gps_key where day = 29;";

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToLast();
		while (!cursor.isBeforeFirst())
		{
			double[] data = new double[cursor.getColumnCount()];
			data[0] = cursor.getDouble(0);
			data[1] = cursor.getDouble(1);
			data[2] = cursor.getDouble(2);
			data[3] = cursor.getDouble(3);
			data[4] = cursor.getInt(4);
			data[5] = cursor.getInt(5);

			list.add(data);
			cursor.moveToPrevious();
		}
		cursor.close();
		return list;
	}

	public List<double[]> getFilteredDataset()
	{
		Date date = new Date();
		int earliestAge = (int) (date.getTime()/1000 - DataFilter.getDaysToLoad()*86400);

		String attentionFilter = DataFilter.getFilter();
		double attentionThreshold = DataFilter.getAttentionThreshold();

		List<double[]> list = new ArrayList<double[]>();

		String query1 = getFirstFilterQuery(earliestAge);

		String query2 = getSecondFilterQuery(query1);

		Cursor cursor = database.rawQuery(query2, null);

		cursor.moveToLast();
		while (!cursor.isBeforeFirst())
		{
			double[] data = new double[5];
			data[0] = cursor.getDouble(0);
			data[1] = cursor.getDouble(1);
			data[2] = cursor.getDouble(2);
			data[3] = cursor.getDouble(3);
			data[4] = cursor.getInt(4);
			list.add(data);
			cursor.moveToPrevious();
		}
		cursor.close();
		return list;
	}
	public String getFirstFilterQuery(int earliestTimestamp){
		String query1;

		String attentionFilter = DataFilter.getFilter();
		double attentionThreshold = DataFilter.getAttentionThreshold();

		if(attentionFilter == null || attentionFilter.equals("")){

			query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
					+ " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
					+ " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
					+ " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
					+ " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp;

		}else if(attentionFilter.equals(SHOW_HIGH_ENGAGEMENT)){

			query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
					+ " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
					+ " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
					+ " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
					+ " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp
					+ " AND a." + DatabaseHelper.COLUMN_ATTENTION + ">" + attentionThreshold;

		}else {
			query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
					+ " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
					+ " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
					+ " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
					+ " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp
					+ " AND a." + DatabaseHelper.COLUMN_ATTENTION + "<" + attentionThreshold;
		}
		return query1;
	}
	public String getSecondFilterQuery(String query1){
		String source = DataFilter.getSource();

		String query2;

		if(source.contains("RangeGraphFragment")){

			query2 = "SELECT AVG(Timestamp) as Timestamp, Latitude, Longitude, MIN(Attention) as MinAttention, MAX(Attention) as MaxAttention, " +
					"COUNT(*) AS Count FROM (" + query1 + ") GROUP BY Latitude, Longitude";

		}else {

			query2 = "SELECT AVG(Timestamp) as Timestamp, Latitude, Longitude, AVG(Attention) as Attention, " +
					"COUNT(*) AS Count FROM (" + query1 + ") GROUP BY Latitude, Longitude";

		}
		return query2;
	}

	public String getMaxFilterQuery(){
//		String query;

//		query = "SELECT Timestamp, Latitude, Longitude, Attention"
//			  + " FROM " + DatabaseHelper.TABLE_ATTENTION 
//			  + " LEFT JOIN " + DatabaseHelper.TABLE_GPS;
		Date date = new Date();
		int earliestAge = (int) (date.getTime()/1000 - DataFilter.getDaysToLoad()*86400);

		String query1 = getFirstFilterQuery(earliestAge);

		String query2 = getSecondFilterQuery(query1);

		return query2;
	}

	public List<double[]> getAllAttentionPoints()
	{
		List<double[]> list = new ArrayList<double[]>();
		double[] attn = new double[2];

		Cursor cursor = database.query(DatabaseHelper.TABLE_ATTENTION, null, null, null, null, null, null);
		cursor.moveToLast();
		while (!cursor.isBeforeFirst())
		{
			attn[0] = cursor.getDouble(1);
			attn[1] = cursor.getDouble(2);
			list.add(attn);
			cursor.moveToPrevious();
		}
		cursor.close();
		return list;
	}

	public void writeToCSV(){

//		System.out.println("Writing to CSV");
//		CSVWriter writer;
		//------------------try
//		try {
//			System.out.println("WritingNEWWRITER");
//			writer = new CSVWriter(new FileWriter("/sdcard/brainwavedata.csv"), '\t');
////			

		//-------------------not this
//			SQLiteStatement stmt = database.compileStatement("SELECT * FROM " + DatabaseHelper.TABLE_RAW);  //+ " WHERE code = ?");
//			//stmt.bindString(1, "US");
//			//stmt.execute();
//			
//			java.sql.ResultSet myResultSet = stmt.execute(); //getFilteredDataSet();
//			
			//----------------------------------------------yes this
//			Connection con = null;//new Connection();
//			
//			Statement aStatement = null; //new Statement();
//			try {
//				System.out.println("WritingCONNECTION");
//				aStatement = con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE );
//			} catch (java.sql.SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			ResultSet myResultSet = null;
//			try {
//				System.out.println("WritingQUERY");
//				myResultSet = aStatement.executeQuery("SELECT * FROM " + DatabaseHelper.TABLE_RAW);
//			} catch (java.sql.SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			try {
//				boolean includeHeaders = true;
//				System.out.println("WritingALL");
//				writer.writeAll(myResultSet, includeHeaders);
//			} catch (java.sql.SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (IOException e1) {
//			System.out.println("WritingNEWWRITER FAILED");
//
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}

//	private void exportChartCsv() {
//        mDatabaseHelper = new DatabaseHelper(this);
//        try {
//            mWriter = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/charts.csv"));
//            mChartList = mDatabaseHelper.getChartsList();
//            String[] mExportChartHeaders = {
//                    "Chart ID", 
//                    "User ID", 
//                    "Patient ID", 
//                    "First Name", 
//                    "Last Name", 
//                    "Date of Birth", 
//                    "Telephone Number", 
//                    "Photo Path", 
//                    "TimeStamp", 
//                    "Questions Completed"
//            };
//
//            mWriter.writeNext(mExportChartHeaders);
//
//            for (ChartTable chartTable : mChartList) {
//                mId = String.valueOf(chartTable.getId());
//                mUserId = chartTable.getUserId();
//                mPatientId = chartTable.getPatientId();
//                mFirstName = chartTable.getFirstName();
//                mLastName = chartTable.getLastName();
//                mDateOfBirth = chartTable.getDob();
//                mTelephoneNumber = chartTable.getPhone();
//                mPhotoPath = chartTable.getPhoto();
//                mTimeStamp = chartTable.getTimeStamp();
//                mQuestionsCompleted = String.valueOf(chartTable.getQuestionsCompleted());
//
//                String[] mExportChart = {mId, mUserId, mPatientId, mFirstName, mLastName, mDateOfBirth, mTelephoneNumber, mPhotoPath, mTimeStamp, mQuestionsCompleted};
//                mWriter.writeNext(mExportChart);
//            }
//            mWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mDatabaseHelper.close();
//    }

	public void clearDatabase()
	{
		database.delete(DatabaseHelper.TABLE_EEG, null, null);
		database.delete(DatabaseHelper.TABLE_HR, null, null);
		database.delete(DatabaseHelper.TABLE_ATTENTION, null, null);
		database.delete(DatabaseHelper.TABLE_RAW, null, null);
		database.delete(DatabaseHelper.TABLE_GPS, null, null);
	}
}
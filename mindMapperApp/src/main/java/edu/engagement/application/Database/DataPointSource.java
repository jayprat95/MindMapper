package edu.engagement.application.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.R;
import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.EEGDataPoint;
import edu.engagement.application.utils.PlacePhotoUtils;
import edu.engagement.application.utils.SessionLocation;
import edu.engagement.application.utils.Session;
import edu.engagement.thrift.EegAttention;
import edu.engagement.thrift.EegPower;
import edu.engagement.thrift.EegRaw;
import edu.engagement.thrift.HeartRate;

public class DataPointSource {

    public static final String SHOW_HIGH_ENGAGEMENT = "HIGH";
    public static final String SHOW_LOW_ENGAGEMENT = "LOW";
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;
    /*private String[] allColumns =
    { DatabaseHelper.COLUMN_TIMESTAMP, DatabaseHelper.COLUMN_HEARTRATE, DatabaseHelper.COLUMN_ALPHA,
			DatabaseHelper.COLUMN_BETA, DatabaseHelper.COLUMN_THETA, DatabaseHelper.COLUMN_ATTENTION,
			DatabaseHelper.COLUMN_CH1, DatabaseHelper.COLUMN_CH2, DatabaseHelper.COLUMN_CH3, DatabaseHelper.COLUMN_CH4,
			DatabaseHelper.COLUMN_CH5, DatabaseHelper.COLUMN_CH6, DatabaseHelper.COLUMN_CH7, DatabaseHelper.COLUMN_CH8 };*/

    public DataPointSource(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public DataPoint createDataPointEEG(long timeStamp, int gpsKey, double alpha, double alpha_1, double alpha_2, double beta, double beta_1, double beta_2, double theta) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
        values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
        values.put(DatabaseHelper.COLUMN_ALPHA_AVERAGE, alpha);
        values.put(DatabaseHelper.COLUMN_ALPHA1, alpha_1);
        values.put(DatabaseHelper.COLUMN_ALPHA2, alpha_2);
        values.put(DatabaseHelper.COLUMN_BETA_AVERAGE, beta);
        values.put(DatabaseHelper.COLUMN_BETA1, beta_1);
        values.put(DatabaseHelper.COLUMN_BETA2, beta_2);
        values.put(DatabaseHelper.COLUMN_THETA, theta);
        double pope = beta/(alpha + theta);
        values.put(DatabaseHelper.COLUMN_POPE, pope);

        database.insert(DatabaseHelper.TABLE_EEG, null, values);

        return new DataPoint(0, "", "",timeStamp, 0, alpha, alpha_1, alpha_2, beta, beta_1, beta_2, theta, pope, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0 ,0);
    }

//    public DataPoint createDataPointHR(long timeStamp, int gpsKey, double hr) {
//        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
//        values.put(DatabaseHelper.COLUMN_GPS_KEY, gpsKey);
//        values.put(DatabaseHelper.COLUMN_HEARTRATE, hr);
//
//        database.insert(DatabaseHelper.TABLE_HR, null, values);
//
//        return new DataPoint(timeStamp, hr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, gpsKey, 0, 0, 0);
//    }

    public DataPoint createDataPointAttention(int sessionId, int attention, long timeStamp, int day, int month) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SESSION_ID, sessionId);
        values.put(DatabaseHelper.COLUMN_ATTENTION, attention);
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
        values.put(DatabaseHelper.COLUMN_DAY, day);
        values.put(DatabaseHelper.COLUMN_MONTH, month);


        database.insert(DatabaseHelper.TABLE_ATTENTION, null, values);

        return new DataPoint(sessionId, "", "",timeStamp, 0, 0, 0, 0, 0, 0, 0, 0, 0, attention, "",0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0);
    }

    public DataPoint createDataPointAnnotation(int sessionId, long timeStamp, String annotation, int attention){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SESSION_ID, sessionId);
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, timeStamp);
        values.put(DatabaseHelper.COLUMN_USER_ANNOTATION, annotation);
        values.put(DatabaseHelper.COLUMN_SUBJECTIVE_ATTENTION, attention);

        database.insert(DatabaseHelper.TABLE_ANNOTATION, null, values);

        return new DataPoint(sessionId, "", "",timeStamp, 0, 0, 0, 0, 0, 0, 0, 0, 0, attention, annotation, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0 , 0 ,0);
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
                                        double ch6, double ch7, double ch8) {
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

        return new DataPoint(0, "","",timeStamp, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "",ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, gpsKey, 0, 0, 0, 0);
    }

    public DataPoint createDataPointGps(double latitude, double longitude, String locationName) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
        values.put(DatabaseHelper.COLUMN_LOCATION_NAME, locationName);

        database.insert(DatabaseHelper.TABLE_GPS, null, values);

        return new DataPoint(0, "",locationName,0, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, latitude, longitude, 0, 0);
    }

    public DataPoint createDataPointSession(int sessionId, String activityName,String locationName){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SESSION_ID, sessionId);
        values.put(DatabaseHelper.COLUMN_ACTIVITY_NAME, activityName);
        values.put(DatabaseHelper.COLUMN_LOCATION_NAME, locationName);

        database.insert(DatabaseHelper.TABLE_SESSION, null, values);

        return new DataPoint(sessionId, activityName,locationName,0, 0, 0, 0, 0, 0, 0, 0, 0,0, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Load preexisting attention data to the database, so we don't need eeg to load data to phone. TODO: remove this after project is finish
     *
     * @param file the debug csv file containing the attention data
     * @return if load data success
     */
    public boolean loadDebugAttentionDataSets(InputStream file) {
        boolean somethingIsWrong = false;

//        FileReader file = null;
//        try {
//            file = new FileReader("EEG_table_attention_data.csv");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.w(DataPointSource.class.getName(), "!!!CSV file not found");
//            somethingIsWrong = true;
//        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(file));
        String line = "";

        try {
            while ((line = buffer.readLine()) != null) {

                ContentValues values = new ContentValues();
                String[] str = line.split(";");
                values.put(DatabaseHelper.COLUMN_TIMESTAMP, /*timeStamp*/str[0]);
                values.put(DatabaseHelper.COLUMN_GPS_KEY, /*gpsKey*/str[1]);
                values.put(DatabaseHelper.COLUMN_ATTENTION, /*attention*/str[2]);
                values.put(DatabaseHelper.COLUMN_MONTH, /*month*/str[3]);
                values.put(DatabaseHelper.COLUMN_DAY, /*day*/str[4]);

                database.insert(DatabaseHelper.TABLE_ATTENTION, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(DataPointSource.class.getName(), "!!!buffer.readLine IO exception");
            somethingIsWrong = true;
        }
        return somethingIsWrong;
    }

    /**
     * Load preexisting gps data to the database, so we don't need eeg to load data to phone. TODO: remove this after project is finish
     *
     * @param file the debug csv file containing the gps data
     * @return if load data success
     */
    public boolean loadDebugGPSDataSets(InputStream file) {
//        int gpsKey = 2;
//        double lat = 37.2250322 + (Math.random() * 0.006);
//        double lon = -80.428961 + (Math.random() * 0.01429);
//        int accuracy = 1;
//
//        int ct = 41;
//        while (ct > 0) {
//            this.createDataPointGps(System.currentTimeMillis(), gpsKey, lat, lon, accuracy);
//            ct --;
//        }
        boolean somethingIsWrong = false;

//        FileReader file = null;
//        try {
//            file = new FileReader("EEG_table_attention_data.csv");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.w(DataPointSource.class.getName(), "!!!CSV file not found");
//            somethingIsWrong = true;
//        }
        BufferedReader buffer = new BufferedReader(new InputStreamReader(file));
        String line = "";

        try {
            while ((line = buffer.readLine()) != null) {

                ContentValues values = new ContentValues();
                String[] str = line.split(";");
                values.put(DatabaseHelper.COLUMN_GPS_KEY, /*gpsKey*/str[0]);
                values.put(DatabaseHelper.COLUMN_TIMESTAMP, /*timeStamp*/str[1]);
                values.put(DatabaseHelper.COLUMN_LATITUDE, /*latitude*/str[2]);
                values.put(DatabaseHelper.COLUMN_LONGITUDE, /*longitude*/str[3]);
                values.put(DatabaseHelper.COLUMN_ACCURACY, /*accuracy*/str[4]);

                database.insert(DatabaseHelper.TABLE_GPS, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(DataPointSource.class.getName(), "!!!buffer.readLine IO exception");
            somethingIsWrong = true;
        }
        return somethingIsWrong;
    }

    /**
     * Check if we need to call loadDebugGPSDataSets and loadDebugAttentionDataSets by checking if there is already data in the database
     * TODO: remove this after project is finish
     *
     * @return
     */
    public boolean doWeNeedMoreDebugData() {
        String query;
        int day = 21;
        //selects the attention level for every timestamp
        query = "SELECT table_attention.Timestamp, table_attention.Attention, table_gps.Latitude, table_gps.Longitude, table_attention.day, table_attention.month FROM "
                + DatabaseHelper.TABLE_ATTENTION
                + "  INNER JOIN table_gps ON table_attention.gps_key=table_gps.gps_key where day = " + day + ";";

        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToLast();
        boolean dataNotExist = cursor.isBeforeFirst();
        cursor.close();
        if (dataNotExist) /* no data returned, we need debug data*/ {
            return true;
        } else {  // we have data, don't need more debug data
            return false;
        }
    }

    public void deleteDataPoint(DataPoint point) {
        long timestamp = point.getTimeStamp();
        if (point.getAlpha() != 0) {
            database.delete(DatabaseHelper.TABLE_EEG, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
            System.out.println("DataPoint deleted with timestamp: " + timestamp);
        } else if (point.getHeartRate() != 0) {
            database.delete(DatabaseHelper.TABLE_HR, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
            System.out.println("DataPoint deleted with timestamp: " + timestamp);
        } else if (point.getAttention() != 0) {
            database.delete(DatabaseHelper.TABLE_ATTENTION, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
            System.out.println("DataPoint deleted with timestamp: " + timestamp);
        } else if (point.getCh1() != 0) {
            database.delete(DatabaseHelper.TABLE_RAW, DatabaseHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
            System.out.println("DataPoint deleted with timestamp: " + timestamp);
        }

    }

    public Bitmap loadSessionPhoto(int sessionId) {
        String[] columns = { DatabaseHelper.COLUMN_SESSION_PHOTO };

        String selection = DatabaseHelper.COLUMN_SESSION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_SESSION_PHOTO,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            byte[] imageData = cursor.getBlob(0);
            cursor.close();
            return PlacePhotoUtils.getImage(imageData);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return null;
    }

    public void saveSessionPhoto(int sessionId, Bitmap image) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_SESSION_ID, sessionId);
        cv.put(DatabaseHelper.COLUMN_SESSION_PHOTO, PlacePhotoUtils.getBytes(image));

        database.insert(DatabaseHelper.TABLE_SESSION_PHOTO, null, cv);
    }

    /*----------------Retrive data from Database------------------*/


    /**
     * Loads and returns a list of sessions from the database that occur between t1 and t2.
     * @return
     */
    public List<Session> getSessionsInTimeRange(int day) {
        Integer[] sessionIds = getSessionIdsInTimeRange(day);

        List<Session> sessions = new ArrayList<>();

        for (int sessionId : sessionIds) {
            Session s = loadSessionData(sessionId);
            sessions.add(s);
        }
        return sessions;
    }

    /**
     * Loads and returns session data for a given session id.
     * @param sessionId
     * @return
     */
    public Session loadSessionData(int sessionId) {
        String[] columns = { DatabaseHelper.COLUMN_LOCATION_NAME,
                                DatabaseHelper.COLUMN_ACTIVITY_NAME
        };

        String selection = DatabaseHelper.COLUMN_SESSION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_SESSION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();

        String locationName = cursor.getString(0);
        String activityName = cursor.getString(1);

        SessionLocation location = loadLocation(locationName);

        Bitmap sessionImage = loadSessionPhoto(sessionId);

        if (sessionImage == null) {
            sessionImage = PlacePhotoUtils
                    .decodeSampledBitmapFromResource(context.getResources(), R.drawable.mcbryde, 400, 200);
        }
        Session s = new Session(sessionId, sessionImage, activityName, location);

        s.addAnnotations(loadAnnotationData(sessionId));
        s.addDataPoints(loadEEGData(sessionId));

        return s;
    }

    public List<Annotation> loadAnnotationData(int sessionId) {
        String[] columns = {    DatabaseHelper.COLUMN_TIMESTAMP,
                                DatabaseHelper.COLUMN_USER_ANNOTATION,
                                DatabaseHelper.COLUMN_SUBJECTIVE_ATTENTION  };

        String selection = DatabaseHelper.COLUMN_SESSION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ANNOTATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_TIMESTAMP);

        List<Annotation> annotations = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timeStamp = cursor.getLong(0);
            String text = cursor.getString(1);
            float attentionLevel = cursor.getFloat(2);

            annotations.add(new Annotation(text, attentionLevel, timeStamp));

            cursor.moveToNext();
        }

        return annotations;
    }

    public List<EEGDataPoint> loadEEGData(int sessionId) {
        String[] columns = { DatabaseHelper.COLUMN_TIMESTAMP, DatabaseHelper.COLUMN_ATTENTION };

        String selection = DatabaseHelper.COLUMN_SESSION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(sessionId) };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ATTENTION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_TIMESTAMP);

        List<EEGDataPoint> dataPoints = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long timeStamp = cursor.getLong(0);
            float attention = cursor.getFloat(1);

            dataPoints.add(new EEGDataPoint(timeStamp, attention));

            cursor.moveToNext();
        }

        return dataPoints;
    }

    public SessionLocation loadLocation(String locationName) {
        String[] columns = {    DatabaseHelper.COLUMN_LONGITUDE, DatabaseHelper.COLUMN_LONGITUDE };

        String selection = DatabaseHelper.COLUMN_LOCATION_NAME + " = ?";
        String[] selectionArgs = { locationName };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_GPS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        cursor.moveToFirst();
        double longitude = cursor.getDouble(0);
        double latitude = cursor.getDouble(1);

        return new SessionLocation(locationName, latitude, longitude);
    }

    /**
     * Finds session ids that fall within the given range of t1 and t2
     * @param day
     * @return
     */
    public Integer[] getSessionIdsInTimeRange(int day) {

        String[] columns = { DatabaseHelper.COLUMN_SESSION_ID };
        String selection = DatabaseHelper.COLUMN_DAY + " = ?";
        String[] selectionArgs = { String.valueOf(day) };

        Cursor cursor = database.query(
                true,
                DatabaseHelper.TABLE_ATTENTION,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);

        List<Integer> sessionIds = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sessionIds.add(cursor.getInt(0));
            cursor.moveToNext();
        }

        return sessionIds.toArray(new Integer[sessionIds.size()]);
    }


    public List<EegPower> getAllDataPointsEEG() {
        List<EegPower> points = new ArrayList<EegPower>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_EEG, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    public List<HeartRate> getAllDataPointsHR() {
        List<HeartRate> points = new ArrayList<HeartRate>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_HR, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    public List<EegAttention> getAllDataPointsAttention() {
        List<EegAttention> points = new ArrayList<EegAttention>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_ATTENTION, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    public List<EegRaw> getAllDataPointsRaw() {
        List<EegRaw> points = new ArrayList<EegRaw>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_RAW, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EegRaw point = new EegRaw();
            point.setMillisecondTimeStamp(String.valueOf(cursor.getLong(0)));
            point.setRaw(cursor.getInt(1));//index 1 is gpsKey..
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return points;
    }

    public List<double[]> getAllDataPointsGPS() {
        List<double[]> list = new ArrayList<double[]>();
        double[] gps = new double[5];
        Cursor cursor = database.query(DatabaseHelper.TABLE_GPS, null, null, null, null, null, null);
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()) {
            /*
			* 1 - Lat
			* 2 - Lon
			* 3 - location
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

    public List<double[]> getMaxDataset() {
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

    public List<String[]> getMapDataset() {
        List<String[]> list = new ArrayList<String[]>();
        String query;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);

        // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
        int month = (c.get(Calendar.MONTH) + 1);

        //selects the lat, long and AVERAGE attention level
        //associated with each unique gpskey
        query = "SELECT table_attention.day, " +
                "table_attention.Session_Id, " +
                "AVG(table_attention.Attention), " +
                "table_gps.LocationName, " +
                "table_gps.Latitude, " +
                "table_gps.Longitude, " +
                "AVG(table_annotation.SubjectiveAttention), " +
                "table_annotation.Session_Id, " +
                "table_session.Session_Id, " +
                "table_session.LocationName"
                + " FROM " + DatabaseHelper.TABLE_ATTENTION + ", " + DatabaseHelper.TABLE_GPS + ", " + DatabaseHelper.TABLE_ANNOTATION
                + " INNER JOIN " + DatabaseHelper.TABLE_SESSION
                + " ON table_attention.day = " + day + " AND table_attention.Session_Id = table_session.Session_Id AND table_gps.LocationName = table_session.LocationName AND table_attention.Session_Id = table_annotation.Session_Id"
                + " GROUP BY table_session.Session_Id";

        Cursor cursor = database.rawQuery(query, null);

        /*
         * 0 - session id
         * 1 - Attention
         * 2 - LocationName
         * 3 - lat
         * 4 - lon
         */
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            String[] data = new String[6];
            data[0] = cursor.getInt(1) + "";
            data[1] = cursor.getDouble(2) + "";
            data[2] = cursor.getString(3) + "";
            data[3] = cursor.getDouble(4) + "";
            data[4] = cursor.getDouble(5) + "";
            data[5] = cursor.getDouble(6) + "";

            list.add(data);
            cursor.moveToPrevious();
        }
        cursor.close();
        return list;
    }

    public List<double[]> getDayGraphDataset() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
//        int day = 21; /* attention data are all marked at 21th TODO: this is the day for debugging data, remove after finishing the application*/

        List<double[]> list = new ArrayList<double[]>();
        String query;

        //selects the attention level for every timestamp
        query = "SELECT table_attention.Timestamp, table_attention.Attention, table_gps.Latitude, table_gps.Longitude, table_attention.day, table_attention.month FROM "
                + DatabaseHelper.TABLE_ATTENTION
                + "  INNER JOIN table_gps ON table_attention.gps_key=table_gps.gps_key where day = " + day + ";";

        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
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

    public List<double[]> getFilteredDataset() {
        Date date = new Date();
        int earliestAge = (int) (date.getTime() / 1000 - DataFilter.getDaysToLoad() * 86400);

        String attentionFilter = DataFilter.getFilter();
        double attentionThreshold = DataFilter.getAttentionThreshold();

        List<double[]> list = new ArrayList<double[]>();

        String query1 = getFirstFilterQuery(earliestAge);

        String query2 = getSecondFilterQuery(query1);

        Cursor cursor = database.rawQuery(query2, null);

        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
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

    public String getFirstFilterQuery(int earliestTimestamp) {
        String query1;

        String attentionFilter = DataFilter.getFilter();
        double attentionThreshold = DataFilter.getAttentionThreshold();

        if (attentionFilter == null || attentionFilter.equals("")) {

            query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
                    + " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
                    + " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
                    + " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
                    + " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp;

        } else if (attentionFilter.equals(SHOW_HIGH_ENGAGEMENT)) {

            query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
                    + " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
                    + " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
                    + " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
                    + " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp
                    + " AND a." + DatabaseHelper.COLUMN_ATTENTION + ">" + attentionThreshold;

        } else {
            query1 = "SELECT a.Timestamp, Latitude, Longitude, Attention"
                    + " FROM " + DatabaseHelper.TABLE_ATTENTION + " AS a "
                    + " INNER JOIN " + DatabaseHelper.TABLE_GPS + " AS b "
                    + " ON a." + DatabaseHelper.COLUMN_GPS_KEY + "=b." + DatabaseHelper.COLUMN_GPS_KEY
                    + " WHERE a." + DatabaseHelper.COLUMN_TIMESTAMP + " > " + earliestTimestamp
                    + " AND a." + DatabaseHelper.COLUMN_ATTENTION + "<" + attentionThreshold;
        }
        return query1;
    }

    public String getSecondFilterQuery(String query1) {
        String source = DataFilter.getSource();

        String query2;

        if (source.contains("RangeGraphFragment")) {

            query2 = "SELECT AVG(Timestamp) as Timestamp, Latitude, Longitude, MIN(Attention) as MinAttention, MAX(Attention) as MaxAttention, " +
                    "COUNT(*) AS Count FROM (" + query1 + ") GROUP BY Latitude, Longitude";

        } else {

            query2 = "SELECT AVG(Timestamp) as Timestamp, Latitude, Longitude, AVG(Attention) as Attention, " +
                    "COUNT(*) AS Count FROM (" + query1 + ") GROUP BY Latitude, Longitude";

        }
        return query2;
    }

    public String getMaxFilterQuery() {
//		String query;

//		query = "SELECT Timestamp, Latitude, Longitude, Attention"
//			  + " FROM " + DatabaseHelper.TABLE_ATTENTION 
//			  + " LEFT JOIN " + DatabaseHelper.TABLE_GPS;
        Date date = new Date();
        int earliestAge = (int) (date.getTime() / 1000 - DataFilter.getDaysToLoad() * 86400);

        String query1 = getFirstFilterQuery(earliestAge);

        String query2 = getSecondFilterQuery(query1);

        return query2;
    }

    public List<double[]> getAllAttentionPoints() {
        List<double[]> list = new ArrayList<double[]>();
        double[] attn = new double[2];

        Cursor cursor = database.query(DatabaseHelper.TABLE_ATTENTION, null, null, null, null, null, null);
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            attn[0] = cursor.getDouble(1);
            attn[1] = cursor.getDouble(2);
            list.add(attn);
            cursor.moveToPrevious();
        }
        cursor.close();
        return list;
    }

    public void writeToCSV() {

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

    public void clearDatabase() {
        database.delete(DatabaseHelper.TABLE_EEG, null, null);
        database.delete(DatabaseHelper.TABLE_HR, null, null);
        database.delete(DatabaseHelper.TABLE_ATTENTION, null, null);
        database.delete(DatabaseHelper.TABLE_RAW, null, null);
        database.delete(DatabaseHelper.TABLE_GPS, null, null);
    }
}
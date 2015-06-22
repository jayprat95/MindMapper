package edu.engagement.application.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_HR = "table_hr";
    public static final String TABLE_GPS = "table_gps";
    public static final String TABLE_EEG = "table_eeg";
    public static final String TABLE_ATTENTION = "table_attention";
    public static final String TABLE_RAW = "table_raw";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_ALPHA = "Alpha";
    public static final String COLUMN_BETA = "Beta";
    public static final String COLUMN_THETA = "Theta";
    public static final String COLUMN_HEARTRATE = "Heartrate";
    public static final String COLUMN_ATTENTION = "Attention";
    public static final String COLUMN_CH1 = "Ch1";
    public static final String COLUMN_CH2 = "Ch2";
    public static final String COLUMN_CH3 = "Ch3";
    public static final String COLUMN_CH4 = "Ch4";
    public static final String COLUMN_CH5 = "Ch5";
    public static final String COLUMN_CH6 = "Ch6";
    public static final String COLUMN_CH7 = "Ch7";
    public static final String COLUMN_CH8 = "Ch8";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_ACCURACY = "Accuracy";
    public static final String COLUMN_GPS_KEY = "gps_key";


    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";


    private static final String DATABASE_NAME = "commments.db";
    private static final int DATABASE_VERSION = 9;

    // Database creation sql statement
    private static final String DATABASE_CREATE_EEG = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EEG + "(" + COLUMN_TIMESTAMP + " INTEGER, "
            + COLUMN_GPS_KEY + " INTEGER, "
            + COLUMN_ALPHA + " REAL, "
            + COLUMN_BETA + " REAL, "
            + COLUMN_THETA + " REAL"
            + " );";

    private static final String DATABASE_CREATE_HR = "CREATE TABLE IF NOT EXISTS "
            + TABLE_HR + "(" + COLUMN_TIMESTAMP + " INTEGER, "
            + COLUMN_GPS_KEY + " INTEGER, "
            + COLUMN_HEARTRATE + " REAL"
            + " );";

//  private static final String DATABASE_CREATE_ATTENTION = "CREATE TABLE IF NOT EXISTS "
//		  + TABLE_ATTENTION + "(" + COLUMN_TIMESTAMP + " INTEGER, "
//		  + COLUMN_GPS_KEY + " INTEGER, "
//		  + COLUMN_ATTENTION + " REAL"
//	      + " );";

    private static final String DATABASE_CREATE_ATTENTION = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ATTENTION + "(" + COLUMN_TIMESTAMP + " INTEGER, "
            + COLUMN_GPS_KEY + " INTEGER, "
            + COLUMN_ATTENTION + " REAL, "
            + COLUMN_MONTH + " INTEGER, "
            + COLUMN_DAY + " INTEGER"
            + " );";
    private static final String DATABASE_CREATE_RAW = "CREATE TABLE IF NOT EXISTS "
            + TABLE_RAW + "(" + COLUMN_TIMESTAMP + " INTEGER, "
            + COLUMN_GPS_KEY + " INTEGER, "
            + COLUMN_CH1 + " REAL, "
            + COLUMN_CH2 + " REAL, "
            + COLUMN_CH3 + " REAL, "
            + COLUMN_CH4 + " REAL, "
            + COLUMN_CH5 + " REAL, "
            + COLUMN_CH6 + " REAL, "
            + COLUMN_CH7 + " REAL, "
            + COLUMN_CH8 + " REAL"
            + " );";

    private static final String DATABASE_CREATE_GPS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_GPS + "("
            + COLUMN_GPS_KEY + " INTEGER, "
            + COLUMN_TIMESTAMP + " INTEGER, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL, "
            + COLUMN_ACCURACY + " REAL"
            + " );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_EEG);
        database.execSQL(DATABASE_CREATE_EEG);
        database.execSQL(DATABASE_CREATE_GPS);
        database.execSQL(DATABASE_CREATE_HR);
        database.execSQL(DATABASE_CREATE_ATTENTION);
        database.execSQL(DATABASE_CREATE_RAW);
        database.execSQL(DATABASE_CREATE_GPS);
        database.execSQL(DATABASE_CREATE_HR);
        database.execSQL(DATABASE_CREATE_ATTENTION);
        database.execSQL(DATABASE_CREATE_RAW);

        System.out.println("Created DBs if necessary");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is called when you've changed the database version and the user opens the app for the first time after that happens. You can put code here that can rebuild the database with new structure
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
//        Log.i("!!!!!"+ DatabaseHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
        Log.i("!"+ "-----", "Let see if this works!!!!!!!!!!!!!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EEG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAW);
        onCreate(db);
    }

} 
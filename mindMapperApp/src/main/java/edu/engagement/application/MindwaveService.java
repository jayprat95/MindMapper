package edu.engagement.application;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;

import edu.engagement.application.Database.DataPointSource;

//import com.google.android.gms.location.LocationListener;

public class MindwaveService extends Service {

    public static final String GPS_KEY = "gpskey";
    final boolean rawEnabled = true;
    private final Handler handler = new Handler(new HandlerClass());
    private Intent handlerIntent = new Intent("android.intent.action.MAIN");
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    BluetoothAdapter adapter = null;
    TGDevice tgDevice = null;
    DataPointSource dataSource = null;
    int gpsKey = 0;
    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode = START_NOT_STICKY;
    /**
     * interface for clients that bind
     */
    IBinder mBinder;
    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("Status Changed");
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        /**** DATABASE STUFF ****/
        dataSource = new DataPointSource(this.getApplicationContext());
        dataSource.open();

        initBluetooth();
        connectToMindwave();
//		  generateRandomData();
//          generateDummyDataPoints();
//
        // Register the listener with the Location Manager to receive location updates
        // need to change this to best provider
//		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, locationListener);
//		  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 100, locationListener);

        System.out.println("Requested Location Updates from Location Manager");

        return mStartMode;
    }

//	    //Generate temporary GPS test data
//		public void generateRandomData()
//		{
//			double lat;
//			double lon;
//			int accuracy;
//
//			gpsKey = (int)(Math.random() * 1000);
//
//			lat = 37.2250322 + (Math.random() * 0.006);
//			lon = -80.428961 + (Math.random() * 0.01429);
//			accuracy = 1; //Temporary (isn't this whole function temporary? :) )
//			dataSource.createDataPointGps(System.currentTimeMillis(), gpsKey, lat, lon, accuracy);
//			System.out.println("Added GPS Data Point key: " + gpsKey);
//		}

    private void initBluetooth() {
        /**** BLUETOOTH STUFF ****/
        // Sending a message to android to initiate a pairing request
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");

			/*
             * Registering a new BTBroadcast receiver from the Main Activity context
			 * with pairing request event
			 */
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);

        // Registering the BTBondReceiver in the application that the status of
        // the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);
    }

    public void connectToMindwave() {

        adapter = BluetoothAdapter.getDefaultAdapter();
//			if (!EEGConnected)
//			{
        if (adapter != null) {
            if (tgDevice == null) {
                tgDevice = new TGDevice(adapter, handler);
            }
            if (tgDevice.getState() != TGDevice.STATE_CONNECTING
                    && tgDevice.getState() != TGDevice.STATE_CONNECTED)
                tgDevice.connect(rawEnabled);
        }
//			}
    }

    public void makeUseOfNewLocation(Location location) {
        //check if location is in same "place" as older gpsKey
        //if not get next gpsKey
        //	storeGpsKey

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        System.out.println(msg);

        gpsKey = readGpsKey();
        //setting the value to confirm sharedPreferences stored
        gpsKey = storeGpsKey(gpsKey + 1);

        int accuracy = 1;

        dataSource.createDataPointGps(System.currentTimeMillis(), gpsKey, location.getLatitude(), location.getLongitude(), accuracy);

        System.out.println("Set gpsKey to: " + gpsKey);
    }

    // Store baseline in persistent storage
    public int storeGpsKey(int key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        //SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt(GPS_KEY, key);
        editor.commit();
        System.out.println("Stored gpsKey value: " + key);
        return key;
    }

    // Read baseline from persistent storage
    public int readGpsKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int key = prefs.getInt(GPS_KEY, -1);
        System.out.println("Retrieved gpsKey value: " + key);

        //if there is no gpsKey stored, set initial to 1
        if (key == -1) {
            key = 1;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt(GPS_KEY, key);
            editor.commit();
            System.out.println("Set gpsKey value to: " + key);
        }

        return key;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Service Bind", Toast.LENGTH_LONG).show();
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "Service Unbind", Toast.LENGTH_LONG).show();
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Toast.makeText(this, "Service Rebind", Toast.LENGTH_LONG).show();
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        // Remove the listener previously added
        locationManager.removeUpdates(locationListener);
        System.out.println("Stop listening for location updates");

        dataSource.close();
        System.out.println("Close service DB connection");
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE")
                        .toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[]
                        {String.class});
                byte[] pin = (byte[]) m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class[]
                        {pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (SecurityException e1) {
                // Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("Bond ApplicationState", "BOND_STATED = " + device.getBondState());
        }
    }
    private void toastFromHandler(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Internal callback handler class
     */
    class HandlerClass implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            Log.i(App.NAME, "Device State: Idle");
                            break;

                        case TGDevice.STATE_CONNECTING:
                            Log.i(App.NAME, "Device State: Connecting");
                            break;

                        case TGDevice.STATE_CONNECTED:
                            Log.i(App.NAME, "Device State: Connected");
                            Log.i(App.NAME, "Starting device...");
                            tgDevice.start();
                            break;

                        // This state happens when the EEG device cannot be found.
                        // Sources of error:
                        //      - EEG turned off
                        //      - Bluetooth turned off
                        case TGDevice.STATE_NOT_FOUND:
                            Log.i(App.NAME, "Device State: Not found");
                            break;

                        case TGDevice.STATE_NOT_PAIRED:
                            Log.i(App.NAME, "Device State: Not Paired");
                            break;

                        case TGDevice.STATE_DISCONNECTED:
                            Log.i(App.NAME, "Device State: Disconnected");
                            break;
                    }

                    break;

                case TGDevice.MSG_POOR_SIGNAL:
                    Log.i(App.NAME, "Device State: Connected- Poor Signal: " + msg.arg1);
                    break;
                case TGDevice.MSG_RAW_DATA:
                    break;
                //testing this out. does it get heart rate?
                case TGDevice.MSG_HEART_RATE:
                    break;
                //testing this out too.
                case TGDevice.MSG_MEDITATION:
                    break;
                case TGDevice.MSG_ATTENTION:

                    Log.i(App.NAME, "Device State: Connected- Attention: " + msg.arg1);

                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
                    int month = (c.get(Calendar.MONTH) + 1);

//					@SuppressWarnings("deprecation")
//					//int day = date.getDay();
//					@SuppressWarnings("deprecation")
//					//int month = date.getMonth();

                    int att = msg.arg1;
                    if (att != 0) {
                        handlerIntent.putExtra("some_msg", Integer.toString(msg.arg1));
                        MindwaveService.this.sendBroadcast(handlerIntent);
                        dataSource.createDataPointAttention(System.currentTimeMillis(), gpsKey, att, day, month);


                    }

//					switchToFragment(REAL_TIME_TAG);
//					setAttentionText(att);
//					tempCounter++;
////					System.out.println("temprary counter: " + tempCounter);
//
//					if (baselineMode && att != 0) { // Calculate running average
//						baselineTotal += att;
//						baselineNum++;
//					}

                    break;

                // Standard Brain Waves
                case TGDevice.MSG_EEG_POWER:
//					TGEegPower eegPow = (TGEegPower) msg.obj;
//					double alpha = eegPow.highAlpha;
//					double beta = eegPow.highBeta;
//					double theta = eegPow.theta;
//
//					dataSource.createDataPointEEG(System.currentTimeMillis(), gpsKey, alpha, beta, theta);

//					TextView tv = (TextView) findViewById(R.id.EEGText);
//					DecimalFormat df = new DecimalFormat("#.##");
//					tv.setText(df.format((beta / (alpha + theta))));
//					System.out.println("Engagement is " + (beta / (alpha + theta)));
                    break;

                case TGDevice.MSG_RAW_MULTI:
//					TGRawMulti eegRaw = (TGRawMulti) msg.obj;
//					double ch1 = eegRaw.ch1;
//					double ch2 = eegRaw.ch2;
//					double ch3 = eegRaw.ch3;
//					double ch4 = eegRaw.ch4;
//					double ch5 = eegRaw.ch5;
//					double ch6 = eegRaw.ch6;
//					double ch7 = eegRaw.ch7;
//					double ch8 = eegRaw.ch8;
//
//					dataSource.createDataPointRaw(System.currentTimeMillis(), gpsKey, ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8);
                    break;

                default:
                    break;
            }
            return true;
        }
    }

//    /** This is for VTURCS 2015. We were not able to finish, and needed some dummy points. */
//    public void generateDummyDataPoints()
//    {
//
//        // Clear out our data base first
//         //dataSource.clearDatabase();
//
//
//        Calendar c =  Calendar.getInstance();
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
//        int month = (c.get(Calendar.MONTH) + 1);
//
//        Random r = new Random();
//
//        for(int i = 0; i < 200; i ++)
//        {
//            int temp = r.nextInt(99) + 1;
//             /* Dummy Points */
//            dataSource.createDataPointAttention(System.currentTimeMillis(), gpsKey, temp , day, month);
//        }
//
//    }
}
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
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.Eeg.EegListener;
import edu.engagement.application.Eeg.EegState;
import edu.engagement.application.Fragments.RealTimeDataFragment;


public class MindwaveService extends Service {

    public static final String GPS_KEY = "gpskey";
    private final Handler handler = new Handler(new HandlerClass());

    // Listeners for Eeg events
    private List<EegListener> listeners = new ArrayList<>();

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    BluetoothAdapter adapter = null;
    TGDevice tgDevice = null;
    DataPointSource dataSource = null;
    int gpsKey = 0;

    /*
     * interface for clients that bind
     */
    private final IBinder binder = new MindwaveBinder();

    /*
     * whether or not the service is connected to the EEG device
     */
    private boolean connected = false;

    /*
     * whether or not the EEG is in a poor signal state
     */
    private boolean goodSignal = false;

    /*
     * whether or not data is being saved
     */
    private boolean recording = false;

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
        Log.d(App.NAME, "MindwaveService started");

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

        return START_NOT_STICKY;
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
                tgDevice.connect(false);
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


       //dataSource.createDataPointGps(gpsKey, System.currentTimeMillis(), location.getLatitude(), location.getLongitude());

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
        return binder;
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
        Log.d(App.NAME, "MindwaveService destroyed!");

        // Remove the listener previously added
        locationManager.removeUpdates(locationListener);
        Log.d(App.NAME, "Stop listening for location updates");

        dataSource.close();
    }

    /**
     * Allows received data from EEG Device to be saved to the database.
     */
    public void startRecording() {
        recording = true;
    }

    /**
     * Stops received data form EEG Device from being saved to the database.
     */
    public void stopRecording() {
        recording = false;
    }

    /**
     * Adds a listener that will receive eeg state changes and data.
     * @param listener the listener to add
     */
    public void addEegListener(EegListener listener) {
        listeners.add(listener);
    }

    /**
     * Send a state change notification to all eeg listeners
     * @param state the state change
     */
    private void sendStateChangeToListeners(EegState state) {
        for (EegListener listener : listeners) {
            listener.onEegStateChange(state);
        }
    }

    /**
     * Send an attention received notification to all eeg listeners
     * @param attention the attention value
     */
    private void sendAttentionToListeners(int attention) {
        for (EegListener listener : listeners) {
            listener.onEegAttentionReceived(attention);
        }
    }

    /**
     * Determine if the service is connected to the EEG device.
     * @return true if connected, otherwise false
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Determine if the service is receiving good data from the EEG device.
     * @return true if good signal, otherwise false
     */
    public boolean isGoodSignal() {
        return goodSignal;
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

    public class MindwaveBinder extends Binder {
        public MindwaveService getService() {
            return MindwaveService.this;
        }
    }

    /**
     * Internal callback handler class
     */
    class HandlerClass implements Handler.Callback {

        private static final int SIGNAL_QUALITY_THRESHOLD = 50;

        /*
         * The number of good or bad signals in a row to cause a state change.
         */
        private static final int SIGNAL_CONSEC_MAX = 5;

        private int goodSignalConsec = 0;
        private int poorSignalConsec = 0;

        @Override
        public boolean handleMessage(Message msg) {
            Log.d("AAAAAAAAAAAAAA", Thread.currentThread().getName());

            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TGDevice.STATE_CONNECTING:
                            Log.d(App.NAME, "Device State: Connecting");
                            break;

                        case TGDevice.STATE_CONNECTED:
                            Log.d(App.NAME, "Device State: Connected");
                            Log.d(App.NAME, "Starting device...");

                            connected = true;

                            sendStateChangeToListeners(EegState.CONNECTED);
                            tgDevice.start();
                            break;

                        // This state happens when the EEG device cannot be found.
                        // Only happens when starting up EEG connection
                        // Sources of error:
                        //      - EEG turned off
                        //      - Bluetooth turned off
                        case TGDevice.STATE_NOT_FOUND:
                            Log.d(App.NAME, "Device State: Not found");
                            sendStateChangeToListeners(EegState.NOT_FOUND);
                            MindwaveService.this.stopSelf();
                            break;

                        case TGDevice.STATE_NOT_PAIRED:
                            Log.d(App.NAME, "Device State: Not Paired");
                            break;

                        case TGDevice.STATE_DISCONNECTED:
                            Log.d(App.NAME, "Device State: Disconnected");

                            connected = false;

                            sendStateChangeToListeners(EegState.DISCONNECTED);

                            // We're going to restart the service if we try to reconnect
                            MindwaveService.this.stopSelf();
                            break;
                    }

                    break;

                case TGDevice.MSG_POOR_SIGNAL:
// TODO: Implement poor signal handling
//                    if (!recording)
//                        return true;
//
//                    // Lower integer means higher quality
//                    int signalQuality = msg.arg1;
//
//                    if (signalQuality > SIGNAL_QUALITY_THRESHOLD) {
//
//                        poorSignalConsec++;
//
//                        Log.d(App.NAME, "Poor Signal: " + signalQuality + ". Poor signals in a row: " + poorSignalConsec);
//
//                        if (poorSignalConsec >= SIGNAL_CONSEC_MAX) {
//                            goodSignal = false;
//                            sendStateChangeToListeners(EegState.POOR_SIGNAL);
//
//                            Log.d(App.NAME, "Too many consecutive poor signals. Stopping data collection.");
//
//                            // Reset poor signal counter
//                            poorSignalConsec = 0;
//
//                            // Stop collecting data
//                            tgDevice.stop();
//                        }
//                        goodSignalConsec = 0;
//                    } else {
//
//                        goodSignalConsec++;
//
//                        if (goodSignalConsec >= SIGNAL_CONSEC_MAX) {
//                            goodSignal = true;
//                            sendStateChangeToListeners(EegState.GOOD_SIGNAL);
//
//                            Log.d(App.NAME, "")
//                        }
//
//                        // Reset poor signal in a row counter
//                        poorSignalConsec = 0;
//                    }
                    break;
                case TGDevice.MSG_RAW_DATA:
                    break;
                case TGDevice.MSG_ATTENTION:

                    // If we're not recording, don't save data
                    if (!recording)
                        return true;

                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
                    int month = (c.get(Calendar.MONTH) + 1);

                    int att = msg.arg1;
                    if (att != 0) {
                        sendAttentionToListeners(att);


                        // TODO: Need to do this in a separate thread
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        int sharedSessionId = prefs.getInt("sessionId", 0);
                        if(RealTimeDataFragment.sessionId != 0 && RealTimeDataFragment.sessionId == sharedSessionId){
                            dataSource.createDataPointAttention(RealTimeDataFragment.sessionId,  att,System.currentTimeMillis());
                        }
                    }
                    break;

                // Standard Brain Waves
                case TGDevice.MSG_EEG_POWER:
					TGEegPower eegPow = (TGEegPower) msg.obj;
                    double alpha_1 = eegPow.highAlpha;
                    double alpha_2 = eegPow.lowAlpha;
                    double alpha = (alpha_1 + alpha_2) / 2;
                    double beta_1 = eegPow.highBeta;
                    double beta_2 = eegPow.lowBeta;
                    double beta = (beta_1 + beta_2) / 2;
                    double theta = eegPow.theta;
//
                    dataSource.createDataPointEEG(System.currentTimeMillis(), gpsKey, alpha, alpha_1, alpha_2, beta, beta_1, beta_2,theta);

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
}
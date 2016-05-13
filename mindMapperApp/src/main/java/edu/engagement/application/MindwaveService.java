package edu.engagement.application;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.Eeg.EegListener;
import edu.engagement.application.Eeg.EegState;
import edu.engagement.application.Fragments.RecordingFragment;


public class MindwaveService extends Service {

    private final Handler handler = new Handler(new HandlerClass());

    // Listeners for Eeg events
    private List<EegListener> listeners = new ArrayList<>();

    // Acquire a reference to the system SessionLocation Manager
    BluetoothAdapter adapter = null;
    TGDevice tgDevice = null;
    DataPointSource dataSource = null;

    /*
     * interface for clients that bind
     */
    private final IBinder binder = new MindwaveBinder();

    /*
     * whether or not the service is connected to the EEG device
     */
    private boolean connected = false;

    /*
     * whether or not
     */
    private boolean recording = false;

    /*
     *
     */
    private int setAttentionInteval = 1;

    private boolean firstRecord;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(App.NAME, "MindwaveService started");

        firstRecord = true;
        /**** DATABASE STUFF ****/
        dataSource = new DataPointSource(this.getApplicationContext());
        dataSource.open();

        initBluetooth();
        connectToMindwave();

        return START_NOT_STICKY;
    }

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
        if (adapter != null) {
            if (tgDevice == null) {
                tgDevice = new TGDevice(adapter, handler);
            }
            if (tgDevice.getState() != TGDevice.STATE_CONNECTING
                    && tgDevice.getState() != TGDevice.STATE_CONNECTED)
                tgDevice.connect(false);
        }
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

        tgDevice.close();
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
     *
     * @param listener the listener to add
     */
    public void addEegListener(EegListener listener) {
        listeners.add(listener);
    }

    /**
     * Send a state change notification to all eeg listeners
     *
     * @param state the state change
     */
    private void sendStateChangeToListeners(EegState state) {
        Log.d(App.NAME, String.valueOf(listeners.size()));
        for (EegListener listener : listeners) {
            listener.onEegStateChange(state);
        }
    }

    /**
     * Send an attention received notification to all eeg listeners
     *
     * @param attention the attention value
     */
    private void sendAttentionToListeners(int attention) {
        for (EegListener listener : listeners) {
            listener.onEegAttentionReceived(attention);
        }
    }

    /**
     * Determine if the service is connected to the EEG device.
     *
     * @return true if connected, otherwise false
     */
    public boolean isConnected() {
        return connected;
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.getString("android.bluetooth.device.extra.DEVICE"));
            Log.d("BTIntent", b.getString("android.bluetooth.device.extra.PAIRING_VARIANT"));
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.getString("android.bluetooth.device.extra.DEVICE"));
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", String.class);
                byte[] pin = (byte[]) m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", pin.getClass());
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (Exception e1) {
                // Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.getString("android.bluetooth.device.extra.DEVICE"));
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

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_ATTENTION:
                    handleFocusData(msg.arg1);
                    break;
                case TGDevice.MSG_STATE_CHANGE:
                    handleStateChange(msg.arg1);
                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                case TGDevice.MSG_RAW_DATA:
                case TGDevice.MSG_EEG_POWER:
                case TGDevice.MSG_RAW_MULTI:
            }
            return true;
        }
    }

    /**
     * Private method to handle focus score data received from the MindWave. Send focus score to
     * be displayed to the user every third time this method is called and save focus score to the
     * database every time.
     *
     * @param focusLevel the focus score received from the MindWave
     */
    private void handleFocusData(int focusLevel) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);

        // The Calendar function returns the index of the month. (ex: Jan = 0, Feb = 1)
        int month = (c.get(Calendar.MONTH) + 1);

        if (setAttentionInteval == 3) {
            sendAttentionToListeners(focusLevel);
            setAttentionInteval = 1;
        } else {
            setAttentionInteval += 1;
        }

        // TODO: Need to do this in a separate thread
        if (RecordingFragment.sessionId != 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int sharedSessionId = prefs.getInt("sessionId", RecordingFragment.sessionId);
            if (RecordingFragment.sessionId == sharedSessionId) {
                // If we're paused, save a 0 in the database
                if (!recording) {
                    focusLevel = 0;
                }

                dataSource.createDataPointAttention(RecordingFragment.sessionId, focusLevel, System.currentTimeMillis(), day, month);
                Log.v("Recording", "The focusLevel: " + focusLevel);

            }
        }
    }

    /**
     * Private method to handle state changes of the MindWave device.
     *
     * @param state the new state
     */
    private void handleStateChange(int state) {
        switch (state) {
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

                MindwaveService.this.stopSelf();
                break;
        }
    }
}
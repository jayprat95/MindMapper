package edu.engagement.application;

/**
 * Created by Alex on 7/31/2015.
 */
public interface EEGConnectionListener {
    void onBluetoothSuccess();
    void onBluetoothFail();
    void onConnectionSuccess();
    void onConnectionFail();
}

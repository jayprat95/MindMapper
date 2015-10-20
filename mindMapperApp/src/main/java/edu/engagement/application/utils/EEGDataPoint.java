package edu.engagement.application.utils;

import java.util.Date;

/**
 * Created by alex on 10/17/15.
 */
public class EEGDataPoint {
    public final long timeStamp;
    public final float attention;

    public EEGDataPoint(long timeStamp, float attention) {
        this.timeStamp = timeStamp;
        this.attention = attention;
    }
}

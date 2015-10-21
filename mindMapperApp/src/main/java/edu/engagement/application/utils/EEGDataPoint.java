package edu.engagement.application.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 10/17/15.
 */
public class EEGDataPoint {

    private static final String DATE_FORMAT = "hh:mm a";

    public final long timeStamp;
    public final float attention;

    public EEGDataPoint(long timeStamp, float attention) {
        this.timeStamp = timeStamp;
        this.attention = attention;
    }

    public String timeStampFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        return formatter.format(new Date(timeStamp));
    }
}

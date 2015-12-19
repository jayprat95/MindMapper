package edu.engagement.application.utils;

import android.text.format.DateUtils;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 11/26/15.
 */
public class TimeUtils {

    public static String getSessionTimeFormatted(Session s) {
        String elapsed = getElapsedTimeFormatted(s);

        return getStartTimeFormatted(s) + " for " + getElapsedTimeFormatted(s);
    }

    private static String getStartTimeFormatted(Session s) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        return sdf.format(new Date(s.getStartTime()));
    }

    private static String getStopTimeFormatted(Session s) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        return sdf.format(new Date(s.getStopTime()));
    }

    public static String getElapsedTimeFormatted(Session s) {
            long timeDiff = s.getStopTime() - s.getStartTime();

            if (timeDiff > DateUtils.HOUR_IN_MILLIS) {
                return DurationFormatUtils.formatDuration(timeDiff, "H hr m ") + "min";
            }
            return DurationFormatUtils.formatDuration(timeDiff, "m ") + "min";
    }
}

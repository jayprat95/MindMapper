package edu.engagement.application.utils;

import java.util.Date;

/**
 * Created by alex on 10/17/15.
 */
public class GraphDataPoint {
    public final Date time;
    public final float attention;

    public GraphDataPoint(Date time, float attention) {
        this.time = time;
        this.attention = attention;
    }
}

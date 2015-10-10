package edu.engagement.application;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *  Data representation for each individual card for each cardView.
 */
public class EventSummary {

    private String location;

    private Date start;
    private Date stop;

    private double          eegData;
    private AttentionLevel  selfReportLevel;

    public EventSummary(String location, long start, long stop, AttentionLevel selfReportLevel, double eegData) {
        this.location = location;
        this.start = new Date(start);
        this.stop = new Date(stop);
        this.selfReportLevel = selfReportLevel;
        this.eegData = eegData;
    }

    public Date getStartDate() {
        return start;
    }

    public Date getStopDate() {
        return stop;
    }

    public String getTimeRangeFormatted(String format, String separator) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);

        return fmt.format(start) + separator + fmt.format(stop);
    }

    public AttentionLevel getSelfReportedAttention() {
        return selfReportLevel;
    }

    public double getEegAttention() {
        return eegData;
    }

    public String getLocation() {
        return this.location;
    }
}
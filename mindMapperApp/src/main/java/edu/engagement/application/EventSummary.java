package edu.engagement.application;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *  Data representation for each individual card for each cardView.
 */
public class EventSummary {

    private String location;
    private String activityName;

    private Date start;
    private Date stop;

    private double          eegData;
    private AttentionLevel  selfReportLevel;
    private int sessionId;

    public EventSummary(int sessionId, String activityName, String location, long start, long stop, AttentionLevel selfReportLevel, double eegData) {
        this.sessionId = sessionId;
        this.activityName = activityName;
        this.location = location;
        this.start = new Date(start);
        this.stop = new Date(stop);
        this.selfReportLevel = selfReportLevel;
        this.eegData = eegData;
    }

    public int getSessionId(){
        return sessionId;
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

    public float getSelfReportedAttention() {
        return selfReportLevel.ordinal();
    }

    public double getEegAttention() {
        return eegData;
    }

    public String getLocation() {
        return this.location;
    }

    public String getActivityName(){
        return activityName;
    }
}
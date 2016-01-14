package edu.engagement.application.utils;

import com.google.android.gms.maps.model.LatLng;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 10/18/15.
 */
public class Session {
    private final int id;
    private final Bitmap sessionImage;
    private final SessionLocation location;
    private final String activityName;
    private LatLng coordinates;
    private final List<EEGDataPoint> data = new ArrayList<>();
    private final List<Annotation> annotations = new ArrayList<>();
    private Annotation finalAnnotation;

    public Session(int id, Bitmap sessionImage, String activityName, SessionLocation location) {
        this.activityName = activityName;
        this.sessionImage = sessionImage;
        this.id = id;
        this.location = location;
    }

    public String getActivityName(){
        return activityName;
    }
    public int getId() {
        return id;
    }

    public Bitmap getImage() {
        return sessionImage;
    }

    public SessionLocation getLocation() {
        return location;
    }

    public List<EEGDataPoint> getEEGData() {
        return new ArrayList<>(data);
    }

    public List<Annotation> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    public float getEEGAverage() {
        float sum = 0;
        for (EEGDataPoint point : data) {
            sum += point.attention;
        }
        return sum / data.size();
    }

    public double getOverallIFeltScore() {
        return finalAnnotation.getAttentionLevel();
    }

    public boolean addDataPoint(EEGDataPoint dataPoint) {
        return data.add(dataPoint);
    }

    public boolean addDataPoint(long timeStamp, float attention) {
        return addDataPoint(new EEGDataPoint(timeStamp, attention));
    }

    public boolean addDataPoints(Collection<? extends EEGDataPoint> dataPoints) {
        return data.addAll(dataPoints);
    }

    public boolean addAnnotation(Annotation annotation) {
        return annotations.add(annotation);
    }

    public boolean addAnnotation(String annotation, double attentionLevel, long timeStamp) {
        return addAnnotation(new Annotation(annotation, attentionLevel, timeStamp));
    }

    public boolean addAnnotations(Collection<? extends Annotation> annotations) {
        return this.annotations.addAll(annotations);
    }

    public long getStartTime() {
        return data.get(0).timeStamp;
    }

    public long getStopTime() {
        return data.get(data.size() - 1).timeStamp;
    }

    public void addGPSData(LatLng newCoord){
        coordinates = newCoord;
    }

    public LatLng getGPSData(){
        return coordinates;
    }

    public void setFinalAnnotation(Annotation finalAnnotation) {
        this.finalAnnotation = finalAnnotation;
    }
}

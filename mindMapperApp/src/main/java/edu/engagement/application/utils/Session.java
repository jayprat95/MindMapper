package edu.engagement.application.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.engagement.application.AttentionLevel;

/**
 * Created by alex on 10/18/15.
 */
public class Session {
    private final int id;
    private final SessionLocation location;
    private final List<EEGDataPoint> data = new ArrayList<>();
    private final List<Annotation> annotations = new ArrayList<>();

    public Session(int id, SessionLocation location) {
        this.id = id;
        this.location = location;
    }

    public int getId() {
        return id;
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

    public AttentionLevel getSelfReportAverage() {
        int sum = 0;
        for (Annotation annotation : annotations) {
            sum += annotation.getAttentionLevel().ordinal();
        }
        int avg = sum / annotations.size();

        return AttentionLevel.fromInt(avg);
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

    public boolean addAnnotation(String annotation, AttentionLevel attentionLevel, long timeStamp) {
        return addAnnotation(new Annotation(annotation, attentionLevel, timeStamp));
    }

    public boolean addAnnotations(Collection<? extends Annotation> annotations) {
        return this.annotations.addAll(annotations);
    }
}

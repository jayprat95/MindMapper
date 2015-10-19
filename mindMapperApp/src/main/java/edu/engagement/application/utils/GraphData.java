package edu.engagement.application.utils;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.AttentionLevel;

/**
 * Created by alex on 10/17/15.
 */
public class GraphData {
    private List<Annotation> annotations = new ArrayList<>();
    private List<EEGDataPoint> dataPoints = new ArrayList<>();

    public boolean addAnnotation(Annotation annotation) {
        return annotations.add(annotation);
    }

    public boolean addAnnotation(String annotation, AttentionLevel attentionLevel, long timeStamp) {
        return addAnnotation(new Annotation(annotation, attentionLevel, timeStamp));
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<EEGDataPoint> getDataPoints() {
        return dataPoints;
    }
}

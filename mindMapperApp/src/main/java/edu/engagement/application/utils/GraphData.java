package edu.engagement.application.utils;

import java.util.ArrayList;
import java.util.List;

import edu.engagement.application.AttentionLevel;

/**
 * Created by alex on 10/17/15.
 */
public class GraphData {
    private List<GraphAnnotation> annotations = new ArrayList<>();
    private List<GraphDataPoint> dataPoints = new ArrayList<>();

    public boolean addAnnotation(GraphAnnotation annotation) {
        return annotations.add(annotation);
    }

    public boolean addAnnotation(String annotation, AttentionLevel attentionLevel, long timeStamp) {
        return addAnnotation(new GraphAnnotation(annotation, attentionLevel, timeStamp));
    }

    public List<GraphAnnotation> getAnnotations() {
        return annotations;
    }

    public List<GraphDataPoint> getDataPoints() {
        return dataPoints;
    }
}

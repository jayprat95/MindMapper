package edu.engagement.application.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.engagement.application.AttentionLevel;

public class Annotation {

    private String annotation;
    private AttentionLevel attentionLevel;
    private long timeStamp;

    public Annotation(String annotation, AttentionLevel attentionLevel, long timeStamp){
        this.annotation = annotation;
        this.attentionLevel = attentionLevel;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTimeFormatted(String format) {
        return new SimpleDateFormat(format).format(new Date(timeStamp));
    }

    public AttentionLevel getAttentionLevel() {
        return this.attentionLevel;
    }

    public String getAnnotation() {
        return annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Annotation)) {
            return false;
        }

        Annotation that = (Annotation)o;

        return that.getTimeStamp() == this.getTimeStamp();
    }
}

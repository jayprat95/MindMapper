package edu.engagement.application;

/**
 * Created by IvenRee on 10/12/15.
 */
public class GraphAnnotation  {

    String annotationNumber = "";
    String time = "";
    String annotation = "";

    public GraphAnnotation(String annotationNumber, String time, String annotation){
        this.annotationNumber = annotationNumber;
        this.time = time;
        this.annotation = annotation;
    }

    public String getAnnotationNumber() {
        return annotationNumber;
    }

    public void setAnnotationNumber(String annotationNumber) {
        this.annotationNumber = annotationNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}

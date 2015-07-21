package edu.engagement.application;

/**
 * Created by alex on 7/14/15.
 */
public enum TrophyType {

    TOP_VALUE_DAILY("This was your most attentive event today!"),
    NONE("");

    private String message;

    private TrophyType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

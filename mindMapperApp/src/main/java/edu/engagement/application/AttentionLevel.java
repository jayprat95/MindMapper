package edu.engagement.application;

import android.graphics.Color;

/**
 * Created by Alex Marshall on 6/27/2015.
 */
public enum AttentionLevel {
    LOW(Color.WHITE),
    MEDIUM_LOW(Color.parseColor("#6666ff")),         // Blue
    MEDIUM(Color.YELLOW),
    MEDIUM_HIGH(Color.parseColor("#FFA500")),      // Orange
    HIGH(Color.RED);

    // The color representing each attention level
    private int color;

    private AttentionLevel(int color) {
        this.color = color;
    }

    /**
     * Get the color that represents an attention level
     * @return the color representation of the attention
     */
    public int getColor() {
        return color;
    }

    /**
     * Return an attention level based on the int passed in.
     * The integer should be between 0 and 4.
     * @param i the integer level of attentiveness out of 4
     * @return the attention level
     */
    public static AttentionLevel fromInt(int i) {
        switch (i) {
            case 0:
                return LOW;
            case 1:
                return MEDIUM_LOW;
            case 2:
                return MEDIUM;
            case 3:
                return MEDIUM_HIGH;
            case 4:
                return HIGH;
            default:
                throw new IllegalArgumentException("Attention Level Integer must be [0-4]. " +
                        "Actual argument value: " + i);
        }
    }
}

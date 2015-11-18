package edu.engagement.application;

import android.graphics.Color;

/**
 * Created by Alex Marshall on 6/27/2015.
 */
public enum AttentionLevel {
    LOW1(Color.parseColor("#BB446E")),
    LOW2(Color.parseColor("#BB4483")),
    LOW3(Color.parseColor("#BB4497")),
    LOW4(Color.parseColor("#BC44AB")),
    LOW5(Color.parseColor("#B944BC")),

    MEDIUM_LOW1(Color.parseColor("#A545BC")),
    MEDIUM_LOW2(Color.parseColor("#9245BC")),
    MEDIUM_LOW3(Color.parseColor("#7E45BD")),
    MEDIUM_LOW4(Color.parseColor("#6B45BD")),
    MEDIUM_LOW5(Color.parseColor("#5746BD")),

    MEDIUM1(Color.parseColor("#4649BE")),
    MEDIUM2(Color.parseColor("#465DBE")),
    MEDIUM3(Color.parseColor("#4671BF")),
    MEDIUM4(Color.parseColor("#4785BF")),
    MEDIUM5(Color.parseColor("#4799BF")),

    MEDIUM_HIGH1(Color.parseColor("#47AEC0")),
    MEDIUM_HIGH2(Color.parseColor("#47C0BE")),
    MEDIUM_HIGH3(Color.parseColor("#48C0AA")),
    MEDIUM_HIGH4(Color.parseColor("#48C196")),
    MEDIUM_HIGH5(Color.parseColor("#48C183")),


    HIGH1(Color.parseColor("#48C16F")),
    HIGH2(Color.parseColor("#49C25B")),
    HIGH3(Color.parseColor("#4BC249")),
    HIGH4(Color.parseColor("#5FC249")),
    HIGH5(Color.parseColor("#74C34A"));

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
     * The integer should be between 1 and 25.
     * @param i the integer level of attentiveness out of 4
     * @return the attention level
     */
    public static AttentionLevel fromInt(int i) {
        switch (i) {
            case 1:
                return LOW1;
            case 2:
                return LOW2;
            case 3:
                return LOW3;
            case 4:
                return LOW4;
            case 5:
                return LOW5;
            case 6:
                return MEDIUM_LOW1;
            case 7:
                return MEDIUM_LOW2;
            case 8:
                return MEDIUM_LOW3;
            case 9:
                return MEDIUM_LOW4;
            case 10:
                return MEDIUM_LOW5;
            case 11:
                return MEDIUM1;
            case 12:
                return MEDIUM2;
            case 13:
                return MEDIUM3;
            case 14:
                return MEDIUM4;
            case 15:
                return MEDIUM5;
            case 16:
                return MEDIUM_HIGH1;
            case 17:
                return MEDIUM_HIGH2;
            case 18:
                return MEDIUM_HIGH3;
            case 19:
                return MEDIUM_HIGH4;
            case 20:
                return MEDIUM_HIGH5;
            case 21:
                return HIGH1;
            case 22:
                return HIGH2;
            case 23:
                return HIGH3;
            case 24:
                return HIGH4;
            case 25:
                return HIGH5;
            default:
                throw new IllegalArgumentException("Attention Level Integer must be [1-25]. " +
                        "Actual argument value: " + i);
        }
    }
}

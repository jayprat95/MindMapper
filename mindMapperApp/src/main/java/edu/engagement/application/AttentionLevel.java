package edu.engagement.application;

import android.graphics.Color;

/**
 * Created by Alex Marshall on 6/27/2015.
 */
public enum AttentionLevel {
    //solid color
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
    HIGH5(Color.parseColor("#74C34A")),

    //transparent color
    TRANSLOW1(Color.parseColor("#90BB446E")),
    TRANSLOW2(Color.parseColor("#90BB4483")),
    TRANSLOW3(Color.parseColor("#90BB4497")),
    TRANSLOW4(Color.parseColor("#90BC44AB")),
    TRANSLOW5(Color.parseColor("#90B944BC")),

    TRANSMEDIUM_LOW1(Color.parseColor("#90A545BC")),
    TRANSMEDIUM_LOW2(Color.parseColor("#909245BC")),
    TRANSMEDIUM_LOW3(Color.parseColor("#907E45BD")),
    TRANSMEDIUM_LOW4(Color.parseColor("#906B45BD")),
    TRANSMEDIUM_LOW5(Color.parseColor("#905746BD")),

    TRANSMEDIUM1(Color.parseColor("#904649BE")),
    TRANSMEDIUM2(Color.parseColor("#90465DBE")),
    TRANSMEDIUM3(Color.parseColor("#904671BF")),
    TRANSMEDIUM4(Color.parseColor("#904785BF")),
    TRANSMEDIUM5(Color.parseColor("#904799BF")),

    TRANSMEDIUM_HIGH1(Color.parseColor("#9047AEC0")),
    TRANSMEDIUM_HIGH2(Color.parseColor("#9047C0BE")),
    TRANSMEDIUM_HIGH3(Color.parseColor("#9048C0AA")),
    TRANSMEDIUM_HIGH4(Color.parseColor("#9048C196")),
    TRANSMEDIUM_HIGH5(Color.parseColor("#9048C183")),

    TRANSHIGH1(Color.parseColor("#9048C16F")),
    TRANSHIGH2(Color.parseColor("#9049C25B")),
    TRANSHIGH3(Color.parseColor("#904BC249")),
    TRANSHIGH4(Color.parseColor("#905FC249")),
    TRANSHIGH5(Color.parseColor("#9074C34A"));

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
     * The integer should be between 0 and 24 inclusive
     * @param i the integer level of attentiveness out of 24
     * @return the attention level
     */
    public static AttentionLevel fromInt(int i) {
        switch (i) {
            case 0:
                return LOW1;
            case 1:
                return LOW2;
            case 2:
                return LOW3;
            case 3:
                return LOW4;
            case 4:
                return LOW5;
            case 5:
                return MEDIUM_LOW1;
            case 6:
                return MEDIUM_LOW2;
            case 7:
                return MEDIUM_LOW3;
            case 8:
                return MEDIUM_LOW4;
            case 9:
                return MEDIUM_LOW5;
            case 10:
                return MEDIUM1;
            case 11:
                return MEDIUM2;
            case 12:
                return MEDIUM3;
            case 13:
                return MEDIUM4;
            case 14:
                return MEDIUM5;
            case 15:
                return MEDIUM_HIGH1;
            case 16:
                return MEDIUM_HIGH2;
            case 17:
                return MEDIUM_HIGH3;
            case 18:
                return MEDIUM_HIGH4;
            case 19:
                return MEDIUM_HIGH5;
            case 20:
                return HIGH1;
            case 21:
                return HIGH2;
            case 22:
                return HIGH3;
            case 23:
                return HIGH4;
            case 24:
                return HIGH5;
            default:
                throw new IllegalArgumentException("Attention Level Integer must be [1-25]. " +
                        "Actual argument value: " + i);
        }
    }

    public static AttentionLevel fromIntTransparent(int i) {
        switch (i+1) {
            case 1:
                return TRANSLOW1;
            case 2:
                return TRANSLOW2;
            case 3:
                return TRANSLOW3;
            case 4:
                return TRANSLOW4;
            case 5:
                return TRANSLOW5;
            case 6:
                return TRANSMEDIUM_LOW1;
            case 7:
                return TRANSMEDIUM_LOW2;
            case 8:
                return TRANSMEDIUM_LOW3;
            case 9:
                return TRANSMEDIUM_LOW4;
            case 10:
                return TRANSMEDIUM_LOW5;
            case 11:
                return TRANSMEDIUM1;
            case 12:
                return TRANSMEDIUM2;
            case 13:
                return TRANSMEDIUM3;
            case 14:
                return TRANSMEDIUM4;
            case 15:
                return TRANSMEDIUM5;
            case 16:
                return TRANSMEDIUM_HIGH1;
            case 17:
                return TRANSMEDIUM_HIGH2;
            case 18:
                return TRANSMEDIUM_HIGH3;
            case 19:
                return TRANSMEDIUM_HIGH4;
            case 20:
                return TRANSMEDIUM_HIGH5;
            case 21:
                return TRANSHIGH1;
            case 22:
                return TRANSHIGH2;
            case 23:
                return TRANSHIGH3;
            case 24:
                return TRANSHIGH4;
            case 25:
                return TRANSHIGH5;
            default:
                throw new IllegalArgumentException("Attention Level Integer must be [1-25]. " +
                        "Actual argument value: " + i);
        }
    }
}

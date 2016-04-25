package edu.engagement.application.utils;

import android.graphics.Color;

/**
 * Created by alex on 11/26/15.
 */
public class ColorUtils {

    /**
     * Get the attention color associated with a given attention value
     *
     * @param value attention value between 0 and 100
     * @return the color associated with the attention value passed in
     */
    public static int getAttentionColor(float value) {
        int colorIndex = Math.round(value/4);

        // Make sure our number is between 1 and 25
        if (colorIndex < 1) colorIndex = 1;
        else if (colorIndex > 25) colorIndex = 25;

        switch (colorIndex) {
            case 1:
                return Color.parseColor("#BB446E");
            case 2:
                return Color.parseColor("#BB4483");
            case 3:
                return Color.parseColor("#BB4497");
            case 4:
                return Color.parseColor("#BC44AB");
            case 5:
                return Color.parseColor("#B944BC");
            case 6:
                return Color.parseColor("#A545BC");
            case 7:
                return Color.parseColor("#9245BC");
            case 8:
                return Color.parseColor("#7E45BD");
            case 9:
                return Color.parseColor("#6B45BD");
            case 10:
                return Color.parseColor("#5746BD");
            case 11:
                return Color.parseColor("#4649BE");
            case 12:
                return Color.parseColor("#465DBE");
            case 13:
                return Color.parseColor("#4671BF");
            case 14:
                return Color.parseColor("#4785BF");
            case 15:
                return Color.parseColor("#4799BF");
            case 16:
                return Color.parseColor("#47AEC0");
            case 17:
                return Color.parseColor("#47C0BE");
            case 18:
                return Color.parseColor("#48C0AA");
            case 19:
                return Color.parseColor("#48C196");
            case 20:
                return Color.parseColor("#48C183");
            case 21:
                return Color.parseColor("#48C16F");
            case 22:
                return Color.parseColor("#49C25B");
            case 23:
                return Color.parseColor("#4BC249");
            case 24:
                return Color.parseColor("#5FC249");
            case 25:
                return Color.parseColor("#74C34A");
            default:
                return Color.parseColor("#ffffff");
        }
    }

    public static int getTransAttentionColor(float value) {
        int colorIndex = Math.round(value/4);

        // Make sure our number is between 1 and 25
        if (colorIndex < 1) colorIndex = 1;
        else if (colorIndex > 25) colorIndex = 25;

        switch (colorIndex) {
            case 1:
                return Color.parseColor("#90BB446E");
            case 2:
                return Color.parseColor("#90BB4483");
            case 3:
                return Color.parseColor("#90BB4497");
            case 4:
                return Color.parseColor("#90BC44AB");
            case 5:
                return Color.parseColor("#90B944BC");
            case 6:
                return Color.parseColor("#90A545BC");
            case 7:
                return Color.parseColor("#909245BC");
            case 8:
                return Color.parseColor("#907E45BD");
            case 9:
                return Color.parseColor("#906B45BD");
            case 10:
                return Color.parseColor("#905746BD");
            case 11:
                return Color.parseColor("#904649BE");
            case 12:
                return Color.parseColor("#90465DBE");
            case 13:
                return Color.parseColor("#904671BF");
            case 14:
                return Color.parseColor("#904785BF");
            case 15:
                return Color.parseColor("#904799BF");
            case 16:
                return Color.parseColor("#9047AEC0");
            case 17:
                return Color.parseColor("#9047C0BE");
            case 18:
                return Color.parseColor("#9048C0AA");
            case 19:
                return Color.parseColor("#9048C196");
            case 20:
                return Color.parseColor("#9048C183");
            case 21:
                return Color.parseColor("#9048C16F");
            case 22:
                return Color.parseColor("#9049C25B");
            case 23:
                return Color.parseColor("#904BC249");
            case 24:
                return Color.parseColor("#905FC249");
            case 25:
                return Color.parseColor("#9074C34A");
            default:
                return Color.parseColor("#90ffffff");
        }
    }
}

package edu.engagement.application.utils;

import edu.engagement.application.AttentionLevel;

/**
 * Created by alex on 10/10/15.
 */
public final class AttentionColor {

    public static int getAttentionColor(double attention) {

        int att;
        if (attention < 20) {
            att = 0;
        } else if (attention < 40) {
            att = 1;
        } else if (attention < 60) {
            att = 2;
        } else if (attention < 80) {
            att = 3;
        } else {
            att = 4;
        }

        return AttentionLevel.fromInt(att).getColor();
    }
}

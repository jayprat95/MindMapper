package edu.engagement.application.utils;

import edu.engagement.application.AttentionLevel;

/**
 * Created by alex on 10/10/15.
 */
public final class AttentionColor {

    public static int getAttentionColor(double attention) {

        int att;
        if (attention < 5) {
            att = 0;
        } else if (attention < 9) {
            att = 1;
        } else if (attention < 13) {
            att = 2;
        } else if (attention < 17) {
            att = 3;
        }else if (attention < 21) {
            att = 4;
        }else if (attention < 25) {
            att = 5;
        }else if (attention < 29) {
            att = 6;
        }else if (attention < 33) {
            att = 7;
        }else if (attention < 37) {
            att = 8;
        }else if (attention < 41) {
            att = 9;
        }else if (attention < 45) {
            att = 10;
        }else if (attention < 49) {
            att = 11;
        }else if (attention < 53) {
            att = 12;
        }else if (attention < 57) {
            att = 13;
        }else if (attention < 61) {
            att = 14;
        }else if (attention < 65) {
            att = 15;
        }else if (attention < 69) {
            att = 16;
        }else if (attention < 73) {
            att = 17;
        }else if (attention < 77) {
            att = 18;
        }else if (attention < 81) {
            att = 19;
        }else if (attention < 85) {
            att = 20;
        }else if (attention < 89) {
            att = 21;
        }else if (attention < 93) {
            att = 22;
        } else if (attention < 97){
            att = 23;
        } else {
            att = 24;
        }

        return AttentionLevel.fromInt(att).getColor();
    }

    public static int getAttentionTransparentColor(double attention) {

        int att;
        if (attention < 5) {
            att = 0;
        } else if (attention < 9) {
            att = 1;
        } else if (attention < 13) {
            att = 2;
        } else if (attention < 17) {
            att = 3;
        }else if (attention < 21) {
            att = 4;
        }else if (attention < 25) {
            att = 5;
        }else if (attention < 29) {
            att = 6;
        }else if (attention < 33) {
            att = 7;
        }else if (attention < 37) {
            att = 8;
        }else if (attention < 41) {
            att = 9;
        }else if (attention < 45) {
            att = 10;
        }else if (attention < 49) {
            att = 11;
        }else if (attention < 53) {
            att = 12;
        }else if (attention < 57) {
            att = 13;
        }else if (attention < 61) {
            att = 14;
        }else if (attention < 65) {
            att = 15;
        }else if (attention < 69) {
            att = 16;
        }else if (attention < 73) {
            att = 17;
        }else if (attention < 77) {
            att = 18;
        }else if (attention < 81) {
            att = 19;
        }else if (attention < 85) {
            att = 20;
        }else if (attention < 89) {
            att = 21;
        }else if (attention < 93) {
            att = 22;
        } else if (attention < 97){
            att = 23;
        } else {
            att = 24;
        }

        return AttentionLevel.fromIntTransparent(att).getColor();
    }
}

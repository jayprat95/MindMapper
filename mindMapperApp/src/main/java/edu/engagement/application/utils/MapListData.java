package edu.engagement.application.utils;

import edu.engagement.application.AttentionLevel;


public class MapListData {
    private String location;
    private AttentionLevel focusLevel;
    private AttentionLevel feltLevel;
    private String activityText;

    public MapListData(String location, AttentionLevel focusLevel, AttentionLevel feltLevel, String activityText ){
        this.location = location;
        this.focusLevel = focusLevel;
        this.feltLevel = feltLevel;
        this.activityText = activityText;
    }

    public String getLocation() {
        return location;
    }

    public AttentionLevel getFocusLevel() {
        return focusLevel;
    }

    public AttentionLevel getFeltLevel() {
        return feltLevel;
    }

    public String getActivityText() {
        return activityText;
    }
}

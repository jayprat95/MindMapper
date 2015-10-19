package edu.engagement.application.utils;

/**
 * Created by alex on 10/18/15.
 */
public class SessionLocation {
    private final String name;
    private final double latitude, longitude;

    public SessionLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof SessionLocation)) {
            return false;
        }
        SessionLocation that = (SessionLocation)other;
        return this.name == that.name;
    }
}

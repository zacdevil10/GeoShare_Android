package uk.co.appsbystudio.geoshare.utils.firebase;

public class DatabaseLocations {

    private final Double longitude;
    private final Double lat;
    private final Long timestamp;

    public DatabaseLocations(Double longitude, Double lat, Long timestamp) {
        this.longitude = longitude;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLat() {
        return lat;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}

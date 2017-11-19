package uk.co.appsbystudio.geoshare.utils.firebase;

public class DatabaseLocations {

    private Double longitude;
    private Double lat;
    private Long timestamp;

    public DatabaseLocations() {}

    public DatabaseLocations(Double longitude, Double lat, Long timestamp) {
        this.longitude = longitude;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(Long timestamp) {
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

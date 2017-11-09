package uk.co.appsbystudio.geoshare.utils.firebase;

public class DatabaseLocations {

    private String friendId;
    private Double longitude;
    private Double lat;
    private Long timestamp;

    public DatabaseLocations() {}

    public DatabaseLocations(Double longitude, Double lat, Long timestamp) {
        this.longitude = longitude;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public DatabaseLocations(String friendId, Double longitude, Double lat, Long timestamp) {
        this.friendId = friendId;
        this.longitude = longitude;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFriendId() {
        return friendId;
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

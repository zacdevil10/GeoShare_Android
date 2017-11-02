package uk.co.appsbystudio.geoshare.utils.firebase;

public class DatabaseLocations {

    private String friendId;
    private Double longitude;
    private Double lat;
    private Long timeStamp;

    public DatabaseLocations() {}

    public DatabaseLocations(Double longitude, Double lat, Long timeStamp) {
        this.longitude = longitude;
        this.lat = lat;
        this.timeStamp = timeStamp;
    }

    public DatabaseLocations(String friendId, Double longitude, Double lat, Long timeStamp) {
        this.friendId = friendId;
        this.longitude = longitude;
        this.lat = lat;
        this.timeStamp = timeStamp;
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

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
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

    public Long getTimeStamp() {
        return timeStamp;
    }
}

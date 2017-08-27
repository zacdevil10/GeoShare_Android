package uk.co.appsbystudio.geoshare.utils;

public class DatabaseLocations {

    private String friendId;
    private Double longitude;
    private Double lat;
    private long timeStamp;

    public DatabaseLocations() {}

    public DatabaseLocations(Double longitude, Double lat, long timeStamp) {
        this.longitude = longitude;
        this.lat = lat;
        this.timeStamp = timeStamp;
    }

    public DatabaseLocations(String friendId, Double longitude, Double lat, long timeStamp) {
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

    public void setTimeStamp(long timeStamp) {
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

    public long getTimeStamp() {
        return timeStamp;
    }
}

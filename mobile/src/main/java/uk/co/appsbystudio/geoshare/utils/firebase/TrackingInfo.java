package uk.co.appsbystudio.geoshare.utils.firebase;

public class TrackingInfo {

    private boolean tracking;
    private long timestamp;

    public TrackingInfo() {}

    public TrackingInfo(boolean tracking, long timestamp) {
        this.tracking = tracking;
        this.timestamp = timestamp;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTracking() {
        return tracking;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

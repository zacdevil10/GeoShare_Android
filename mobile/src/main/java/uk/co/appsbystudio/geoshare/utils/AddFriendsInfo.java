package uk.co.appsbystudio.geoshare.utils;

public class AddFriendsInfo {

    private String uid;
    private boolean isOutgoing;

    public AddFriendsInfo() {}

    public AddFriendsInfo(boolean isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public AddFriendsInfo(String uid, boolean isOutgoing) {
        this.uid = uid;
        this.isOutgoing = isOutgoing;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setOutgoing(boolean outgoing) {
        isOutgoing = outgoing;
    }

    public String getUid() {
        return uid;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }
}

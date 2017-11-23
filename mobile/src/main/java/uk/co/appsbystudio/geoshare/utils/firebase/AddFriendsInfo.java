package uk.co.appsbystudio.geoshare.utils.firebase;

public class AddFriendsInfo {

    private boolean isOutgoing;

    public AddFriendsInfo() {}

    public AddFriendsInfo(boolean isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public void setOutgoing(boolean outgoing) {
        isOutgoing = outgoing;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }
}

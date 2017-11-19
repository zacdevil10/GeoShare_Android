package uk.co.appsbystudio.geoshare.utils.firebase;

public class AddFriendsInfo {

    private boolean isOutgoing;

    public AddFriendsInfo() {}

    public AddFriendsInfo(boolean isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }
}

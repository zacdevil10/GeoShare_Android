package uk.co.appsbystudio.geoshare.utils.firebase;

public class AddFriendsInfo {

    private final boolean isOutgoing;

    public AddFriendsInfo(boolean isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }
}

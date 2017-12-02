package uk.co.appsbystudio.geoshare.utils.firebase.listeners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.File;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsNavAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsRequestAdapter;

public class UpdatedProfilePicturesListener implements ChildEventListener {

    private FriendsNavAdapter navAdapter;
    private FriendsPendingAdapter pendingAdapter;
    private FriendsRequestAdapter requestAdapter;

    public UpdatedProfilePicturesListener(FriendsNavAdapter adapter) {
        this.navAdapter = adapter;
    }

    public UpdatedProfilePicturesListener(FriendsPendingAdapter adapter) {
        this.pendingAdapter = adapter;
    }

    public UpdatedProfilePicturesListener(FriendsRequestAdapter adapter) {
        this.requestAdapter = adapter;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        onChildChanged(dataSnapshot, s);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        File imageFile = new File(MainActivity.cacheDir + "/" + dataSnapshot.getKey() + ".png");
        if (imageFile.exists() && dataSnapshot.getValue(Long.class) != null && dataSnapshot.getValue(Long.class) > imageFile.lastModified()) {
            imageFile.delete();

            if (navAdapter != null) navAdapter.notifyDataSetChanged();
            if (pendingAdapter != null) pendingAdapter.notifyDataSetChanged();
            if (requestAdapter != null) requestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        onChildChanged(dataSnapshot, null);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

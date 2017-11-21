package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsRequestAdapter;
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.UpdatedProfilePicturesListener;
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification;

public class FriendsPendingFragment extends Fragment implements FriendsRequestAdapter.Callback, FriendsPendingAdapter.Callback {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference databasePendingReference;

    private FriendsRequestAdapter friendsRequestAdapter;
    private FriendsPendingAdapter friendsPendingAdapter;

    private final ArrayList<String> userId = new ArrayList<>();

    private final ArrayList<String> userIdRequests = new ArrayList<>();

    private View view;

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        NewFriendNotification.cancel(getContext());

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        if (auth.getCurrentUser() != null) {
            databasePendingReference = database.getReference("pending/" + auth.getCurrentUser().getUid());
            databasePendingReference.keepSynced(true);
        }

        RecyclerView friendsIncomingList = view.findViewById(R.id.friend_incoming_list);
        setupRecycler(friendsIncomingList);

        RecyclerView friendsOutgoingList = view.findViewById(R.id.friend_outgoing_list);
        setupRecycler(friendsOutgoingList);

        if (auth.getCurrentUser() != null) getRequests();

        friendsRequestAdapter = new FriendsRequestAdapter(userIdRequests, databaseReference, FriendsPendingFragment.this);
        friendsPendingAdapter = new FriendsPendingAdapter(userId, databaseReference, FriendsPendingFragment.this);

        databaseReference.child("picture").addChildEventListener(new UpdatedProfilePicturesListener(friendsRequestAdapter));
        databaseReference.child("picture").addChildEventListener(new UpdatedProfilePicturesListener(friendsPendingAdapter));

        friendsIncomingList.setAdapter(friendsRequestAdapter);
        friendsOutgoingList.setAdapter(friendsPendingAdapter);

        return view;
    }

    private void setupRecycler(RecyclerView friendsIncomingList) {
        friendsIncomingList.setHasFixedSize(false);
        friendsIncomingList.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsIncomingList.setLayoutManager(layoutManagerRequests);
    }

    private void getRequests() {
        databasePendingReference.addChildEventListener(newPendingItemListener);
    }

    private void removeRequests(DataSnapshot dataSnapshot, AddFriendsInfo addFriendsInfo) {
        if (!addFriendsInfo.isOutgoing()) {
            userIdRequests.remove(dataSnapshot.getKey());
            friendsRequestAdapter.notifyDataSetChanged();
        } else {
            userId.remove(dataSnapshot.getKey());
            friendsPendingAdapter.notifyDataSetChanged();
        }
    }

    private void addRequests(DataSnapshot dataSnapshot, AddFriendsInfo addFriendsInfo) {
        if (!addFriendsInfo.isOutgoing()) {
            userIdRequests.add(dataSnapshot.getKey());
            friendsRequestAdapter.notifyDataSetChanged();
        } else {
            userId.add(dataSnapshot.getKey());
            friendsPendingAdapter.notifyDataSetChanged();
        }
    }

    private void resetIsEmptyMessage() {
        boolean hasUserIds = userId.size() == 0;
        boolean hasUserIdsRequests = userIdRequests.size() == 0;

        view.findViewById(R.id.friends_no_requests).setVisibility(hasUserIds ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.friends_no_requests).setVisibility(hasUserIdsRequests ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onAcceptReject(Boolean accept, String uid) {
        if (auth.getCurrentUser() != null) {
            if (accept) {
                databaseReference.child("friends").child(auth.getCurrentUser().getUid()).child(uid).setValue(true);
                databaseReference.child("friends").child(uid).child(auth.getCurrentUser().getUid()).setValue(true);
            } else {
                databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
                databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();

                new File(MainActivity.cacheDir + "/" + uid + ".png").delete();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onReject(String uid) {
        if (auth.getCurrentUser() != null) {
            databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
            databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();

            new File(MainActivity.cacheDir + "/" + uid + ".png").delete();
        }
    }

    private final ChildEventListener newPendingItemListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (!MainActivity.pendingId.containsKey(dataSnapshot.getKey())) MainActivity.pendingId.put(dataSnapshot.getKey(), true);
            AddFriendsInfo addFriendsInfo = dataSnapshot.getValue(AddFriendsInfo.class);
            if (addFriendsInfo != null) {
                addRequests(dataSnapshot, addFriendsInfo);
            }

            resetIsEmptyMessage();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            if (MainActivity.pendingId.containsKey(dataSnapshot.getKey())) MainActivity.pendingId.remove(dataSnapshot.getKey());
            AddFriendsInfo addFriendsInfo = dataSnapshot.getValue(AddFriendsInfo.class);
            if (addFriendsInfo != null) {
                removeRequests(dataSnapshot, addFriendsInfo);
            }

            resetIsEmptyMessage();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
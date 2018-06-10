package uk.co.appsbystudio.geoshare.friends.manager.pages.pending;

import android.content.Context;
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

import uk.co.appsbystudio.geoshare.base.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.adapters.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.adapters.FriendsRequestAdapter;
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo;
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification;

public class FriendsPendingFragment extends Fragment implements FriendsRequestAdapter.Callback, FriendsPendingAdapter.Callback {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference databasePendingReference;

    private FriendsRequestAdapter friendsIncomingAdapter;
    private FriendsPendingAdapter friendsOutgoingAdapter;

    private final ArrayList<String> userIdIncoming = new ArrayList<>();
    private final ArrayList<String> userIdOutgoing = new ArrayList<>();

    private View view;

    private File cache;

    public FriendsPendingFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cache = context.getCacheDir();
    }

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

        friendsIncomingAdapter = new FriendsRequestAdapter(getContext(), userIdIncoming, databaseReference, FriendsPendingFragment.this);
        friendsOutgoingAdapter = new FriendsPendingAdapter(getContext(), userIdOutgoing, databaseReference, FriendsPendingFragment.this);

        friendsIncomingList.setAdapter(friendsIncomingAdapter);
        friendsOutgoingList.setAdapter(friendsOutgoingAdapter);

        return view;
    }

    private void setupRecycler(RecyclerView friendsLists) {
        friendsLists.setHasFixedSize(false);
        friendsLists.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsLists.setLayoutManager(layoutManagerRequests);
    }

    private void getRequests() {
        databasePendingReference.addChildEventListener(newPendingItemListener);
    }

    private void removeRequests(DataSnapshot dataSnapshot, AddFriendsInfo addFriendsInfo) {
        if (!addFriendsInfo.isOutgoing()) {
            userIdIncoming.remove(dataSnapshot.getKey());
            friendsIncomingAdapter.notifyDataSetChanged();
        } else {
            userIdOutgoing.remove(dataSnapshot.getKey());
            friendsOutgoingAdapter.notifyDataSetChanged();
        }
    }

    private void addRequests(DataSnapshot dataSnapshot, AddFriendsInfo addFriendsInfo) {
        if (!addFriendsInfo.isOutgoing()) {
            userIdIncoming.add(dataSnapshot.getKey());
            friendsIncomingAdapter.notifyDataSetChanged();
        } else {
            userIdOutgoing.add(dataSnapshot.getKey());
            friendsOutgoingAdapter.notifyDataSetChanged();
        }
    }

    private void resetIsEmptyMessage() {
        boolean hasUserIdsOutgoing = userIdOutgoing.size() == 0;
        boolean hasUserIdsIncoming = userIdIncoming.size() == 0;

        view.findViewById(R.id.friends_no_pending).setVisibility(hasUserIdsOutgoing ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.friends_no_requests).setVisibility(hasUserIdsIncoming ? View.VISIBLE : View.GONE);
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

                new File(cache + "/" + uid + ".png").delete();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onReject(String uid) {
        if (auth.getCurrentUser() != null) {
            databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
            databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();

            new File(cache + "/" + uid + ".png").delete();
        }
    }

    private final ChildEventListener newPendingItemListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            AddFriendsInfo addFriendsInfo = dataSnapshot.getValue(AddFriendsInfo.class);
            if (!MainActivity.Companion.getPendingId().containsKey(dataSnapshot.getKey()) && addFriendsInfo != null) {
                if (addFriendsInfo.isOutgoing()) {
                    MainActivity.Companion.getPendingId().put(dataSnapshot.getKey(), true);
                } else {
                    MainActivity.Companion.getPendingId().put(dataSnapshot.getKey(), false);
                }
            }

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
            if (MainActivity.Companion.getPendingId().containsKey(dataSnapshot.getKey())) MainActivity.Companion.getPendingId().remove(dataSnapshot.getKey());
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
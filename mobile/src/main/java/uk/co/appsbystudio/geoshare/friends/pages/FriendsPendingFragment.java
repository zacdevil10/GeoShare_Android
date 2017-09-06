package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsRequestAdapter;
import uk.co.appsbystudio.geoshare.utils.AddFriendsInfo;

public class FriendsPendingFragment extends Fragment implements FriendsRequestAdapter.Callback {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databasePendingReference;
    private StorageReference storageReference;

    private FriendsRequestAdapter friendsRequestAdapter;
    private FriendsPendingAdapter friendsPendingAdapter;

    SwipeRefreshLayout swipeRefresh;

    private final ArrayList<String> userId = new ArrayList<>();

    private final ArrayList<String> userIdRequests = new ArrayList<>();

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databasePendingReference = database.getReference("pending/" + auth.getCurrentUser().getUid());
        databasePendingReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        RecyclerView friendsIncomingList = (RecyclerView) view.findViewById(R.id.friend_incoming_list);
        friendsIncomingList.setHasFixedSize(false);
        friendsIncomingList.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsIncomingList.setLayoutManager(layoutManagerRequests);

        RecyclerView friendsOutgoingList = (RecyclerView) view.findViewById(R.id.friend_outgoing_list);
        friendsOutgoingList.setHasFixedSize(false);
        friendsOutgoingList.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerPending = new LinearLayoutManager(getActivity());
        friendsOutgoingList.setLayoutManager(layoutManagerPending);

        getRequests();

        friendsRequestAdapter = new FriendsRequestAdapter(getContext(), userIdRequests, databaseReference, FriendsPendingFragment.this);
        friendsPendingAdapter = new FriendsPendingAdapter(getContext(), userId, databaseReference);

        friendsIncomingList.setAdapter(friendsRequestAdapter);
        friendsOutgoingList.setAdapter(friendsPendingAdapter);

        TextView noRequests = (TextView) view.findViewById(R.id.friends_no_requests);
        TextView noPending = (TextView) view.findViewById(R.id.friends_no_pending);

        return view;
    }

    private void getRequests() {
        databasePendingReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AddFriendsInfo addFriendsInfo = dataSnapshot.getValue(AddFriendsInfo.class);
                if (!addFriendsInfo.isOutgoing()) {
                    userIdRequests.add(dataSnapshot.getKey());
                    friendsRequestAdapter.notifyDataSetChanged();
                } else {
                    userId.add(dataSnapshot.getKey());
                    friendsPendingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AddFriendsInfo addFriendsInfo = dataSnapshot.getValue(AddFriendsInfo.class);
                if (!addFriendsInfo.isOutgoing()) {
                    userIdRequests.remove(dataSnapshot.getKey());
                    friendsRequestAdapter.notifyDataSetChanged();
                } else {
                    userId.remove(dataSnapshot.getKey());
                    friendsPendingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAcceptReject(Boolean accept, String uid) {
        if (accept) {
            databaseReference.child("friends").child(auth.getCurrentUser().getUid()).child(uid).setValue(true);
            databaseReference.child("friends").child(uid).child(auth.getCurrentUser().getUid()).setValue(true);
            databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
            databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();
        } else {
            databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
            databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();
        }
    }
}
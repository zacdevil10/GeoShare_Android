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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsRequestAdapter;

public class FriendsPendingFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    FriendsRequestAdapter friendsRequestAdapter;
    FriendsPendingAdapter friendsPendingAdapter;

    SwipeRefreshLayout swipeRefresh;

    private final ArrayList<String> userId = new ArrayList<>();

    private final ArrayList<String> userIdRequests = new ArrayList<>();

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl("https://modular-decoder-118720.firebaseio.com/");
        storageReference = FirebaseStorage.getInstance().getReference();

        RecyclerView friendsIncomingList = (RecyclerView) view.findViewById(R.id.friend_incoming_list);
        friendsIncomingList.setHasFixedSize(true);
        friendsIncomingList.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsIncomingList.setLayoutManager(layoutManagerRequests);

        RecyclerView friendsOutgoingList = (RecyclerView) view.findViewById(R.id.friend_outgoing_list);
        friendsOutgoingList.setHasFixedSize(true);
        friendsOutgoingList.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManagerPending = new LinearLayoutManager(getActivity());
        friendsOutgoingList.setLayoutManager(layoutManagerPending);

        getIncomingFriends();
        getOutgoingFriends();

        friendsRequestAdapter = new FriendsRequestAdapter(getContext(), userIdRequests);
        friendsPendingAdapter = new FriendsPendingAdapter(getContext(), userId);

        friendsIncomingList.setAdapter(friendsRequestAdapter);
        friendsOutgoingList.setAdapter(friendsPendingAdapter);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        TextView noRequests = (TextView) view.findViewById(R.id.friends_no_requests);
        TextView noPending = (TextView) view.findViewById(R.id.friends_no_pending);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIncomingFriends();
                getOutgoingFriends();
            }
        });

        return view;
    }

    private void getIncomingFriends() {
        Query query = databaseReference.child("pending").child(auth.getCurrentUser().getUid()).orderByChild("outgoing").startAt(false).endAt(false);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsRequestAdapter.notifyItemRangeRemoved(0, userId.size());
                userIdRequests.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userIdRequests.add(ds.getKey());
                    friendsRequestAdapter.notifyDataSetChanged();
                }
                if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getOutgoingFriends() {
        Query query = databaseReference.child("pending").child(auth.getCurrentUser().getUid()).orderByChild("outgoing").startAt(true).endAt(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsPendingAdapter.notifyItemRangeRemoved(0, userId.size());
                userId.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userId.add(ds.getKey());
                    friendsPendingAdapter.notifyDataSetChanged();
                }
                if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
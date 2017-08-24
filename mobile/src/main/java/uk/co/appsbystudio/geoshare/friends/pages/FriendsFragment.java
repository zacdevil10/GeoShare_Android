package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsAdapter;

public class FriendsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    FriendsAdapter friendsAdapter;
    SwipeRefreshLayout swipeRefresh;

    private final ArrayList<String> userId = new ArrayList<>();

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl("https://modular-decoder-118720.firebaseio.com/");
        storageReference = FirebaseStorage.getInstance().getReference();

        RecyclerView friendsList = (RecyclerView) view.findViewById(R.id.friend_list);
        friendsList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendsList.setLayoutManager(layoutManager);

        getFriends();

        friendsAdapter = new FriendsAdapter(getContext(),userId, databaseReference);
        friendsList.setAdapter(friendsAdapter);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriends();
            }
        });

        return view;
    }

    private void getFriends() {
        Query query = databaseReference.child("friends").child(auth.getCurrentUser().getUid()).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsAdapter.notifyItemRangeRemoved(0, userId.size());
                userId.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userId.add(ds.getKey());
                    friendsAdapter.notifyDataSetChanged();
                }
                if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
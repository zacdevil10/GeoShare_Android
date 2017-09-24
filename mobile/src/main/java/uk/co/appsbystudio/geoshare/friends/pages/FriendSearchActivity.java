package uk.co.appsbystudio.geoshare.friends.pages;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;

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
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsSearchAdapter;
import uk.co.appsbystudio.geoshare.utils.RecentSearches;
import uk.co.appsbystudio.geoshare.utils.UserInformation;

public class FriendSearchActivity extends AppCompatActivity implements FriendsSearchAdapter.Callback {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private String uid;

    private FriendsSearchAdapter searchAdapter;

    private SearchView searchView;

    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> userId = new ArrayList<>();
    private final ArrayList<Boolean> isSearch = new ArrayList<>();
    private final ArrayList<Boolean> isRecent = new ArrayList<>();

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl("https://modular-decoder-118720.firebaseio.com/");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        uid = auth.getCurrentUser().getUid();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView searchResults = findViewById(R.id.searchResults);
        searchResults.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchResults.setLayoutManager(layoutManager);

        getRecentSearches();

        searchAdapter = new FriendsSearchAdapter(FriendSearchActivity.this, databaseReference, auth, names, isSearch, isRecent, userId, FriendSearchActivity.this);
        searchResults.setAdapter(searchAdapter);

        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() == 0) {
                    getRecentSearches();
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Query query = databaseReference.child("users").orderByChild("caseFoldedName").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "~");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            searchAdapter.notifyItemRangeRemoved(0, names.size());
                            names.clear();
                            userId.clear();
                            isRecent.clear();
                            isSearch.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                UserInformation userInformation = dataSnapshot.child(ds.getKey()).getValue(UserInformation.class);
                                assert userInformation != null;
                                isSearch.add(false);
                                isRecent.add(false);
                                names.add(ds.child("name").getValue(String.class));
                                userId.add(ds.getKey());

                                searchAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    RecentSearches recentSearches = new RecentSearches(s, String.valueOf(-1 * System.currentTimeMillis()), true);
                    databaseReference.child("recent_friends_search").child(auth.getCurrentUser().getUid()).push().setValue(recentSearches);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0) {
                    getRecentSearches();
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Query query = databaseReference.child("users").orderByChild("caseFoldedName").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "~");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            searchAdapter.notifyItemRangeRemoved(0, names.size());
                            names.clear();
                            userId.clear();
                            isRecent.clear();
                            isSearch.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                names.add(ds.child("name").getValue(String.class));
                                isSearch.add(false);
                                userId.add(ds.getKey());
                                isRecent.add(false);
                                searchAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                return false;
            }
        });
    }

    private void getRecentSearches() {
        Query query = databaseReference.child("recent_friends_search").child(uid).orderByChild("timeStamp").limitToLast(15);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchAdapter.notifyItemRangeRemoved(0, names.size());
                names.clear();
                userId.clear();
                isRecent.clear();
                isSearch.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    names.add(0, ds.child("entry").getValue(String.class));
                    userId.add(0, ds.child("uid").getValue(String.class));
                    isSearch.add(0, ds.child("search").getValue(Boolean.class));
                    isRecent.add(true);
                    searchAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSearchItemClick(String searchEntry) {
        searchView.setQuery(searchEntry, false);
    }
}

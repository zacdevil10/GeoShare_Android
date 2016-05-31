package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;
import uk.co.appsbystudio.geoshare.json.JSONStringRequests;

public class FriendsFragment extends Fragment {

    private ListView friendsList;

    private SwipeRefreshLayout swipeRefresh;

    private String pID;
    private String mUsername;

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        DatabaseHelper db = new DatabaseHelper(getContext());

        friendsList = (ListView) view.findViewById(R.id.friend_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/", pID).execute();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/", pID).execute();
            }
        });

        return view;
    }
}
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

public class FriendsRequestFragment extends Fragment {

    private ListView friendRequestList;

    private SwipeRefreshLayout swipeRefresh;

    private String pID;
    private String mUsername;

    public FriendsRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_request, container, false);

        friendRequestList = (ListView) view.findViewById(R.id.friend_request_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        DatabaseHelper db = new DatabaseHelper(getContext());

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new JSONStringRequests(getActivity(), friendRequestList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/request/", pID).execute();
            }
        });

        new JSONStringRequests(getActivity(), friendRequestList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/request/", pID).execute();

        return view;
    }
}
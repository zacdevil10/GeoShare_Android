package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.JSONStringRequests;

public class FriendsFragment extends Fragment {

    private ListView friendsList;
    private SwipeRefreshLayout swipeRefresh;

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = (ListView) view.findViewById(R.id.friend_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        requestFriends(friendsList, swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestFriends(friendsList, swipeRefresh);
            }
        });

        return view;
    }

    private void requestFriends(ListView friendsList, SwipeRefreshLayout swipeRefresh) {
        new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(getActivity()) + "/friends/", new ReturnData().getpID(getActivity())).execute();
    }
}
package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import uk.co.appsbystudio.geoshare.R;

public class FriendsRequestFragment extends Fragment {

    private ListView friendsRequestList;
    private SwipeRefreshLayout swipeRefresh;

    public FriendsRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_request, container, false);

        friendsRequestList = (ListView) view.findViewById(R.id.friend_request_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        requestFriends(friendsRequestList, swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestFriends(friendsRequestList, swipeRefresh);
            }
        });

        return view;
    }

    private void requestFriends(ListView friendsList, SwipeRefreshLayout swipeRefresh) {
        //new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(getActivity()) + "/friends/request/", new ReturnData().getpID(getActivity())).execute();
    }
}
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

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.JSONStringRequestFriendsList;

public class FriendsPendingFragment extends Fragment {

    private RecyclerView friendsRequestList;
    private RecyclerView friendsPendingList;
    private SwipeRefreshLayout swipeRefresh;
    private TextView noRequests;
    private TextView noPending;

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        friendsRequestList = (RecyclerView) view.findViewById(R.id.friend_request_list);
        friendsRequestList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsRequestList.setLayoutManager(layoutManagerRequests);

        friendsPendingList = (RecyclerView) view.findViewById(R.id.friend_pending_list);
        friendsPendingList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerPending = new LinearLayoutManager(getActivity());
        friendsPendingList.setLayoutManager(layoutManagerPending);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        noRequests = (TextView) view.findViewById(R.id.friends_no_requests);
        noPending = (TextView) view.findViewById(R.id.friends_no_pending);

        requestFriends(friendsRequestList, swipeRefresh, noRequests);
        pendingFriends(friendsPendingList, swipeRefresh, noPending);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestFriends(friendsRequestList, swipeRefresh, noRequests);
                pendingFriends(friendsPendingList, swipeRefresh, noPending);
            }
        });

        return view;
    }

    private void requestFriends(RecyclerView friendsList, SwipeRefreshLayout swipeRefresh, TextView noRequests) {
        new JSONStringRequestFriendsList(getActivity(), friendsList, swipeRefresh, noRequests, "https://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(getActivity()) + "/friends/request/", new ReturnData().getpID(getActivity()), 1).execute();
    }

    private void pendingFriends(RecyclerView friendsList, SwipeRefreshLayout swipeRefresh, TextView noPending) {
        new JSONStringRequestFriendsList(getActivity(), friendsList, swipeRefresh, noPending, "https://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(getActivity()) + "/friends/pending/", new ReturnData().getpID(getActivity()), 2).execute();
    }
}
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

public class FriendsPendingFragment extends Fragment {

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        RecyclerView friendsRequestList = view.findViewById(R.id.friend_request_list);
        friendsRequestList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerRequests = new LinearLayoutManager(getActivity());
        friendsRequestList.setLayoutManager(layoutManagerRequests);

        RecyclerView friendsPendingList = view.findViewById(R.id.friend_pending_list);
        friendsPendingList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerPending = new LinearLayoutManager(getActivity());
        friendsPendingList.setLayoutManager(layoutManagerPending);

        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeContainer);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        TextView noRequests = view.findViewById(R.id.friends_no_requests);
        TextView noPending = view.findViewById(R.id.friends_no_pending);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });

        return view;
    }
}
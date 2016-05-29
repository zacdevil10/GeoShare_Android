package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class FriendsPendingFragment extends Fragment {

    private ListView friendsPendingList;
    private ListAdapter friendsPendingAdapter;

    private SwipeRefreshLayout swipeRefresh;

    private FriendPendingTask friendPendingTask = null;

    private RequestQueue requestQueue;
    private JsonArrayRequest request;

    String pID;
    String mUsername;

    DatabaseHelper db;

    public FriendsPendingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        friendsPendingList = (ListView) view.findViewById(R.id.friend_pending_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        requestQueue = Volley.newRequestQueue(getContext());

        db = new DatabaseHelper(getContext());

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friendPendingTask = new FriendPendingTask(mUsername, pID);
                friendPendingTask.execute((Void) null);
            }
        });

        friendPendingTask = new FriendPendingTask(mUsername, pID);
        friendPendingTask.execute((Void) null);

        return view;
    }

    public class FriendPendingTask extends AsyncTask<Void, Void, ArrayList> {

        private final String mUsername;
        private final String pID;

        FriendPendingTask(String username, String pIDFinal) {
            mUsername = username;
            pID = pIDFinal;
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final ArrayList friend_pending_username = new ArrayList<>();

            request = new JsonArrayRequest("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/pending/", future, future){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("REST_API_TOKEN", pID);
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", System.getProperty("http.agent"));
                    return headers;
                }
            };

            requestQueue.add(request);

            try {
                JSONArray response = null;

                while (response == null) {
                    try {
                        response = future.get(30, TimeUnit.SECONDS);

                        if (response != null) {
                            for (int i=0;i<response.length();i++) {
                                try {
                                    friend_pending_username.add(response.get(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getContext(), "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }


            return friend_pending_username;
        }

        @Override
        protected void onPostExecute(final ArrayList username) {
            friendPendingTask = null;

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            friendsPendingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.friends_list_item, R.id.friend_name, username);
            friendsPendingList.setAdapter(friendsPendingAdapter);
        }

        @Override
        protected void onCancelled() {
            friendPendingTask = null;
        }
    }
}
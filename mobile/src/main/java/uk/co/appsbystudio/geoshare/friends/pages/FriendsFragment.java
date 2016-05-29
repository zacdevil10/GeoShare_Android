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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class FriendsFragment extends Fragment {

    private ListView friendsList;
    private ListAdapter friendsAdapter;

    private SwipeRefreshLayout swipeRefresh;

    private FriendsTask friendsTask = null;

    private RequestQueue requestQueue;
    private JsonArrayRequest request;

    String pID;
    String mUsername;

    DatabaseHelper db;

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        db = new DatabaseHelper(getContext());

        requestQueue = Volley.newRequestQueue(getContext());

        friendsList = (ListView) view.findViewById(R.id.friend_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friendsTask = new FriendsTask(mUsername, pID);
                friendsTask.execute((Void) null);
            }
        });

        friendsTask = new FriendsTask(mUsername, pID);
        friendsTask.execute((Void) null);

        return view;
    }

    public class FriendsTask extends AsyncTask<Void, Void, ArrayList> {

        private final String mUsername;
        private final String pID;

        FriendsTask(String username, String pIDFinal) {
            mUsername = username;
            pID = pIDFinal;
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();

            final ArrayList friends_username = new ArrayList<>();

            request = new JsonArrayRequest("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/", future, future){
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
                                    friends_username.add(response.get(i).toString());
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


            return friends_username;
        }

        @Override
        protected void onPostExecute(final ArrayList username) {
            friendsTask = null;

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            System.out.println(username);

            friendsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.friends_list_item, R.id.friend_name, username);
            friendsList.setAdapter(friendsAdapter);
        }

        @Override
        protected void onCancelled() {
            friendsTask = null;
        }
    }
}
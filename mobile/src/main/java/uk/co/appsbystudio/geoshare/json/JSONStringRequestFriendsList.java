package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsPendingAdapter;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsRequestAdapter;

public class JSONStringRequestFriendsList extends AsyncTask<Void, Void, ArrayList> {

    private final RecyclerView friendsList;
    private final SwipeRefreshLayout refreshList;
    private final TextView noRequest;

    private final String pID;
    private String URL;
    private final Integer arrayMethod;

    private final Context context;

    public JSONStringRequestFriendsList(Context context, RecyclerView friendsList, SwipeRefreshLayout refreshList, TextView noRequest,String URL, String pID, Integer arrayMethod) {
        this.context = context;
        this.friendsList = friendsList;
        this.refreshList = refreshList;
        this.noRequest = noRequest;
        this.pID = pID;
        this.URL = URL;
        this.arrayMethod = arrayMethod;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        RequestFuture<String> future = RequestFuture.newFuture();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<String> friends_username = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL.replace(" ", "%20"), future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("REST_API_TOKEN", pID);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        requestQueue.add(stringRequest);

        try {
            String response = null;

            while (response == null) {
                try {
                    response = future.get(30, TimeUnit.SECONDS);

                    try {
                        JSONArray friends = new JSONArray(response);

                        for (int i=0;i<friends.length();i++) {
                            try {
                                JSONObject inner = (JSONObject) friends.get(i);
                                friends_username.add((String) inner.get("username"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

        return friends_username;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        if (arrayMethod == 0) {
            FriendsAdapter friendsAdapter = new FriendsAdapter(context, arrayList);
            friendsList.setAdapter(friendsAdapter);
        } else if (arrayMethod == 1) {
            FriendsRequestAdapter friendsRequestAdapter = new FriendsRequestAdapter(context, arrayList);
            friendsList.setAdapter(friendsRequestAdapter);
            noRequest.setVisibility(arrayList.isEmpty()? View.VISIBLE : View.GONE);
        } else if (arrayMethod == 2) {
            FriendsPendingAdapter friendsPendingAdapter = new FriendsPendingAdapter(context, arrayList);
            friendsList.setAdapter(friendsPendingAdapter);
            noRequest.setVisibility(arrayList.isEmpty()? View.VISIBLE : View.GONE);
        }

        if (refreshList != null) {
            if (refreshList.isRefreshing()) {
                refreshList.setRefreshing(false);
            }
        }
    }
}

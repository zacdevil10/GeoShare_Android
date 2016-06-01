package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsAdapter;

public class JSONStringRequests extends AsyncTask<Void, Void, ArrayList> {

    private final RecyclerView friendsList;
    private final SwipeRefreshLayout refreshList;

    private final String pID;
    private final String URL;

    private final Context context;

    public JSONStringRequests (Context context, RecyclerView friendsList, SwipeRefreshLayout refreshList, String URL, String pID) {
        this.context = context;
        this.friendsList = friendsList;
        this.refreshList = refreshList;
        this.pID = pID;
        this.URL = URL;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        RequestFuture<String> future = RequestFuture.newFuture();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<String> friends_username = new ArrayList<>();
        ArrayList<Bitmap> friends_profile_pictures = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, future, future) {
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
        FriendsAdapter friendsAdapter = new FriendsAdapter(context, arrayList);
        friendsList.setAdapter(friendsAdapter);

        if (refreshList.isRefreshing()) {
            refreshList.setRefreshing(false);
        }

    }
}

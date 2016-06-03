package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.friends.pages.FriendsFragment;
import uk.co.appsbystudio.geoshare.friends.pages.FriendsPendingFragment;

public class JSONObjectRequest extends AsyncTask<Void, Void, Boolean> {

    private String URL;
    private String status;
    private String pID;
    private boolean success = false;

    private final Context context;

    public JSONObjectRequest(Context context, String URL, String status, String pID) {
        this.URL = URL;
        this.context = context;
        this.status = status;
        this.pID = pID;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("action", status);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest request = new StringRequest(Request.Method.POST, URL, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("REST_API_TOKEN", pID);
                headers.put("X-HTTP-Method-Override", "PATCH");
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("action", status);
                return hashMap;
            }
        };

        requestQueue.add(request);

        try {
            String response = null;

            while (response == null) {
                try {
                    response = future.get(30, TimeUnit.SECONDS);
                    success = true;
                } catch (InterruptedException e) {
                    success = false;
                    Thread.currentThread().interrupt();
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(context, "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            new FriendsFragment().refreshList();
            new FriendsPendingFragment().refreshList();
        }
    }
}
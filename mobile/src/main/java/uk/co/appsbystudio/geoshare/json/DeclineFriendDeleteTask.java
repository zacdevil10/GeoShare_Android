package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeclineFriendDeleteTask extends AsyncTask<Void, Void, Boolean> {

    private final String URL;
    private final String pID;
    private boolean success = true;

    private final Context context;

    public DeclineFriendDeleteTask(Context context, String URL, String pID) {
        this.URL = URL;
        this.context = context;
        this.pID = pID;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("action", "ignore");

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, URL, new JSONObject(hashMap), null, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("REST-API-TOKEN", pID);
                headers.put("X-HTTP-Method-Override", "PATCH");
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                System.out.println(response.statusCode);

                success = response.statusCode == 200;

                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(request);

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            //TODO: refresh list
            System.out.println("Refreshing friends list.");
        }
    }
}
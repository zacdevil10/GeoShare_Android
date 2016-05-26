package uk.co.appsbystudio.geoshare.json;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSONRequests {

    private RequestQueue requestQueue;
    private JsonObjectRequest request;
    RequestFuture<JSONObject> future = RequestFuture.newFuture();

    public boolean onPostRequest(String URL) {
        return false;
    }

    public boolean onGetRequest(String URL) {
        return false;
    }

    public void onDeleteRequest(String URL, final String pID, Context mContext) {
        requestQueue = Volley.newRequestQueue(mContext);

        request = new JsonObjectRequest(Request.Method.DELETE, URL, null, future, future){
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
    }

}

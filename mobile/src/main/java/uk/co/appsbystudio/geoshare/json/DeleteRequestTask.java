package uk.co.appsbystudio.geoshare.json;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DeleteRequestTask {

    public void onDeleteRequest(String URL, final String pID, Context mContext) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, URL, null, null, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("REST-API-TOKEN", pID);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        requestQueue.add(request);
    }

}

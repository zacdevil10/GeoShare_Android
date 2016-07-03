package uk.co.appsbystudio.geoshare.json;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSONRequests {

    private RequestQueue requestQueue;
    private JsonObjectRequest request;

    public void onPostRequest(String URL, final String pID, Context mContext) {
        System.out.println(URL.replace(" ", "%20"));
        requestQueue = Volley.newRequestQueue(mContext);

        request = new JsonObjectRequest(Request.Method.POST, URL.replace(" ", "%20"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Done");
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
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

    public void onDeleteRequest(String URL, final String pID, Context mContext) {
        requestQueue = Volley.newRequestQueue(mContext);

        request = new JsonObjectRequest(Request.Method.DELETE, URL, null, null, null){
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

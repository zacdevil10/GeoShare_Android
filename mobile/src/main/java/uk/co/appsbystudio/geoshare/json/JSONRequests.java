package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

public class JSONRequests {

    private RequestQueue requestQueue;
    private JsonObjectRequest request;

    boolean result;

    public boolean onPostRequest(String URL, final String pID, Context mContext) {
        requestQueue = Volley.newRequestQueue(mContext);

        request = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                result = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result = false;
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("REST_API_TOKEN", pID);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        return result;
    }

    public ArrayList onGetRequest(String URL, final String pID, Context mContext) {

        final ArrayList<String> userdata = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(mContext);



        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        request = new JsonObjectRequest(Request.Method.GET, URL, null, future, future) {
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
            JSONObject response = null;

            while (response == null) {
                try {
                    response = future.get(30, TimeUnit.SECONDS);
                    try {
                        userdata.add((String) response.get("username"));
                        userdata.add((String) response.get("email"));
                        userdata.add((String) response.get("created"));
                        userdata.add((String) response.get("modified"));
                        userdata.add((String) response.get("findByEmail"));
                        userdata.add((String) response.get("seenTutorial"));
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
            Toast.makeText(mContext, "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
        }

        return userdata;
    }

    public boolean onPatchRequest(String URL, final String pID, Context mContext, String key, String value) {
        requestQueue = Volley.newRequestQueue(mContext);

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(key, value);

        request = new JsonObjectRequest(Request.Method.PATCH, URL, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                result = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result = false;
            }
        }) {
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

        return result;
    }

    public void onDeleteRequest(String URL, final String pID, Context mContext) {
        requestQueue = Volley.newRequestQueue(mContext);

        request = new JsonObjectRequest(Request.Method.DELETE, URL, null, null, null){
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

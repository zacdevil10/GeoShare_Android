package uk.co.appsbystudio.geoshare.json;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.appsbystudio.geoshare.database.ReturnData;

public class SingleShareLocationTask extends AsyncTask<Void, Void, Boolean>{

    private final Context context;
    private final String username;
    private final Double longitude;
    private final Double latitude;

    public SingleShareLocationTask(Context context, String username, Double longitude, Double latitude) {
        this.context = context;
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("type", "peer");
        hashMap.put("recipient", username);
        hashMap.put("long", String.valueOf(longitude));
        hashMap.put("lat", String.valueOf(latitude));

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, "https://geoshare.appsbystudio.co.uk/api/share/", new JSONObject(hashMap), null, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("REST-API-TOKEN", new ReturnData().getpID(context));
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        requestQueue.add(request);

        System.out.println("Yo");

        return null;
    }
}

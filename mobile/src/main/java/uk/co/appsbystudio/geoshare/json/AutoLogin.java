package uk.co.appsbystudio.geoshare.json;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.login.LoginActivity;

public class AutoLogin extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final String pID;
    private final String username;
    private final ProgressDialog progressDialog;

    public AutoLogin(Context context, String pID, String username, ProgressDialog progressDialog) {
        this.context = context;
        this.pID = pID;
        this.username = username;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://geoshare.appsbystudio.co.uk/api/user/" + username + "/session/" + pID, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray pIDLive = new JSONArray(s);

                    JSONObject inner = (JSONObject) pIDLive.get(0);

                    if (Objects.equals(inner.getString("token"), pID)) {
                        login();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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

        requestQueue.add(stringRequest);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
    }

    private void login() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        LoginActivity loginActivity = (LoginActivity) context;
        loginActivity.finish();
    }
}

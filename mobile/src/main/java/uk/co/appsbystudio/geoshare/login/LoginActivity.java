package uk.co.appsbystudio.geoshare.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class LoginActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private SignupFragment signupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinator);

        loginFragment = new LoginFragment();
        signupFragment = new SignupFragment();

        getSession();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
    }

    public void buttonCallback(View button) {
        if(button.getId() == R.id.sign_up) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .replace(loginFragment.getId(), signupFragment).addToBackStack(null).commit();
        }
        if (button.getId() == R.id.sign_up_sign_up) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .replace(signupFragment.getId(), loginFragment).commit();
        }
    }

    private String pIDDatabase;
    private String mUsernameDatabase;

    private void getSession() {

        DatabaseHelper db = new DatabaseHelper(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pIDDatabase = id.getpID();
            mUsernameDatabase = id.getUsername().replace(" ", "%20");
        }

        if (pIDDatabase != null && isConnection_status()) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsernameDatabase + "/session/" + pIDDatabase, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONArray pIDLive = new JSONArray(s);

                        JSONObject inner = (JSONObject) pIDLive.get(0);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Objects.equals(inner.getString("token"), pIDDatabase)) {
                                loginFragment.login();
                            }
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
                    headers.put("REST_API_TOKEN", pIDDatabase);
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", System.getProperty("http.agent"));
                    return headers;
                }
            };

            requestQueue.add(stringRequest);

        } else {
            if (!isConnection_status()) {
                System.out.println("No network");
            }
            if (pIDDatabase == null) {
                System.out.println("Session gone");
            }
        }

        db.close();
    }

    private boolean connection_status = false;

    private boolean isConnection_status() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                connection_status = true;
            } else {
                networkInfo = connectivityManager.getNetworkInfo(1);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    connection_status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return connection_status;
    }
}

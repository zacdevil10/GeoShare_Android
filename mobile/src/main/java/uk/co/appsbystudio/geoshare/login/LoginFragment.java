package uk.co.appsbystudio.geoshare.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.marlonmafra.android.widget.EditTextPassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;

public class LoginFragment extends Fragment {

    private UserLoginTask mAuthTask = null;

    private EditText usernameEntry;
    private EditTextPassword passwordEntry;
    private CheckBox rememberMe;
    private Integer rememberInt;

    private RequestQueue requestQueue;

    private boolean success = false;
    private boolean connection_status = false;
    private String mUsernameDatabase;

    private DatabaseHelper db;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        db = new DatabaseHelper(getActivity());

        requestQueue = Volley.newRequestQueue(getContext());

        usernameEntry = (EditText) view.findViewById(R.id.username);
        passwordEntry = (EditTextPassword) view.findViewById(R.id.password);
        Button loginButton = (Button) view.findViewById(R.id.log_in);
        rememberMe = (CheckBox) view.findViewById(R.id.remember);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            mUsernameDatabase = id.getUsername();
        }

        if (mUsernameDatabase != null) {
            usernameEntry.setText(mUsernameDatabase);
            passwordEntry.requestFocus();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnection_status()) {
                    attemptLogin();
                } else {
                    System.out.println("No network");
                }
            }
        });

        return view;
    }

    private boolean isConnection_status() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        if (rememberMe.isChecked()) {
            rememberInt = 1;
        } else {
            rememberInt = 0;
        }

        String username = usernameEntry.getText().toString();
        String password = passwordEntry.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordEntry.setError(getString(R.string.error_field_required));
            focusView = passwordEntry;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEntry.setError(getString(R.string.error_field_required));
            focusView = usernameEntry;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(username, password, rememberInt);
            mAuthTask.execute((Void) null);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mUsername;
        private final String mPassword;
        private final Integer mRemember;

        UserLoginTask(String email, String password, Integer remember) {
            mUsername = email;
            mPassword = password;
            mRemember = remember;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("password", mPassword);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername.replace(" ", "%20") + "/session/", new JSONObject(hashMap), future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
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
                        success = true;
                        UserModel userModel = null;
                        try {
                            userModel = new UserModel((String) response.get("pID"), mUsername.replace("%20", " "), null, rememberInt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        db.addUsers(userModel);
                        db.close();
                    } catch (InterruptedException e) {
                        success = false;
                        Thread.currentThread().interrupt();
                    }
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getContext(), "Timeout. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }

            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                login();
            } else {
                passwordEntry.setError(getString(R.string.error_incorrect_password_username));
                passwordEntry.requestFocus();
            }
            db.close();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public void login() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}

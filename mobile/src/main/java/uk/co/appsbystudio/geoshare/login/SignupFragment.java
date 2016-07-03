package uk.co.appsbystudio.geoshare.login;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.marlonmafra.android.widget.EditTextPassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.settings.ConfirmationDialog;

public class SignUpFragment extends Fragment {

    private UserSignUpTask mAuthTask = null;

    private EditText emailEntry;
    private EditText usernameEntry;
    private EditTextPassword passwordEntry;

    private RequestQueue requestQueue;

    private ProgressDialog progressDialog;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container,false);

        usernameEntry = (EditText) view.findViewById(R.id.username_signup);
        emailEntry = (EditText) view.findViewById(R.id.email_signup);
        passwordEntry = (EditTextPassword) view.findViewById(R.id.password_signup);

        Button signUp = (Button) view.findViewById(R.id.sign_up_sign_up);

        requestQueue = Volley.newRequestQueue(getContext());

        progressDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating account...");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        return view;
    }

    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }

        String username = usernameEntry.getText().toString();
        String email = emailEntry.getText().toString();
        String password = passwordEntry.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordEntry.setError(getString(R.string.error_field_required));
            focusView = passwordEntry;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailEntry.setError(getString(R.string.error_field_required));
            focusView = emailEntry;
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
            mAuthTask = new UserSignUpTask(username, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    public class UserSignUpTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;

        UserSignUpTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        Integer success = null;

        @Override
        protected void onPreExecute() {
            progressDialog.show();

            progressDialog.setCancelable(false);

        }

        @Override
        protected Integer doInBackground(Void... params) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("username", mUsername);
            hashMap.put("email", mEmail);
            hashMap.put("password", mPassword);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://geoshare.appsbystudio.co.uk/api/user/", new JSONObject(hashMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    success = 201;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    String responseString = null;

                    if (response != null && response.data != null){
                        switch (response.statusCode) {
                            case 409:
                                try {
                                    responseString = new String(response.data);
                                    JSONObject responseObject = new JSONObject(responseString);
                                    responseString = responseObject.getString("duplicate");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (responseString != null) {
                                    if ("username".equals(responseString)) {
                                        success = 1;
                                    } else if (Objects.equals(responseString, "email")) {
                                        success = 2;
                                    }
                                } else {
                                    success = 409;
                                }
                                break;
                            case 500:
                                success = 500;
                                break;
                            case 400:
                                success = 400;

                                try {
                                    responseString = new String(response.data);
                                    JSONObject responseObject = new JSONObject(responseString);
                                    responseString = responseObject.getString("invalid");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (responseString != null) {
                                    System.out.println(responseString);
                                    if ("username".equals(responseString)) {
                                        success = 3;
                                    } else if ("email".equals(responseString)) {
                                        success = 4;
                                    } else if ("password".equals(responseString)) {
                                        success = 5;
                                    }
                                } else {
                                    success = 409;
                                }
                                break;
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", System.getProperty("http.agent"));
                    return headers;
                }

            };

            requestQueue.add(request);

            long time = System.currentTimeMillis();

            System.out.println(success);

            while (success == null && (System.currentTimeMillis()-time) < 10000)

            if (success == null) {
                success = 0;
            }

            System.out.println(success);

            return success;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;

            if (success == 201) {
                usernameEntry.setText("");
                emailEntry.setText("");
                passwordEntry.setText("");
                progressDialog.dismiss();
                getFragmentManager().popBackStack();
                confirmationDialog();
            } else if (success == 1) {
                progressDialog.dismiss();
                passwordEntry.setText("");
                usernameEntry.setError("This username is already in use.");
                usernameEntry.requestFocus();
            } else if (success == 2) {
                progressDialog.dismiss();
                passwordEntry.setText("");
                emailEntry.setError("An account is associated with this email.");
                emailEntry.requestFocus();
            } else if (success == 3) {
                progressDialog.dismiss();
                passwordEntry.setText("");
                usernameEntry.setError("This username is invalid.");
                usernameEntry.requestFocus();
            } else if (success == 4) {
                progressDialog.dismiss();
                passwordEntry.setText("");
                emailEntry.setError("This email address is invalid.");
                emailEntry.requestFocus();
            } else if (success == 5) {
                progressDialog.dismiss();
                passwordEntry.setText("");
                passwordEntry.setError("This password is invalid.\nMust be X characters long.\nMust contain an uppercase letter.\nMust conatin a lowercase letter.\nMust contain a number or special character.");
                passwordEntry.requestFocus();
            } else if (success == 0) {
                Toast.makeText(getContext(), "Could not connect to the registration server.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else if (success == 400) {
                System.out.println("The server response is useless");
                progressDialog.dismiss();
            } else {
                Toast.makeText(getContext(), success + " code response.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        private void confirmationDialog() {
            FragmentManager fragmentManager = getFragmentManager();
            DialogFragment profileDialog = new ConfirmationDialog();
            profileDialog.show(fragmentManager, "");
        }

    }
}

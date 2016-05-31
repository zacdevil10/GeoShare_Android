package uk.co.appsbystudio.geoshare.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.marlonmafra.android.widget.EditTextPassword;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.R;

public class SignupFragment extends Fragment {

    private UserSignUpTask mAuthTask = null;

    private EditText emailEntry;
    private EditText usernameEntry;
    private EditTextPassword passwordEntry;

    private RequestQueue requestQueue;

    private boolean success = false;

    public SignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container,false);

        usernameEntry = (EditText) view.findViewById(R.id.username_signup);
        emailEntry = (EditText) view.findViewById(R.id.email_signup);
        passwordEntry = (EditTextPassword) view.findViewById(R.id.password_signup);

        Button signUp = (Button) view.findViewById(R.id.sign_up_sign_up);

        requestQueue = Volley.newRequestQueue(getContext());

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

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEntry.setError(getString(R.string.error_invalid_password));
            focusView = passwordEntry;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEntry.setError(getString(R.string.error_field_required));
            focusView = usernameEntry;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            usernameEntry.setError(getString(R.string.error_invalid_email));
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

    private boolean isUsernameValid(String username) {
        return username.length() > 5;
    }

    private boolean isEmailValid(String email) {
        return email.length() > 5;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;

        UserSignUpTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("username", mUsername);
            hashMap.put("email", mEmail);
            hashMap.put("password", mPassword);



            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://geoshare.appsbystudio.co.uk/api/user/", new JSONObject(hashMap), future, future) {
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
                    } catch (InterruptedException e) {
                        success = false;
                        Thread.currentThread().interrupt();
                    }
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
                success = false;
            } catch (TimeoutException e) {
                success = false;
                Toast.makeText(getContext(), "Timeout", Toast.LENGTH_LONG).show();
            }

            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                usernameEntry.setText("");
                emailEntry.setText("");
                passwordEntry.setText("");
                getFragmentManager().popBackStack();
            } else {
                passwordEntry.setText("");
                passwordEntry.setError(null);
                passwordEntry.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

    }
}

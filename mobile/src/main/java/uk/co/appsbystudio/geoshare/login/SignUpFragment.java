package uk.co.appsbystudio.geoshare.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.marlonmafra.android.widget.EditTextPassword;

import uk.co.appsbystudio.geoshare.R;

public class SignUpFragment extends Fragment {

    private EditText emailEntry;
    private EditText usernameEntry;
    private EditTextPassword passwordEntry;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container,false);

        usernameEntry = view.findViewById(R.id.username_signup);
        emailEntry = view.findViewById(R.id.email_signup);
        passwordEntry = view.findViewById(R.id.password_signup);

        Button signUp = view.findViewById(R.id.sign_up_sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        return view;
    }

    private void attemptSignUp() {
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
            //TODO: Sign up here?
        }
    }
}

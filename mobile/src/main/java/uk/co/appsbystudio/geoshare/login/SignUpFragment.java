package uk.co.appsbystudio.geoshare.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.marlonmafra.android.widget.EditTextPassword;

import uk.co.appsbystudio.geoshare.R;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private EditText emailEntry;
    private EditText nameEntry;
    private EditTextPassword passwordEntry;
    String name;
    String email;
    String password;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container,false);

        firebaseAuth = FirebaseAuth.getInstance();

        nameEntry = (EditText) view.findViewById(R.id.name_signup);
        emailEntry = (EditText) view.findViewById(R.id.email_signup);
        passwordEntry = (EditTextPassword) view.findViewById(R.id.password_signup);

        Button signUp = (Button) view.findViewById(R.id.sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        return view;
    }

    private void attemptSignUp() {
        name = nameEntry.getText().toString();
        email = emailEntry.getText().toString();
        password = passwordEntry.getText().toString();

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

        if (TextUtils.isEmpty(name)) {
            nameEntry.setError(getString(R.string.error_field_required));
            focusView = nameEntry;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            signUp();
        }
    }

    private void signUp() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null) Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            getFragmentManager().popBackStack();
                        }
                    }
                });
    }
}

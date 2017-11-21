package uk.co.appsbystudio.geoshare.login;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.Connectivity;
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation;
import uk.co.appsbystudio.geoshare.utils.services.OnNetworkStateChangeListener;
import uk.co.appsbystudio.geoshare.utils.setup.InitialSetupActivity;

public class LoginActivity extends AppCompatActivity implements OnNetworkStateChangeListener.NetworkStateReceiverListener {

    private OnNetworkStateChangeListener networkStateChangeListener;

    protected EditText nameEntry;
    private EditText emailEntry;
    private EditText passwordEntry;

    private Button signUp, signUpShow, forgotPassword, done, back;

    private CircularProgressButton login;

    private String name, email, password;

    private boolean showingSignUp = false;
    private boolean showingForgotPassword = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        networkStateChangeListener = new OnNetworkStateChangeListener();
        networkStateChangeListener.addListener(this);
        this.registerReceiver(networkStateChangeListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        nameEntry = findViewById(R.id.nameInput);
        emailEntry = findViewById(R.id.emailInput);
        passwordEntry = findViewById(R.id.passwordInput);
        login = findViewById(R.id.log_in);
        signUp = findViewById(R.id.sign_up);
        signUpShow = findViewById(R.id.open_sign_up);
        done = findViewById(R.id.done);
        back = findViewById(R.id.back);
        forgotPassword = findViewById(R.id.forgot_password);

        signUpShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignUpView();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(false);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(true);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(emailEntry.getText().toString())) {
                    firebaseAuth.sendPasswordResetEmail(emailEntry.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        setForgotPasswordView();
                                        Toast.makeText(LoginActivity.this, "Hmm...That didn't seem to work!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    setLoginView();
                                    Toast.makeText(LoginActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    emailEntry.setError(getString(R.string.error_field_required));
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForgotPasswordView();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginView();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                login(firebaseAuth);
            }
        };
    }

    private void login(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && Connectivity.isConnected(LoginActivity.this)) {
            Intent intent;
            if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("first_run", true)) {
                intent = new Intent(LoginActivity.this, InitialSetupActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }

    private void setSignUpView() {
        nameEntry.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.VISIBLE);

        login.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.GONE);
        signUpShow.setVisibility(View.GONE);

        back.setVisibility(View.VISIBLE);

        nameEntry.requestFocus();

        showingSignUp = true;
    }

    private void setForgotPasswordView() {
        nameEntry.setVisibility(View.GONE);
        passwordEntry.setVisibility(View.GONE);

        login.setVisibility(View.GONE);
        signUp.setVisibility(View.GONE);
        done.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.GONE);
        signUpShow.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        emailEntry.requestFocus();

        showingForgotPassword = true;
    }

    private  void setLoginView() {
        nameEntry.setVisibility(View.GONE);
        passwordEntry.setVisibility(View.VISIBLE);

        login.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.GONE);
        done.setVisibility(View.GONE);

        forgotPassword.setVisibility(View.VISIBLE);
        signUpShow.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);

        emailEntry.requestFocus();

        showingSignUp = false;
        showingForgotPassword = false;
    }

    private void validate(boolean signUp) {
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

        if (showingSignUp && TextUtils.isEmpty(name)) {
            nameEntry.setError(getString(R.string.error_field_required));
            focusView = nameEntry;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else if (!signUp){
            login.startAnimation();
            login();
        } else {
            showingSignUp = false;
            signUp();
        }
    }

    private void login() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null) Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            login.revertAnimation();
                        }
                    }
                });
    }

    private void signUp() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null) Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user = firebaseAuth.getCurrentUser();
                            UserInformation userInformation = new UserInformation(name, name.toLowerCase());
                            if (user != null) {
                                user.updateProfile(profileChangeRequest);
                                String userId = user.getUid();
                                ref.child("users").child(userId).setValue(userInformation);
                            }

                            setLoginView();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateChangeListener.removeListener(this);
        this.unregisterReceiver(networkStateChangeListener);
        login.dispose();
    }

    @Override
    public void onBackPressed() {
        if (showingSignUp || showingForgotPassword) {
            setLoginView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void networkAvailable() {
        login(firebaseAuth);
    }

    @Override
    public void networkUnavailable() {
        Snackbar.make(findViewById(R.id.startup_layout_parent), "No network connection detected", Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void networkWifi() {

    }

    @Override
    public void networkMobile() {

    }
}

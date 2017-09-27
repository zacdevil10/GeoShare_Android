package uk.co.appsbystudio.geoshare.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.UserInformation;
import uk.co.appsbystudio.geoshare.utils.setup.InitialSetupActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final boolean LOCAL_LOGV = true;

    private EditText nameEntry;
    private EditText emailEntry;
    private EditText passwordEntry;
    private Button signUp;
    private Button signUpShow;
    private Button forgotPassword;
    private CircularProgressButton login;
    private String name;
    private String email;
    private String password;
    private Bitmap error;

    private Intent intent;

    private SharedPreferences sharedPreferences;

    private static final int GET_PERMS = 1;

    private boolean showingSignUp = false;
    private boolean showingForgotPassword = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getPermissions();

        nameEntry = findViewById(R.id.nameInput);
        emailEntry = findViewById(R.id.emailInput);
        passwordEntry = findViewById(R.id.passwordInput);
        login = findViewById(R.id.log_in);
        signUp = findViewById(R.id.sign_up);
        signUpShow = findViewById(R.id.open_sign_up);
        forgotPassword = findViewById(R.id.forgot_password);

        error = BitmapFactory.decodeResource(LoginActivity.this.getResources(), R.drawable.ic_close_white_48px);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
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
                    if (showingForgotPassword) {
                        emailEntry.setError(getString(R.string.error_field_required));
                    } else {
                        setForgotPasswordView();
                    }
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    if (LOCAL_LOGV) Log.v(TAG, "User has signed in: " + currentUser.getUid());
                    if (sharedPreferences.getBoolean("first_run", true)) {
                        intent = new Intent(LoginActivity.this, InitialSetupActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void setSignUpView() {
        nameEntry.setVisibility(View.VISIBLE);
        emailEntry.setVisibility(View.VISIBLE);
        passwordEntry.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.GONE);
        signUpShow.setVisibility(View.GONE);
        login.setVisibility(View.GONE);

        nameEntry.requestFocus();

        showingSignUp = true;
    }

    private void setForgotPasswordView() {
        nameEntry.setVisibility(View.GONE);
        emailEntry.setVisibility(View.VISIBLE);
        passwordEntry.setVisibility(View.GONE);
        signUp.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.VISIBLE);
        signUpShow.setVisibility(View.GONE);
        login.setVisibility(View.GONE);

        emailEntry.requestFocus();

        forgotPassword.setBackground(getDrawable(R.drawable.round_edge_green_background));
        forgotPassword.setText("DONE");

        showingForgotPassword = true;
    }

    private  void setLoginView() {
        nameEntry.setVisibility(View.GONE);
        emailEntry.setVisibility(View.VISIBLE);
        passwordEntry.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.VISIBLE);
        signUpShow.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);

        emailEntry.requestFocus();

        if (showingForgotPassword) {
            forgotPassword.setText("FORGOT PASSWORD?");
            forgotPassword.setBackgroundResource(0);
        }

        showingSignUp = false;
        showingForgotPassword = false;
    }

    private void validate(boolean signUp) {
        if (LOCAL_LOGV) Log.v(TAG, "Validating");
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
            if (LOCAL_LOGV) Log.v(TAG, "Login?");
            login.startAnimation();
            login();
        } else {
            if (LOCAL_LOGV) Log.v(TAG, "Signup?");
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
                            if (error != null) {
                                login.doneLoadingAnimation(Color.WHITE, error);
                            }
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
                            if (LOCAL_LOGV) Log.v(TAG, "Updating profile");
                            user = firebaseAuth.getCurrentUser();
                            UserInformation userInformation = new UserInformation(name, name.toLowerCase(), email);
                            if (user != null) {
                                String userId = user.getUid();
                                ref.child("users").child(userId).setValue(userInformation);
                                if (LOCAL_LOGV) Log.v(TAG, "Updating profile was successful");
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
}

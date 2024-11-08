package com.example.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// UserValidator: handles validation for username and password
class UserValidator {
    private final EditText usernameField;
    private final EditText passwordField;

    public UserValidator(EditText usernameField, EditText passwordField) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
    }

    public boolean validateInputs() {
        return validateUsername() && validatePassword();
    }

    public boolean validateUsername() {
        String val = usernameField.getText().toString();
        if (val.isEmpty()) {
            usernameField.setError("Username cannot be empty");
            return false;
        }
        usernameField.setError(null);
        return true;
    }

    public boolean validatePassword() {
        String val = passwordField.getText().toString();
        if (val.isEmpty()) {
            passwordField.setError("Password cannot be empty");
            return false;
        }
        passwordField.setError(null);
        return true;
    }
}

// UserAuthenticator: handles Firebase authentication for the user
class UserAuthenticator {
    private final DatabaseReference userDatabaseRef;

    public UserAuthenticator() {
        this.userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void authenticateUser(String username, String password, AuthListener listener) {
        Query checkUserQuery = userDatabaseRef.orderByChild("username").equalTo(username);
        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(password)) {
                            listener.onAuthSuccess(userSnapshot);
                            return;
                        }
                    }
                    listener.onAuthFailure("Invalid Credentials");
                } else {
                    listener.onAuthFailure("User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onAuthError(error);
            }
        });
    }

    // Listener interface to handle authentication callbacks
    interface AuthListener {
        void onAuthSuccess(DataSnapshot userSnapshot);
        void onAuthFailure(String errorMessage);
        void onAuthError(DatabaseError error);
    }
}

// LoginActivity: main activity class handling login functionality
public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private UserValidator userValidator;
    private UserAuthenticator userAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        // Initialize components
        userValidator = new UserValidator(loginUsername, loginPassword);
        userAuthenticator = new UserAuthenticator();

        // Set up login button action
        loginButton.setOnClickListener(view -> {
            if (userValidator.validateInputs()) {
                String username = loginUsername.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                authenticateUser(username, password);
            }
        });

        // Set up signup redirection
        signupRedirectText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void authenticateUser(String username, String password) {
        userAuthenticator.authenticateUser(username, password, new UserAuthenticator.AuthListener() {
            @Override
            public void onAuthSuccess(DataSnapshot userSnapshot) {
                navigateToLevelPath(userSnapshot);
            }

            @Override
            public void onAuthFailure(String errorMessage) {
                if ("Invalid Credentials".equals(errorMessage)) {
                    loginPassword.setError(errorMessage);
                    loginPassword.requestFocus();
                } else if ("User does not exist".equals(errorMessage)) {
                    loginUsername.setError(errorMessage);
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onAuthError(DatabaseError error) {
                // Handle database error (optional: show error message or log it)
            }
        });
    }

    private void navigateToLevelPath(DataSnapshot userSnapshot) {
        String nameFromDB = userSnapshot.child("name").getValue(String.class);
        String emailFromDB = userSnapshot.child("email").getValue(String.class);
        String usernameFromDB = userSnapshot.child("username").getValue(String.class);

        Intent intent = new Intent(LoginActivity.this, LevelPathActivity.class);
        intent.putExtra("name", nameFromDB);
        intent.putExtra("email", emailFromDB);
        intent.putExtra("username", usernameFromDB);
        startActivity(intent);
    }
}

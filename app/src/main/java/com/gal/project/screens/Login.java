package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gal.project.R;
import com.gal.project.models.User;
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;
import com.gal.project.utils.SharedPreferencesUtil;
import com.gal.project.utils.Validator;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText etEmail, etPassword;
    Button btnLog;


    private static final String TAG = "LoginActivity";


    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private User user=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


      user=  SharedPreferencesUtil.getUser(Login.this);


        /// get the views


        etEmail= findViewById(R.id.etEmail);
        etPassword= findViewById(R.id.etPassword);
        btnLog = findViewById(R.id.btnLog);

        if(user!=null) {
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }
        btnLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
     String    email = etEmail.getText().toString();
      String   password = etPassword.getText().toString();
        Log.d("TAG", "onClick:btnSignIn");

        /// log the email and password
        Log.d(TAG, "onClick: Email: " + email);
        Log.d(TAG, "onClick: Password: " + password);

        Log.d(TAG, "onClick: Validating input...");
        /// Validate input
        if (!checkInput(email, password)) {
            /// stop if input is invalid
            return;
        }

        Log.d(TAG, "onClick: Logging in user...");

        /// Login user
        loginUser(email, password);


    }


    /// Method to check if the input is valid
    /// It checks if the email and password are valid
    /// @see Validator#isEmailValid(String)
    /// @see Validator#isPasswordValid(String)
    private boolean checkInput(String email, String password) {
        if (!Validator.isEmailValid(email)) {
            Log.e(TAG, "checkInput: Invalid email address");
            /// show error message to user
            etEmail.setError("Invalid email address");
            /// set focus to email field
            etEmail.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            Log.e(TAG, "checkInput: Password must be at least 6 characters long");
            /// show error message to user
            etPassword.setError("Password must be at least 6 characters long");
            /// set focus to password field
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        authenticationService.signIn(email, password, new AuthenticationService.AuthCallback() {
            /// Callback method called when the operation is completed
            /// @param uid the user ID of the user that is logged in
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User logged in successfully");
                /// get the user data from the database


                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<com.gal.project.models.User>() {
                    @Override
                    public void onCompleted(com.gal.project.models.User u) {
                        user = u;
                        Log.d(TAG, "onCompleted: User data retrieved successfully");
                        /// save the user data to shared preferences
                        SharedPreferencesUtil.saveUser(Login.this, user);
                        /// Redirect to main activity and clear back stack to prevent user from going back to login screen
                        Intent mainIntent = new Intent(Login.this, After.class);
                        /// Clear the back stack (clear history) and start the MainActivity
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);

                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                        /// Show error message to user
                        etPassword.setError("Invalid email or password");
                        etPassword.requestFocus();
                        /// Sign out the user if failed to retrieve user data
                        /// This is to prevent the user from being logged in again
                        authenticationService.signOut();

                    }
                });


            }



            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to log in user", e);
                /// Show error message to user
                etPassword.setError("Invalid email or password");
                etPassword.requestFocus();

            }
        });
    }



}



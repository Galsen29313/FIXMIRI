package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gal.project.R;
import com.gal.project.models.User;
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;
import com.gal.project.utils.SharedPreferencesUtil;

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText etFname, etLname, etPhone, etEmail, etPassword;
    String  Fname, Lname, Phone, Email, Password;
    Button btnReg;
    Boolean isVaild = true;


    private static final String TAG = "RegisterActivity";




    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private com.gal.project.models.User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


/// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();



        initViews();




    }


    private void initViews() {
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnReg) {
            Fname = etFname.getText().toString();
            Lname = etLname.getText().toString();
            Phone = etPhone.getText().toString();
            Email = etEmail.getText().toString();
            Password = etPassword.getText().toString();
            if (!Fname.isEmpty() && !Lname.isEmpty() && !Phone.isEmpty() && !Email.isEmpty() && !Password.isEmpty()) {
                etFname.setText("");
                etLname.setText("");
                etPhone.setText("");
                etEmail.setText("");
                etPassword.setText("");

                if (Fname.length() < 2) {
                    Toast.makeText(Register.this, "שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (Lname.length() < 2) {
                    Toast.makeText(Register.this, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (Phone.length() < 9 || Phone.length() > 10) {
                    Toast.makeText(Register.this, "מספר הטלפון לא תקין", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (!Email.contains("@")) {
                    Toast.makeText(Register.this, "כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (Password.length() < 6) {
                    Toast.makeText(Register.this, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (Password.length() > 20) {
                    Toast.makeText(Register.this, "הסיסמה ארוכה מדי", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }


           if  (   isVaild ){
                    registerUser(Email, Password, Fname, Lname, Phone);

                }


            }
        }
    }



            /// Register the user
            private void registerUser(String email, String password, String fName, String lName, String phone) {
                Log.d(TAG, "registerUser: Registering user...");


            /// call the sign up method of the authentication service
            authenticationService.signUp(email, password, new AuthenticationService.AuthCallback() {

                @Override
                public void onCompleted(String uid) {
                    Log.d(TAG, "onCompleted: User registered successfully");
                    /// create a new user object
                    User user = new User();
                    user.setId(uid);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setFname(fName);
                    user.setLname(lName);
                    user.setPhone(phone);

                    /// call the createNewUser method of the database service
                    databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {

                        @Override
                        public void onCompleted(Void object) {
                            Log.d(TAG, "onCompleted: User registered successfully");
                            /// save the user to shared preferences
                            SharedPreferencesUtil.saveUser(Register.this, user);
                            Log.d(TAG, "onCompleted: Redirecting to MainActivity");
                            /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                            Intent mainIntent = new Intent(Register.this, MainActivity.class);
                            /// clear the back stack (clear history) and start the MainActivity
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Log.e(TAG, "onFailed: Failed to register user", e);
                            /// show error message to user
                            Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                            /// sign out the user if failed to register
                            /// this is to prevent the user from being logged in again
                            authenticationService.signOut();
                        }
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e(TAG, "onFailed: Failed to register user", e);
                    /// show error message to user
                    Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                }
            });


        }

    }




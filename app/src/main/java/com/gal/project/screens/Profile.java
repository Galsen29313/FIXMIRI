package com.gal.project.screens;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gal.project.R;
import com.gal.project.models.User;
import com.gal.project.utils.SharedPreferencesUtil;

public class Profile extends AppCompatActivity {

    TextView tvFname, tvLname, tvPhone, tvEmail;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // קבלת פרטי המשתמש מה-SharedPreferences
        user = SharedPreferencesUtil.getUser(this);

        // אתחול TextViews
        tvFname = findViewById(R.id.tvFname);
        tvLname = findViewById(R.id.tvLname);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        // הצגת הפרטים במסכים המתאימים
        if (user != null) {
            // הצגת שם המשתמש לצד כל פריט
            tvFname.setText("שם פרטי: " + user.getFname());
            tvLname.setText("שם משפחה: " + user.getLname());
            tvPhone.setText("טלפון: " + user.getPhone());
            tvEmail.setText("אימייל: " + user.getEmail());
        }
    }
}

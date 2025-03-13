package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gal.project.R;

public class After extends AppCompatActivity implements View.OnClickListener {


    ImageButton ibprofile,ibaddEvent,ibsearchEvent,ibinformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_after);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ibaddEvent=findViewById(R.id.ibaddE);
        ibaddEvent.setOnClickListener(this);
        ibsearchEvent=findViewById(R.id.ibsearchE);
        ibsearchEvent.setOnClickListener(this);
        ibinformation=findViewById(R.id.ibinfo);
        ibinformation.setOnClickListener(this);
        ibprofile=findViewById(R.id.ibAccount);
        ibprofile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==ibaddEvent) {
            Intent go = new Intent(getApplicationContext(), AddNewEvent.class);
            startActivity(go);
        }
        if(v==ibsearchEvent) {
            Intent go = new Intent(getApplicationContext(), SearchEventsActivity.class);
            startActivity(go);
        }
        if(v==ibinformation) {
            Intent go = new Intent(getApplicationContext(), Info.class);
            startActivity(go);
        }
        if(v==ibprofile) {


            Intent go = new Intent(getApplicationContext(), Profile.class);
            startActivity(go);
        }
    }
}
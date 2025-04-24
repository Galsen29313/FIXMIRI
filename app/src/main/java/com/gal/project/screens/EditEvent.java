package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gal.project.R;
import com.gal.project.models.Event;
import com.gal.project.services.DatabaseService;

import java.util.ArrayList;

public class EditEvent extends AppCompatActivity {

    EditText etName, etDate, etTime, etAddress, etMaxMembers, etDescription;
    Spinner spType, spCity;
    Button saveEventButton;
    String eventId;

    private DatabaseService databaseService;
    private Event event;
    private String eventName,eventDate,eventTime,eventAddress,eventDescription;


    String cityArr[];
    String typeArr[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        cityArr=getResources().getStringArray(R.array.arrCity);
        typeArr=getResources().getStringArray(R.array.arrTYpe);

        databaseService = DatabaseService.getInstance();

        // אתחול השדות
        etName = findViewById(R.id.etEventNameEdit);
        etDate = findViewById(R.id.etEventDateEdit);
        etTime = findViewById(R.id.etEventTimeEdit);
        etAddress = findViewById(R.id.etEventAddressEdit);
        etMaxMembers = findViewById(R.id.etEventMaxMembersEdit);
        etDescription = findViewById(R.id.etDescEdit);
        spType = findViewById(R.id.spEventTypeEdit);
        spCity = findViewById(R.id.spEventCityEdit);
        saveEventButton = findViewById(R.id.btnSaveEvent);

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        if (event != null) {


            eventName = event.getName().toString();
            eventDate = event.getDate().toString();
            eventTime =  event.getTime().toString();
            eventAddress=event.getStreet().toString();
            eventDescription = event.getDescription().toString();
            int maxMembers = event.getMaxJoin();
            // הצגת הנתונים בשדות המתאימים
            etName.setText(eventName);
            etDate.setText(eventDate);
            etTime.setText(eventTime);
            etAddress.setText(eventAddress);
            etMaxMembers.setText(String.valueOf(maxMembers));
            etDescription.setText(eventDescription);
          cityArr[]

            spCity.setSelection();

            // חיבור Spinner לסוג אירוע וערים, כאן כדאי להתאים את הערכים שנשלחו ל-Spinner
            // לדוגמה, הצגת סוג האירוע בהתאם לנתונים הקיימים

            saveEventButton.setOnClickListener(view -> saveUpdatedEvent());
        }
    }



    private void saveUpdatedEvent() {
        // קבלת הערכים המעודכנים
        String updatedName = etName.getText().toString();
        String updatedDate = etDate.getText().toString();
        String updatedTime = etTime.getText().toString();
        String updatedAddress = etAddress.getText().toString();
        int updatedMaxMembers = Integer.parseInt(etMaxMembers.getText().toString());
        String updatedDescription = etDescription.getText().toString();

        // יצירת האירוע החדש עם הנתונים המעודכנים
        Event updatedEvent = new Event(eventId, null, updatedName, spType.getSelectedItem().toString(),
                updatedDate, updatedTime, spCity.getSelectedItem().toString(), updatedAddress,
                0.0, 0.0, updatedMaxMembers, new ArrayList<>(), updatedDescription, "active");

        // שמירה למסד הנתונים
        databaseService.updateEvent(updatedEvent, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(EditEvent.this, "האירוע עודכן בהצלחה!", Toast.LENGTH_LONG).show();
                finish();  // סיום הפעולה וחזרה למסך הקודם
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditEvent.this, "הייתה שגיאה בעדכון האירוע", Toast.LENGTH_LONG).show();
            }
        });
    }
}

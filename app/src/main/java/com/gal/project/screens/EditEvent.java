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
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;

import java.util.ArrayList;

public class EditEvent extends AppCompatActivity {

    EditText etName, etDate, etTime, etAddress, etMaxMembers, etDescription;
    Spinner spType, spCity;
    Button saveEventButton;
    String eventId;

    private DatabaseService databaseService;
    private Event event;
    private String eventName,eventDate,eventTime,eventAddress,eventDescription, eventCity,eventType;


    String cityArr[];
    String typeArr[];
    private String uid;

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
          cityArr =getResources().getStringArray(R.array.arrCity);
            int saveI=-1;
            for(int i=1;i<=32;i++)
                if(event.getCity().equals(cityArr[i]))
                    saveI=i;

            spCity.setSelection(saveI);

            typeArr =getResources().getStringArray(R.array.arrTYpe);
           saveI=-1;
            for(int i=1;i<=27;i++)
                if(event.getType().equals(typeArr[i]))
                    saveI=i;

            spType.setSelection(saveI);




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
        String updatedCity=spCity.getSelectedItem().toString();
        String updatedType=spType.getSelectedItem().toString();


        // יצירת האירוע החדש עם הנתונים המעודכנים
        Event updatedEvent = new Event(eventId, null, updatedName, updatedType,
                updatedDate, updatedTime, updatedCity, updatedAddress,
                0.0, 0.0, updatedMaxMembers, new ArrayList<>(), updatedDescription, "active");

        event.setCity(updatedCity);
        event.setType(updatedType);
        ///להמשיך

        // שמירה למסד הנתונים
        databaseService.updateEvent(event, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(EditEvent.this, "האירוע עודכן בהצלחה!", Toast.LENGTH_LONG).show();
                 // סיום הפעולה וחזרה למסך הקודם

                Intent goAbo=new Intent(getApplicationContext(), UserEvents.class);
                startActivity(goAbo);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditEvent.this, "הייתה שגיאה בעדכון האירוע", Toast.LENGTH_LONG).show();
            }
        });



//

    }
}

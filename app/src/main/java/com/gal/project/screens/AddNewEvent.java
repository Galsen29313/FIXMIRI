package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gal.project.R;
import com.gal.project.models.Event;
import com.gal.project.models.User;
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;
import com.gal.project.utils.SharedPreferencesUtil;

import java.util.ArrayList;

public class AddNewEvent extends AppCompatActivity implements View.OnClickListener {

    EditText etName,etDate,etTime,etAddress,etMaxMembers,etDescription;
    String name,date,time, address,members,descroption;
    Spinner spType,spCity;
    Button createEvent;
    Boolean isVaild = true;

    private AuthenticationService authenticationService;
    private DatabaseService databaseService;
    private com.gal.project.models.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_event);
/// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


        initViews();

        String uid = authenticationService.getCurrentUserId();


        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<com.gal.project.models.User>() {
            @Override
            public void onCompleted(com.gal.project.models.User u) {
                user = u;
                Log.d("TAG", "onCompleted: User data retrieved successfully");


            }

            @Override
            public void onFailed(Exception e) {

            }

        });
    }
    private void initViews() {
        etName = findViewById(R.id.etEventName);
        etDate = findViewById(R.id.etEventDate);
        etTime = findViewById(R.id.etEventTime);
        etAddress = findViewById(R.id.etEventAddress);
        etMaxMembers = findViewById(R.id.etEventMaxMembers);
        etDescription = findViewById(R.id.etDesc);
        createEvent = findViewById(R.id.btnAddNewEvent);
        createEvent.setOnClickListener(this);

        spType = findViewById(R.id.spEventType);
        spType.setAdapter(spType.getAdapter());

        spCity = findViewById(R.id.spEventCity);
        spCity.setAdapter(spCity.getAdapter());


    }
    @Override
    public void onClick(View v) {
        if (v == createEvent) {
            name = etName.getText().toString();
            date = etDate.getText().toString();
            time = etTime.getText().toString();
            address = etAddress.getText().toString();
            members = etMaxMembers.getText().toString();
            descroption = etDescription.getText().toString();



            if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !address.isEmpty() && !members.isEmpty()) {
                etName.setText("");
                etDate.setText("");
                etTime.setText("");
                etAddress.setText("");
                etMaxMembers.setText("");

                if (name.length() < 2) {
                    Toast.makeText(AddNewEvent.this, "שם אירוע קצר מדי", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (date.length() < 2) {
                    Toast.makeText(AddNewEvent.this, "תאריך לא תקין", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (!time.contains(":")) {
                    Toast.makeText(AddNewEvent.this, "מספר הטלפון לא תקין", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }
                if (address.contains("@")) {
                    Toast.makeText(AddNewEvent.this, "כתובת לא תקינה", Toast.LENGTH_LONG).show();
                    isVaild = false;
                }

                if (isVaild) {

                    /// generate a new id for the food
                    String id = databaseService.generateEventId();
                    ArrayList<User>joinUsers=new ArrayList<>();
                    joinUsers.add(new User (user));

                    // יצירת אירוע חדש
                    Event newEvent = new Event(
                            id, // מזהה ייחודי
                            user, // המשתמש המנהל
                            name, // שם האירוע
                            spType.getSelectedItem().toString(), // סוג האירוע
                            date, // תאריך
                            time, // זמן
                            spCity.getSelectedItem().toString(), // עיר
                            address, // כתובת
                            0.0, // latitude (תוכל להוסיף קבלת ערך אם יש לך מיקום גיאוגרפי)
                            0.0, // longitude
                            Integer.parseInt(members), // מקסימום משתתפים
                            joinUsers, // רשימת המשתתפים, יכול להיות ריקה בהתחלה
                            descroption, // תיאור
                            "active" // סטטוס, לדוגמה "active"
                    );

                    // שמירה במסד הנתונים
                    databaseService.createNewEvent  (newEvent, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void result) {
                            Toast.makeText(AddNewEvent.this, "האירוע נוצר בהצלחה!", Toast.LENGTH_LONG).show();
                            // חזור למסך הראשי או למקום אחר
                            // או להתחיל Activity חדש אם צריך
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(AddNewEvent.this, "הייתה שגיאה ביצירת האירוע", Toast.LENGTH_LONG).show();
                        }
                    });

                    databaseService.setEventForUsers(newEvent, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });
                }

                // לאחר יצירת האירוע, נוכל להעביר למסך עריכת אירוע עם הפרטים
                Intent intent = new Intent(AddNewEvent.this, EditEvent.class);
                intent.putExtra("eventName", name);
                intent.putExtra("eventDate", date);
                intent.putExtra("eventTime", time);
                intent.putExtra("eventAddress", address);
                intent.putExtra("maxMembers", Integer.parseInt(members));
                startActivity(intent);






            }
        }
    }

}
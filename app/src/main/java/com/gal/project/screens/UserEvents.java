package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gal.project.R;
import com.gal.project.adapters.EventAdapter;
import com.gal.project.adapters.EventUserAdapter;
import com.gal.project.models.Event;
import com.gal.project.models.User;
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class UserEvents extends AppCompatActivity {
       ImageButton backButton;
    private RecyclerView recyclerView;
    private EventUserAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseService databaseService;
    private ProgressBar progressBar;

    // Filter criteria
    private Spinner spinnerSportType, spinnerCity;
    private EditText editTextDate;
    private Button btnSearch;
    private String selectedSportType="", selectedCity="", enteredDate;
    private User user;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton=findViewById(R.id.reButton);
        databaseService=DatabaseService.getInstance();

         uid= AuthenticationService.getInstance().getCurrentUserId();


        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<com.gal.project.models.User>() {
                    @Override
                    public void onCompleted(com.gal.project.models.User u) {
                        user = new User(u);
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });

        recyclerView = findViewById(R.id.rcUserEvents);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventUserAdapter(eventList,UserEvents.this,uid);
        recyclerView.setAdapter(eventAdapter);





        databaseService.getUserEvents(uid,  new DatabaseService.DatabaseCallback<List<Event>>() {
            @Override
            public void onCompleted(List<Event> events) {

                eventList.addAll(events);

               eventAdapter.notifyDataSetChanged();



//





                    }






            @Override
            public void onFailed(Exception e) {

                Toast.makeText(UserEvents.this, "שגיאה בטעינת האירועים", Toast.LENGTH_SHORT).show();
                Log.e("SearchEventsActivity", "Error fetching events", e);
            }
        });



    }









    public void joinEvent(Event event) {

        String uid= AuthenticationService.getInstance().getCurrentUserId();


        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<com.gal.project.models.User>() {
            @Override
            public void onCompleted(com.gal.project.models.User u) {
                user = new User(u);


                Log.d("TAG", "onCompleted: User data retrieved successfully");
                /// save the user data to shared preferences
                int numStatus = event.addParticipant(user);

                if (numStatus == 1) {

                    databaseService.updateEvent(event, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            eventAdapter.notifyDataSetChanged();
                            Toast.makeText(UserEvents.this, "הצטרפת בהצלחה", Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });

                } else {
                    if (numStatus == 2) {

                        Toast.makeText(UserEvents.this, "הנך רשום לאירוע", Toast.LENGTH_LONG).show();
                    } else if (numStatus == 3) {

                        Toast.makeText(UserEvents.this, "לא ניתן להצטרף ", Toast.LENGTH_LONG).show();

                    }


                }
            }

            @Override
            public void onFailed(Exception e) {

            }




        });
    }


   public void  eventExit(Event event){




       event.delParticipant(uid);





                   databaseService.updateEvent(event, new DatabaseService.DatabaseCallback<Void>() {
                       @Override
                       public void onCompleted(Void object) {




                           eventAdapter.notifyDataSetChanged();


                       }

                       @Override
                       public void onFailed(Exception e) {

                       }
                   });

       databaseService.delEventForOneUser(event.getId(),uid, new DatabaseService.DatabaseCallback<Void>() {
           @Override
           public void onCompleted(Void object) {
               Intent goEdit=new Intent(getApplicationContext(), After.class);


               startActivity(goEdit);

           }

           @Override
           public void onFailed(Exception e) {

           }



       });







               }

               public  void goToEditEvent(Event event){



                   Intent goEdit=new Intent(getApplicationContext(), EditEvent.class);

                   goEdit.putExtra("event", event);
                   startActivity(goEdit);

               }

    public void back(View view) {
        Intent intent=new Intent(this,After.class);
        startActivity(intent);
    }

    }


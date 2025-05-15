package com.gal.project.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gal.project.R;
import com.gal.project.adapters.EventAdapter;
import com.gal.project.models.Event;
import com.gal.project.models.User;
import com.gal.project.services.AuthenticationService;
import com.gal.project.services.DatabaseService;
import com.gal.project.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseService databaseService;
    private ProgressBar progressBar;

    // Filter criteria
    private Spinner spinnerSportType, spinnerCity;
    private EditText editTextDate;
    private Button btnSearch;
    private String selectedSportType="", selectedCity="", enteredDate;
    private User user;
    ImageButton backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_events);



        recyclerView = findViewById(R.id.rcEvents);

        spinnerSportType = findViewById(R.id.spinnerSportType);
        spinnerCity = findViewById(R.id.spinnerCity);
        editTextDate = findViewById(R.id.editTextDate);
        btnSearch = findViewById(R.id.btnSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList,SearchEventsActivity.this);
        recyclerView.setAdapter(eventAdapter);

        databaseService = DatabaseService.getInstance();

        // Get the entered date from the EditText
        // Setup Spinners (Dropdowns)
        setupSpinners();

        databaseService.getEvents(new DatabaseService.DatabaseCallback<List<Event>>() {
            @Override
            public void onCompleted(List<Event> events) {

                eventList.addAll(events);





//




                eventAdapter = new EventAdapter(eventList,SearchEventsActivity.this);
                recyclerView.setAdapter(eventAdapter);


                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        enteredDate = editTextDate.getText().toString();
                        List<Event> filteredEvents = new ArrayList<>();
                        for (Event event : eventList) {
                            //     Apply your filters here
                                   if(( event.getType().contains(selectedSportType))&& ( event.getCity().contains(selectedCity))&&(event.getDate().contains(enteredDate)))
                                       filteredEvents.add(event);
                               }

//


                                  EventAdapter adaptFilter=new EventAdapter(filteredEvents,SearchEventsActivity.this);
                                recyclerView.setAdapter(adaptFilter);
//




                    }
                });



            }

            @Override
            public void onFailed(Exception e) {

                Toast.makeText(SearchEventsActivity.this, "שגיאה בטעינת האירועים", Toast.LENGTH_SHORT).show();
                Log.e("SearchEventsActivity", "Error fetching events", e);
            }
        });

        backbutton=findViewById(R.id.reButton);

    }

    private void setupSpinners() {
        // Sport Type Spinner
        ArrayAdapter<CharSequence> sportAdapter = ArrayAdapter.createFromResource(this,
                R.array.arrTYpe, android.R.layout.simple_spinner_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSportType.setAdapter(sportAdapter);
        spinnerSportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(position>0) {
                    selectedSportType = parentView.getItemAtPosition(position).toString();
                }
                else   selectedSportType="";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedSportType="";

            }
        });

        // City Spinner
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.arrCity, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position>0) {
                    selectedCity = parentView.getItemAtPosition(position).toString();
                }
                else selectedCity="";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCity="";
            }
        });






    }


    private void searchEvents() {



        // Example: Search events based on the selected filters
        databaseService.getEvents(new DatabaseService.DatabaseCallback<List<Event>>() {
            @Override
            public void onCompleted(List<Event> events) {
                progressBar.setVisibility(ProgressBar.GONE);




                List<Event> filteredEvents = new ArrayList<>();
                for (Event event : events) {

                    //     Apply your filters here

                   if(event.getType().equals(selectedSportType)&& event.getCity().equals(selectedCity))
                        filteredEvents.add(event);
                    }

//


               // if(filteredEvents.size()>0)
             //   {
                    EventAdapter adaptFilter=new EventAdapter(filteredEvents,SearchEventsActivity.this);
                    recyclerView.setAdapter(adaptFilter);

             //   }
              //  else {

              //      eventList.clear();
              //      eventList.addAll(events);
              //      eventAdapter.notifyDataSetChanged();
              //  }
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(SearchEventsActivity.this, "שגיאה בטעינת האירועים", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SearchEventsActivity.this, "הצטרפת בהצלחה", Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });



                    databaseService.setEventForOneUser(event, uid, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    });

                } else {
                    if (numStatus == 2) {

                        Toast.makeText(SearchEventsActivity.this, "הנך רשום לאירוע", Toast.LENGTH_LONG).show();
                    } else if (numStatus == 3) {

                        Toast.makeText(SearchEventsActivity.this, "לא ניתן להצטרף ", Toast.LENGTH_LONG).show();

                    }


                }
            }

            @Override
            public void onFailed(Exception e) {

            }




    });
}
    public void back(View view) {
        Intent intent=new Intent(this,After.class);
        startActivity(intent);
    }
}

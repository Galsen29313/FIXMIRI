package com.gal.project.services;

import android.util.Log;

import androidx.annotation.Nullable;


import com.gal.project.models.Event;
import com.gal.project.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    /// @see Log
    private static final String TAG = "DatabaseService";

    public void createEvent(Event newEvent, DatabaseCallback<Void> callback) {
        // במקרה ואין ID לאירוע, ניצור אחד
        String eventId = newEvent.getId();
        if (eventId == null || eventId.isEmpty()) {
            eventId = generateEventId();  // צור מזהה חדש אם לא סופק
            newEvent.setId(eventId);  // הגדר את המזהה החדש לאירוע
        }

        // שמור את האירוע ב-Firebase תחת הנתיב "events/{eventId}"
        writeData("events/" + eventId, newEvent, callback);
    }


    /// callback interface for database operations
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        void onCompleted(T object);

        /// called when the operation fails with an exception
        void onFailed(Exception e);
    }

    /// the instance of this class
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // private generic methods to write and read data from the database

    /// write data to the database at a specific path
    /// @param path the path to write the data to
    /// @param data the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        Log.d(TAG, "Writing data to path: " + path);  // לוג לפני שמירת הנתונים
        databaseReference.child(path).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data written successfully to path: " + path);  // לוג אם השמירה הצליחה
                if (callback != null) {
                    callback.onCompleted(task.getResult());
                }
            } else {
                Log.e(TAG, "Failed to write data to path: " + path, task.getException());  // לוג במקרה של שגיאה
                if (callback != null) {
                    callback.onFailed(task.getException());
                }
            }
        });
    }


    /// read data from the database at a specific path
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    /// @param path the path to get the data from
    /// @param clazz the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @return void
    /// @see DatabaseCallback
    /// @see Class
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// generate a new id for a new object in the database
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    // end of private methods for reading and writing data

    // public methods to interact with the database

    /// create a new user in the database
    /// @param user the user object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User

    public void createNewUser(@NotNull final com.gal.project.models.User user, @Nullable final DatabaseCallback<Void> callback) {
        writeData("Users/" + user.getId(), user, callback);
    }

    /// create a new event in the database
    /// @param event the event object to create
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive void
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see Event
    public void createNewEvent(@NotNull final com.gal.project.models.Event event, @Nullable final DatabaseCallback<Void> callback) {
        writeData("events/" + event.getId(), event, callback);
    }


    /// get a user from the database
    /// @param uid the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the user object
    ///             if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see User
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<com.gal.project.models.User> callback) {
        getData("Users/" + uid, com.gal.project.models.User.class, callback);
    }



    private void deleteData(@NotNull final String path, final @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(path).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback == null) return;
                callback.onCompleted(task.getResult());
            } else {
                if (callback == null) return;
                callback.onFailed(task.getException());
            }
        });
    }


    public void updateEvent(@NotNull final com.gal.project.models.Event event, @Nullable final DatabaseCallback<Void> callback) {
        writeData("events/" + event.getId(), event, callback);
    }


    public void setEventForUsers(@NotNull final Event Event, @Nullable final DatabaseCallback<Void> callback) {
        for (int i = 0; i < Event.getJoined().size(); i++) {
            User user = Event.getJoined().get(i);

            writeData("UserEvent/" + user.getId()+"/"+ Event.getId(), Event, callback);
        }
    }


    public void deleteEventForUser(@NotNull final Event event, String  uid, @Nullable final DatabaseCallback<Void> callback)

    {
        for (int i = 0; i < event.getJoined().size(); i++) {
            User user = event.getJoined().get(i);
            if (user.getId().equals(uid))

                event.getJoined().remove(i);

        }
        writeData("Events/" + event.getId(), event, callback);

        deleteData("UserEvent/" + uid + "/" + event.getId(), callback);
    }


    public void getUserEvents(@NotNull final String uid, final DatabaseCallback<List<Event>> callback) {


        readData("UserEvent/" + uid ).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<Event> Events = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                Event Event = dataSnapshot.getValue(Event.class);
                Log.d(TAG, "Got Event: " + Event);
                Events.add(Event);
            });

            callback.onCompleted(Events);
        });


    }






    /// get a event from the database
    /// @param eventId the id of the event to get
    /// @param callback the callback to call when the operation is completed
    ///               the callback will receive the event object
    ///              if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see Event
    public void getEvent(@NotNull final String eventId, @NotNull final DatabaseCallback<com.gal.project.models.Event> callback) {
        getData("events/" + eventId, com.gal.project.models.Event.class, callback);
    }


    /// generate a new id for a new event in the database
    /// @return a new id for the event
    /// @see #generateNewId(String)
    /// @see Event
    public String generateEventId() {
        return generateNewId("events");
    }

    /// get all the events from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of event objects
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see List
    /// @see Event
    /// @see #getData(String, Class, DatabaseCallback)
    public void getEvents(@NotNull final DatabaseCallback<List<com.gal.project.models.Event>> callback) {
        readData("events").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<com.gal.project.models.Event> events = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                com.gal.project.models.Event event = dataSnapshot.getValue(com.gal.project.models.Event.class);
                Log.d(TAG, "Got event: " + event);
                events.add(event);
            });

            callback.onCompleted(events);
        });
    }
    public void getEventsByCategory(@NotNull final String category, @NotNull final DatabaseCallback<List<Event>> callback) {
        Query query = databaseReference.child("events").orderByChild("category").equalTo(category);

        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting events by category", task.getException());
                callback.onFailed(task.getException());
                return;
            }

            List<Event> events = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    events.add(event);
                }
            });

            callback.onCompleted(events);
        });
    }


    /// get all the users from the database
    /// @param callback the callback to call when the operation is completed
    ///              the callback will receive a list of event objects
    ///            if the operation fails, the callback will receive an exception
    /// @return void
    /// @see DatabaseCallback
    /// @see List
    /// @see Event
    /// @see #getData(String, Class, DatabaseCallback)
    public void getUsers(@NotNull final DatabaseCallback<List<com.gal.project.models.User>> callback) {
        readData("Users").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<com.gal.project.models.User> users = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                com.gal.project.models.User user = dataSnapshot.getValue(com.gal.project.models.User.class);
                Log.d(TAG, "Got user: " + user);
                users.add(user);
            });

            callback.onCompleted(users);
        });
    }

}

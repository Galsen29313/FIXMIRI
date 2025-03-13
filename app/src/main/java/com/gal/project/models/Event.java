package com.gal.project.models;

import android.widget.Toast;

import com.gal.project.models.User;

import java.util.ArrayList;
import java.util.List;

public class Event {


    protected String id;
    protected User userAdmin;
    protected String name, type, date, time, city, street;

    double lat, lang;
    protected int maxJoin;
    protected List<User> joined;
    protected String description;

    protected String status;

    public Event(String id, User userAdmin, String name, String type, String date, String time, String city, String street, double lat, double lang, int maxJoin, List<User> joined, String description, String status) {
        this.id = id;
        this.userAdmin = userAdmin;
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
        this.city = city;
        this.street = street;
        this.lat = lat;
        this.lang = lang;
        this.maxJoin = maxJoin;
        this.joined = joined;
        this.description = description;
        this.status = status;
    }

    public Event() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(User userAdmin) {
        this.userAdmin = userAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public int getMaxJoin() {
        return maxJoin;
    }

    public void setMaxJoin(int maxJoin) {
        this.maxJoin = maxJoin;
    }

    public List<User> getJoined() {
        return joined;
    }

    public void setJoined(List<User> joined) {
        this.joined = joined;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // בתוך מחלקת Event
    public int addParticipant(User user) {

        if (this.joined == null)
            this.joined = new ArrayList<>();
        if (joined.size() >= maxJoin)
            return 3;

        boolean found = false;

        for (int i = 0; i < this.joined.size(); i++) {

            if (this.joined.get(i).getId().equals(user.getId())) {

                found = true;

            }
        }

        if (found)
            return 2;


        if (!found && joined.size() < maxJoin) {


            joined.add(user); // הוסף את המשתמש לרשימה
            return 1;
        }

        return 0;

    }



    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", userAdmin=" + userAdmin +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", lat=" + lat +
                ", lang=" + lang +
                ", maxJoin=" + maxJoin +
                ", joined=" + joined +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

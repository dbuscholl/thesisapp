package com.example.davidbuscholl.veranstalter.Entities;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 22.11.2016.
 */
public class Meeting {
    private int eventId;
    private int id;
    private String driverUsername;
    private String driverFullname;
    private String datestart;
    private String dateend;
    private ArrayList<Refuse> refuses = new ArrayList<>();

    public Meeting() {}

    public Meeting(int eventId, int id, String driverUsername, String driverFullname, String datestart, String dateend) {
        this.eventId = eventId;
        this.id = id;
        this.driverUsername = driverUsername;
        this.driverFullname = driverFullname;
        this.datestart = datestart;
        this.dateend = dateend;
    }

    public String getDriverUsername() {
        return driverUsername;
    }

    public void setDriverUsername(String driverUsername) {
        this.driverUsername = driverUsername;
    }

    public String getDriverFullname() {
        return driverFullname;
    }

    public void setDriverFullname(String driverFullname) {
        this.driverFullname = driverFullname;
    }

    public String getDatestart() {
        return datestart;
    }

    public void setDatestart(String datestart) {
        this.datestart = datestart;
    }

    public String getDateend() {
        return dateend;
    }

    public void setDateend(String dateend) {
        this.dateend = dateend;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ArrayList<Refuse> getRefuses() {
        return refuses;
    }

    public void setRefuses(ArrayList<Refuse> refuses) {
        this.refuses = refuses;
    }
}

package com.example.davidbuscholl.veranstalter.Entities;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 22.11.2016.
 */

public class EventDetail {
    private String title;
    private String location;
    private ArrayList<Participant> participants = new ArrayList<>();
    private ArrayList<Meeting> meetings = new ArrayList<>();

    public EventDetail() {}

    public EventDetail(String title, String location, ArrayList<Participant> participants, ArrayList<Meeting> meetings) {
        this.title = title;
        this.location = location;
        this.participants = participants;
        this.meetings = meetings;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
    }
}

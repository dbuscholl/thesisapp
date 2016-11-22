package com.example.davidbuscholl.veranstalter.Entities;

/**
 * Created by David Buscholl on 22.11.2016.
 */

public class Refuse {
    private int participant;
    private int meeting;

    public Refuse(){}

    public Refuse(int participant, int meeting) {
        this.participant = participant;
        this.meeting = meeting;
    }

    public int getParticipant() {
        return participant;
    }

    public void setParticipant(int participant) {
        this.participant = participant;
    }

    public int getMeeting() {
        return meeting;
    }

    public void setMeeting(int meeting) {
        this.meeting = meeting;
    }
}

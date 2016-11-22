package com.example.davidbuscholl.veranstalter.Entities;

/**
 * Created by David Buscholl on 22.11.2016.
 */

public class Participant {
    private int id;
    private String username;
    private String vorname;
    private String nachname;
    private String adresse;

    public Participant() {}

    public Participant(int id, String username, String vorname, String nachname, String adresse) {
        this.id = id;
        this.username = username;
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}

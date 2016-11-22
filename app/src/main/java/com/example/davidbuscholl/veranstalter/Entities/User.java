package com.example.davidbuscholl.veranstalter.Entities;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 14.11.2016.
 */
public class User {
    private int id;
    private String username;
    private String vorname;
    private String nachname;
    private String email;
    private String adresse;
    private String joined;
    private static ArrayList<Integer> roles = new ArrayList<>();
    public static ArrayList<Integer> roles() {
        return roles;
    }

    public User() {}

    public User(int id, String username, String vorname, String nachname, String email, String adresse, String joined) {
        this.id = id;
        this.username = username;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.adresse = adresse;
        this.joined = joined;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public static ArrayList<Integer> getRoles() {
        return roles;
    }

    public static void setRoles(ArrayList<Integer> roles) {
        User.roles = roles;
    }
}

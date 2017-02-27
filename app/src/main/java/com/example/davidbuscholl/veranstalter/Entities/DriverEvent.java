package com.example.davidbuscholl.veranstalter.Entities;

import java.sql.Driver;
import java.util.ArrayList;

/**
 * Created by David Buscholl on 23.02.2017.
 *
 * Entity Class containing event information specially for Drivers
 * Containing an ArrayList where Results of a request are stored.
 * Delegated some methods from the ArrayList
 */

public class DriverEvent {
    private static ArrayList<DriverEvent> eventlist = new ArrayList<>();

    private int id;
    private int angebotId;
    private String datumStart;
    private String datumEnde;
    private String angebotName;
    private int stationen;
    private String duration;

    public DriverEvent(int id, int angebotId, String datumStart, String datumEnde, String angebotName, int stationen, String duration) {
        this.id = id;
        this.angebotId = angebotId;
        this.datumStart = datumStart;
        this.datumEnde = datumEnde;
        this.angebotName = angebotName;
        this.stationen = stationen;
        this.duration = duration;
    }

    public DriverEvent(){

    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAngebotId() {
        return angebotId;
    }

    public void setAngebotId(int angebotId) {
        this.angebotId = angebotId;
    }

    public String getDatumStart() {
        return datumStart;
    }

    public void setDatumStart(String datumStart) {
        this.datumStart = datumStart;
    }

    public String getDatumEnde() {
        return datumEnde;
    }

    public void setDatumEnde(String datumEnde) {
        this.datumEnde = datumEnde;
    }

    public String getAngebotName() {
        return angebotName;
    }

    public void setAngebotName(String angebotName) {
        this.angebotName = angebotName;
    }

    public int getStationen() {
        return stationen;
    }

    public void setStationen(int stationen) {
        this.stationen = stationen;
    }

    public static int size() {
        return eventlist.size();
    }

    public static int indexOf(Object o) {
        return eventlist.indexOf(o);
    }

    public static boolean add(DriverEvent driverEvent) {
        return eventlist.add(driverEvent);
    }

    public static DriverEvent remove(int index) {
        return eventlist.remove(index);
    }

    public static DriverEvent get(int index) {
        return eventlist.get(index);
    }

    public static void clear() {
        eventlist.clear();
    }
}

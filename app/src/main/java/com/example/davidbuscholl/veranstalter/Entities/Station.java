package com.example.davidbuscholl.veranstalter.Entities;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 23.02.2017.
 */

public class Station {
    private static ArrayList<Station> eventlist = new ArrayList<>();

    private String time;
    private String address;
    private String name;

    public Station(String time, String address, String name) {
        this.time = time;
        this.address = address;
        this.name = name;
    }

    public Station() {

    }

    public static ArrayList<Station> getEventlist() {
        return eventlist;
    }

    public static void setEventlist(ArrayList<Station> eventlist) {
        Station.eventlist = eventlist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Station get(int index) {
        return eventlist.get(index);
    }

    public static void clear() {
        eventlist.clear();
    }

    public static boolean add(Station station) {
        return eventlist.add(station);
    }

    public static int size() {
        return eventlist.size();
    }
}

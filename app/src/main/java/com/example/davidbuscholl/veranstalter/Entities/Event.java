package com.example.davidbuscholl.veranstalter.Entities;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 22.11.2016.
 */

public class Event {
    private static ArrayList<Event> eventlist = new ArrayList<>();

    private String name;
    private String location;

    private int id;

    private int participants;
    public Event(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public static void clear() {
        eventlist.clear();
    }

    public static boolean add(Event event) {
        return eventlist.add(event);
    }

    public static Event get(int index) {
        return eventlist.get(index);
    }

    public static int indexOf(Object o) {
        return eventlist.indexOf(o);
    }

    public static int size() {
        return eventlist.size();
    }

    public static boolean isEmpty() {
        return eventlist.isEmpty();
    }

    public static boolean contains(Object o) {
        return eventlist.contains(o);
    }

    public static Event remove(int index) {
        return eventlist.remove(index);
    }

    public static boolean remove(Object o) {
        return eventlist.remove(o);
    }

    public static ArrayList<Event> getEventlist() {
        return eventlist;
    }

    public static void setEventlist(ArrayList<Event> eventlist) {
        Event.eventlist = eventlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

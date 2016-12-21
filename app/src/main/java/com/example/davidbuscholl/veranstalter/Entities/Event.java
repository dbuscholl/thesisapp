package com.example.davidbuscholl.veranstalter.Entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by David Buscholl on 22.11.2016.
 */

public class Event {
    private static ArrayList<Event> eventlist = new ArrayList<>();

    private String name;
    private String location;
    private EventDetail detail;

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

    public EventDetail getDetail() {
        return detail;
    }

    public void setDetail(EventDetail detail) {
        this.detail = detail;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public void detailFromJsonObject(JSONObject ob) {
        EventDetail eventDetail = new EventDetail();
        try {
            eventDetail.setId(Integer.parseInt(ob.getJSONObject("data").getString("id")));
            eventDetail.setTitle(ob.getJSONObject("data").getString("name"));
            eventDetail.setLocation(ob.getJSONObject("data").getString("adresse"));

            ArrayList<Participant> part = new ArrayList<>();
            JSONArray jsonpart = ob.getJSONArray("participants");
            for (int j = 0; j < jsonpart.length(); j++) {
                JSONObject p = jsonpart.getJSONObject(j);
                part.add(new Participant(Integer.parseInt(p.getString("id")), p.getString("username"), p.getString("vorname"), p.getString("nachname"), p.getString("adresse")));
            }
            eventDetail.setParticipants(part);

            ArrayList<Refuse> refuses = new ArrayList<>();
            JSONArray jsonref = ob.getJSONArray("refuses");
            for (int j = 0; j < jsonref.length(); j++) {
                JSONObject r = jsonref.getJSONObject(j);
                refuses.add(new Refuse(Integer.parseInt(r.getString("personenId")), Integer.parseInt(r.getString("treffenId"))));
            }

            ArrayList<Meeting> meetings = new ArrayList<>();
            JSONArray jsonmeet = ob.getJSONArray("meetings");
            for (int j = 0; j < jsonmeet.length(); j++) {
                JSONObject m = jsonmeet.getJSONObject(j);
                Meeting meeting = new Meeting(Integer.parseInt(m.getString("angebotId")), Integer.parseInt(m.getString("id")), m.getString("datumStart"), m.getString("datumEnde"));
                for (Refuse r : refuses) {
                    if (r.getMeeting() == meeting.getId()) {
                        meeting.getRefuses().add(r);
                    }
                }
                meetings.add(meeting);
            }
            eventDetail.setMeetings(meetings);
            setDetail(eventDetail);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

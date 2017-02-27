package com.example.davidbuscholl.veranstalter.GUI.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by David Buscholl on 21.11.2016.
 * the adapter holding the structure for the organizers events listview
 */
public class EventListAdapter extends BaseAdapter{

    private final JSONArray events;
    private final Context context;

    /**
     * creates the event objects from the jsonarray
     * @param context inherited from parent
     * @param events the json array from which the events should be created
     */
    public EventListAdapter(Context context, JSONArray events) {
        this.events = events;
        this.context = context;
        Event.clear();
        try {
            for(int i = 0; i < events.length(); i++) {
                Event.add(new Event(Integer.parseInt(events.getJSONObject(i).getString("id")),events.getJSONObject(i).getString("name"),events.getJSONObject(i).getString("adresse")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getCount() {
        return events.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        View row = inf.inflate(R.layout.list_addresses, parent, false);

        try {
            TextView title = (TextView) row.findViewById(R.id.eventlistTitle);
            title.setText(events.getJSONObject(position).getString("name"));
            TextView location = (TextView) row.findViewById(R.id.eventListAddress);
            location.setText(events.getJSONObject(position).getString("adresse"));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return row;
    }
}

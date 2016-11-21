package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;

/**
 * Created by David Buscholl on 21.11.2016.
 */
public class EventListAdapter extends BaseAdapter{

    private final JSONArray events;
    private final Context context;

    public EventListAdapter(Context context, JSONArray events) {
        this.events = events;
        this.context = context;
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
            location.setText(events.getJSONObject(position).getString("location"));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return row;
    }
}

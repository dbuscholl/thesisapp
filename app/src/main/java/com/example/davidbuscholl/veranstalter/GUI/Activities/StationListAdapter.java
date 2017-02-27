package com.example.davidbuscholl.veranstalter.GUI.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.Station;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David Buscholl on 23.02.2017.
 */

/**
 * This is the adapter holding the structure for the stations listview on the driver detail acitivity containing times and addresses of the route
 */
public class StationListAdapter extends BaseAdapter {

    private final JSONArray events;
    private final Context context;

    /**
     * creates the station objects from the given jsonarray
     * @param context the context of which it should be executed
     * @param events the jsonarray containing the server response with the stations
     */
    public StationListAdapter(Context context, JSONArray events) {
        this.events = events;
        this.context = context;
        Station.clear();
        try {
            for(int i = 0; i < events.length(); i++) {
                Station.add(new Station(events.getJSONObject(i).getString("time"),events.getJSONObject(i).getString("address"),events.getJSONObject(i).getString("name"),Float.parseFloat(events.getJSONObject(i).getString("latitude")),Float.parseFloat(events.getJSONObject(i).getString("longitude"))));
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
        View row = inf.inflate(R.layout.list_stations, parent, false);

        TextView time = (TextView) row.findViewById(R.id.adetStoptime);
        TextView address = (TextView) row.findViewById(R.id.adetAddress);
        TextView person = (TextView) row.findViewById(R.id.adetPerson);
        Station s = Station.get(position);
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = df.parse(s.getTime());
            df = new SimpleDateFormat("HH:mm");
            time.setText(df.format(start) + " Uhr");
            address.setText(s.getAddress());
            person.setText(s.getName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return row;
    }
}

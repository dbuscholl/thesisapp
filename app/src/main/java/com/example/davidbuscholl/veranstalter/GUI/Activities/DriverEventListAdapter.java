package com.example.davidbuscholl.veranstalter.GUI.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.Entities.DriverEvent;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David Buscholl on 23.02.2017.
 * the adapter holding the datastructure for the drivers meetings listview
 */
public class DriverEventListAdapter extends BaseAdapter {

    private final JSONArray events;
    private final Context context;

    /**
     * creating driverevent objects from the jsonarray
     * @param context inherited from parent
     * @param events the json array of which the objects should be created
     */
    public DriverEventListAdapter(Context context, JSONArray events) {
        this.events = events;
        this.context = context;
        DriverEvent.clear();
        try {
            for(int i = 0; i < events.length(); i++) {
                DriverEvent.add(new DriverEvent(Integer.parseInt(events.getJSONObject(i).getString("id")),Integer.parseInt(events.getJSONObject(i).getString("angebotId")),events.getJSONObject(i).getString("datumStart"),events.getJSONObject(i).getString("datumEnde"),events.getJSONObject(i).getString("angebotName"),Integer.parseInt(events.getJSONObject(i).getString("stationen")),events.getJSONObject(i).getString("durationMinutes")));
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

    /**
     * filling the content for each listitem
     * @param position inherited from parent
     * @param convertView inherited from parent
     * @param parent inherited from parent
     * @return inherited from parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        View row = inf.inflate(R.layout.list_addresses_driver, parent, false);

        TextView name = (TextView) row.findViewById(R.id.ddetName);
        TextView date = (TextView) row.findViewById(R.id.ddetDate);
        TextView time = (TextView) row.findViewById(R.id.ddetTime);
        TextView stations = (TextView) row.findViewById(R.id.ddetStations);
        TextView duration = (TextView) row.findViewById(R.id.ddetDuration);
        DriverEvent event = DriverEvent.get(position);
        try {
            name.setText(event.getAngebotName());
            stations.setText(String.valueOf(event.getStationen()));
            duration.setText(event.getDuration());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = df.parse(event.getDatumStart());
            Date end = df.parse(event.getDatumEnde());

            df = new SimpleDateFormat("dd. MM. yyyy");
            date.setText(df.format(start));

            df = new SimpleDateFormat("HH:mm");
            time.setText(df.format(start) + " - " + df.format(end));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return row;
    }
}
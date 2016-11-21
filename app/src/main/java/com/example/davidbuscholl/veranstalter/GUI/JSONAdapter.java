package com.example.davidbuscholl.veranstalter.GUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by David Buscholl on 08.11.2016.
 */
public class JSONAdapter extends BaseAdapter {
    private final JSONArray meetings;
    private final Context context;

    public JSONAdapter(Context context, JSONArray meetings)  {
        this.context = context;
        this.meetings = meetings;
    }

    @Override
    public int getCount() {
        return meetings.length();
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
        View row = inf.inflate(R.layout.meetings_list, parent, false);

        try {
            TextView date = (TextView) row.findViewById(R.id.meetingRowDate);
            date.setText(meetings.getJSONObject(position).getString("date"));
            TextView time = (TextView) row.findViewById(R.id.meetingRowTime);
            time.setText(meetings.getJSONObject(position).getString("time"));
            TextView location = (TextView) row.findViewById(R.id.meetingRowLocation);
            location.setText(meetings.getJSONObject(position).getString("location"));
            TextView participants = (TextView) row.findViewById(R.id.meetingRowParticipants);
            participants.setText(meetings.getJSONObject(position).getString("participants"));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return row;
    }
}

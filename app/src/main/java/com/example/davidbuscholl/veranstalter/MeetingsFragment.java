package com.example.davidbuscholl.veranstalter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by David Buscholl on 08.11.2016.
 */

public class MeetingsFragment extends Fragment {
    public View rootView;

    public MeetingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.meetings_fragment, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.meetingsListView);
        try {
            JSONArray meetings = new JSONArray("[{\"time\":\"12:00 - 14:00\",\"date\":\"02.02.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":42},{\"time\":\"12:00 - 14:00\",\"date\":\"09.02.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":40},{\"time\":\"12:00 - 14:00\",\"date\":\"16.02.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":42},{\"time\":\"12:00 - 14:00\",\"date\":\"23.02.2016\",\"location\":\"Unterallmendstraße 10, Furtwangen im Schwarzwald\",\"participants\":30},{\"time\":\"14:00 - 16:00\",\"date\":\"02.03.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":41},{\"time\":\"12:00 - 16:00\",\"date\":\"09.03.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":42},{\"time\":\"14:00 - 16:00\",\"date\":\"02.03.2016\",\"location\":\"Bahnhofstraße 3, St. Georgen\",\"participants\":38}]");
            ListAdapter myAdapter = new JSONAdapter(getContext(),meetings);
            lv.setAdapter(myAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }
}

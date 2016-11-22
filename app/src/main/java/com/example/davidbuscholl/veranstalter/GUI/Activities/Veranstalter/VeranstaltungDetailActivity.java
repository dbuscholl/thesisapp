package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.EventDetail;
import com.example.davidbuscholl.veranstalter.Entities.Meeting;
import com.example.davidbuscholl.veranstalter.Entities.Participant;
import com.example.davidbuscholl.veranstalter.Entities.Refuse;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VeranstaltungDetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    private static ProgressDialog progress;
    private static Event event;
    private static EventDetail eventDetail;
    private static VeranstaltungDetailActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstalterdetail);

        context = this;
        Intent i = getIntent();
        int position = i.getIntExtra("event", -1);
        if (position == -1 || position >= Event.size()) {
            ServerErrorDialog.show(VeranstaltungDetailActivity.this, "Fehlerhafter Aufruf");
            finish();
            return;
        }
        event = Event.get(position);

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        loadEventExtras(context);


/*
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/user/3/meetings";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(this.toString(),response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
*/
    }

    public static void loadEventExtras(final VeranstaltungDetailActivity context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/events/" + event.getId() + "/details";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                Log.d(this.toString(), response);
                JSONObject ob = null;
                try {
                    ob = new JSONObject(response);
                    if (ob.has("success")) {
                        if (ob.getBoolean("success")) {
                            createEventDetail(ob);

                            Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
                            context.setSupportActionBar(toolbar);

                            mViewPager = (ViewPager) context.findViewById(R.id.container);

                            mSectionsPagerAdapter = new SectionsPagerAdapter(context.getSupportFragmentManager());
                            mViewPager.setAdapter(mSectionsPagerAdapter);

                            TabLayout tabLayout = (TabLayout) context.findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(mViewPager);

                            FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        } else {
                            ServerErrorDialog.show(context);
                        }
                    } else {
                        ServerErrorDialog.show(context);
                        ((Activity) context).finish();
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(context);
                    e.printStackTrace();
                    ((Activity) context).finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    private static void createEventDetail(JSONObject ob) {
        eventDetail = new EventDetail();
        try {
            eventDetail.setTitle(ob.getJSONObject("data").getString("name"));
            eventDetail.setLocation(ob.getJSONObject("data").getString("adresse"));

            ArrayList<Participant> part = new ArrayList<>();
            JSONArray jsonpart = ob.getJSONArray("participants");
            for (int i = 0; i < jsonpart.length(); i++) {
                JSONObject p = jsonpart.getJSONObject(i);
                part.add(new Participant(Integer.parseInt(p.getString("id")), p.getString("username"), p.getString("vorname"), p.getString("nachname"), p.getString("adresse")));
            }
            eventDetail.setParticipants(part);

            ArrayList<Refuse> refuses = new ArrayList<>();
            JSONArray jsonref = ob.getJSONArray("refuses");
            for (int i = 0; i < jsonref.length(); i++) {
                JSONObject r = jsonref.getJSONObject(i);
                refuses.add(new Refuse(Integer.parseInt(r.getString("personenId")), Integer.parseInt(r.getString("treffenId"))));
            }

            ArrayList<Meeting> meetings = new ArrayList<>();
            JSONArray jsonmeet = ob.getJSONArray("meetings");
            for (int i = 0; i < jsonmeet.length(); i++) {
                JSONObject m = jsonmeet.getJSONObject(i);
                Meeting meeting = new Meeting(Integer.parseInt(m.getString("angebotId")), Integer.parseInt(m.getString("id")), m.getString("datumStart"), m.getString("datumEnde"));
                for (Refuse r : refuses) {
                    if (r.getMeeting() == meeting.getId()) {
                        meeting.getRefuses().add(r);
                    }
                }
                meetings.add(meeting);
            }
            eventDetail.setMeetings(meetings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;
        VeranstaltungDetailBesucherFragment vdbf;
        VeranstaltungDetailFragment vdf;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    vdf = new VeranstaltungDetailFragment();
                    return vdf;
                case 1:
                    vdbf = new VeranstaltungDetailBesucherFragment();
                    return vdbf;
            }
            return null;
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Besucher";
            }
            return null;
        }
    }


    public static class VeranstaltungDetailFragment extends Fragment {
        public View rootView;
        public JSONArray jsonParticipants = null;
        private ViewGroup container;
        private LayoutInflater inflater;

        public VeranstaltungDetailFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.inflater = inflater;
            this.container = container;
            if (eventDetail != null) {
                return loadFragment();
            }
            return new LinearLayout(getContext());
        }

        private View loadFragment() {
            rootView = inflater.inflate(R.layout.fragment_veranstaltung_detail, container, false);
            TextView title = (TextView) rootView.findViewById(R.id.vaDetTitle);
            TextView location = (TextView) rootView.findViewById(R.id.vaDetLocation);
            TextView participants = (TextView) rootView.findViewById(R.id.vaDetTotal);
            ListView meetings = (ListView) rootView.findViewById(R.id.vaDetList);

            title.setText(event.getName());
            location.setText(event.getLocation());
            participants.setText(String.valueOf(eventDetail.getParticipants().size()));
            MeetingsAdapter ma = new MeetingsAdapter(getContext());
            meetings.setAdapter(ma);

            return rootView;
        }

        private static class MeetingsAdapter extends BaseAdapter {
            private Context context;

            public MeetingsAdapter(Context context) {
                this.context = context;
            }

            @Override
            public int getCount() {
                return eventDetail.getMeetings().size();
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
                View row = inf.inflate(R.layout.list_meetings, parent, false);

                TextView date = (TextView) row.findViewById(R.id.vaDetDate);
                TextView time = (TextView) row.findViewById(R.id.vaDetTime);
                TextView yes = (TextView) row.findViewById(R.id.vaDetYes);
                TextView no = (TextView) row.findViewById(R.id.vaDetNo);

                Meeting m = eventDetail.getMeetings().get(position);
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date start = df.parse(m.getDatestart());
                    Date end = df.parse(m.getDateend());

                    df = new SimpleDateFormat("dd. MM. yyyy");
                    date.setText(df.format(start));

                    df = new SimpleDateFormat("HH:mm");
                    time.setText(df.format(start) + " - " + df.format(end));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                no.setText(String.valueOf(m.getRefuses().size()));
                yes.setText(String.valueOf(eventDetail.getParticipants().size() - m.getRefuses().size()));
                return row;
            }
        }
    }

    public static class VeranstaltungDetailBesucherFragment extends Fragment {
        public View rootView;
        private ViewGroup container;
        private LayoutInflater inflater;

        public VeranstaltungDetailBesucherFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.inflater = inflater;
            this.container = container;
            if (eventDetail != null) {
                return loadFragment();
            }
            return new LinearLayout(getContext());
        }

        private View loadFragment() {
            rootView = inflater.inflate(R.layout.fragment_veranstaltung_besucher, container, false);
            ListView besucher = (ListView) rootView.findViewById(R.id.vaDetBesuchers);
            ParticipantsAdapter pa = new ParticipantsAdapter(getContext());
            besucher.setAdapter(pa);
            return rootView;
        }

        private static class ParticipantsAdapter extends BaseAdapter {
            private Context context;

            public ParticipantsAdapter(Context context) {
                this.context = context;
            }

            @Override
            public int getCount() {
                return eventDetail.getParticipants().size();
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
                View row = inf.inflate(R.layout.list_participants, parent, false);

                TextView fullname = (TextView) row.findViewById(R.id.vaDetFullName);
                TextView username = (TextView) row.findViewById(R.id.vaDetUsername);

                Participant p = eventDetail.getParticipants().get(position);
                fullname.setText(p.getVorname() + " " + p.getNachname());
                username.setText(p.getUsername());

                return row;
            }
        }
    }
}

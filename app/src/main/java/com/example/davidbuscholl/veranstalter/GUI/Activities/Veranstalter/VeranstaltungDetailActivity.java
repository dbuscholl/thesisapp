package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity holding the detail view in two tabs by the viewpager
 */
public class VeranstaltungDetailActivity extends AppCompatActivity {

    private static SectionsPagerAdapter mSectionsPagerAdapter;

    private static ViewPager mViewPager;
    private static ProgressDialog progress;
    private static Event event;
    private static EventDetail eventDetail;
    public static VeranstaltungDetailActivity context;
    private static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstalterdetail);

        context = this;

        // get event item from the position which gets obtained from the intent
        Intent i = getIntent();
        position = i.getIntExtra("event", -1);
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
        progress.show();

        loadEventExtras(context);

    }

    /**
     * sending a request to the server asking for the detailed information about an event such as
     * list of meetings and participants
     * @param context inherited from parent
     */
    public static void loadEventExtras(final VeranstaltungDetailActivity context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/events/" + event.getId() + "/details";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                Log.i(this.toString(), response);
                JSONObject ob = null;
                try {
                    ob = new JSONObject(response);
                    if (ob.has("success")) {
                        if (ob.getBoolean("success")) {
                            event.detailFromJsonObject(ob);
                            eventDetail = event.getDetail();

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
                                    Intent i = new Intent(context,VeranstaltungTreffenActivity.class);
                                    i.putExtra("event",eventDetail.getId());
                                    context.startActivity(i);
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

    /**
     * Method closing the activity and reopening it (workaround because the viewpager is not dynamic...)
     */
    public static void reload() {
        Intent i = new Intent(context,VeranstaltungDetailActivity.class);
        i.putExtra("event",position);
        context.startActivity(i);
        context.finish();
    }

    /*
        ----------------------  SECTION PAGER ADAPTER --------------------------------------------------
        tells which fragment has to be opened at which position / tab
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

        /**
         * takes care of the titles displayed on th etabs
         * @param position inherited from parent
         * @return inherited from parent
         */
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

    /*
    ----------------------  VERANSTALTUNG DETAIL FRAGMENT ------------------------------------------
    Showing the details for an event such as Meetings list
     */

    public static class VeranstaltungDetailFragment extends Fragment {
        public View rootView;
        public JSONArray jsonParticipants = null;
        private ViewGroup container;
        private LayoutInflater inflater;
        private ListView meetings;
        private MeetingsAdapter meetingsAdapter;

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

        //getting all viws and filling them with information about the event
        private View loadFragment() {
            rootView = inflater.inflate(R.layout.fragment_veranstaltung_detail, container, false);
            TextView title = (TextView) rootView.findViewById(R.id.vaDetTitle);
            TextView location = (TextView) rootView.findViewById(R.id.vaDetLocation);
            TextView participants = (TextView) rootView.findViewById(R.id.vaDetTotal);
            TextView id = (TextView) rootView.findViewById(R.id.vaDetId);
            final ImageView connect = (ImageView) rootView.findViewById(R.id.vaDetConnect);
            meetings = (ListView) rootView.findViewById(R.id.vaDetList);
            registerForContextMenu(meetings);

            title.setText(event.getName());
            location.setText(event.getLocation());
            participants.setText(String.valueOf(eventDetail.getParticipants().size()));
            id.setText(String.valueOf(event.getId()));
            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Identifikationsnummer für TeilnehmerActivity zum abonnieren.", Toast.LENGTH_LONG).show();
                }
            };
            connect.setOnClickListener(ocl);
            id.setOnClickListener(ocl);
            meetingsAdapter = new MeetingsAdapter(getContext());
            meetings.setAdapter(meetingsAdapter);

            return rootView;
        }


        /**
         * Context menug which opens on long tap on a meeting to delete it
         * @param menu inherited from parent
         * @param v inherited from parent
         * @param menuInfo inherited from parent
         */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add("Löschen");
        }

        /**
         * invoking the deletion process
         * @param item inherited from parent
         * @return inherited from parent
         */
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int listPosition = info.position;

            progress.show();
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://37.221.196.48/thesis/public/meetings/delete";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress.dismiss();
                    Log.i(this.toString(),response);
                    JSONObject ob = null;
                    try {
                        ob = new JSONObject(response);
                        if(ob.has("success")) {
                            if (ob.getBoolean("success")) {
                                Toast.makeText(context,"Treffen gelöscht!",Toast.LENGTH_SHORT).show();
                                reload();
                            } else {
                                ServerErrorDialog.show(context);
                            }
                            progress.dismiss();
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
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", Token.get(context));
                    map.put("id", String.valueOf(eventDetail.getMeetings().get(listPosition).getId()));
                    return map;
                }
            };
            queue.add(stringRequest);
            return true;
        }

        /*
                ----------------------  MEETINGS ADAPTER ---------------------------------------------------
                As all meetings are displayed in a list it also needs an adapter of which the class is described
                here
                */
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
                TextView driver = (TextView) row.findViewById(R.id.vaDetDriver);

                Meeting m = eventDetail.getMeetings().get(position);
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date start = df.parse(m.getDatestart());
                    Date end = df.parse(m.getDateend());

                    df = new SimpleDateFormat("dd. MM. yyyy");
                    date.setText(df.format(start));

                    df = new SimpleDateFormat("HH:mm");
                    time.setText(df.format(start) + " - " + df.format(end));

                    driver.setText(m.getDriverFullname() + " (" + m.getDriverUsername() + ")");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                no.setText(String.valueOf(m.getRefuses().size()));
                yes.setText(String.valueOf(eventDetail.getParticipants().size() - m.getRefuses().size()));
                return row;
            }
        }
    }

    /*
    ----------------------  VERANSTALTUNG BESUCHER FRAGMENT ----------------------------------------
    this fragment holds the content for the participants list. the organizer can see all people taking
    part on the event
    */

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

        /*
        ----------------------  PARTICIPANTS ADAPTER -----------------------------------------------
        as this the participants list is hold inside a listview it also needs an adapter of which the
        class is described below
        */
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

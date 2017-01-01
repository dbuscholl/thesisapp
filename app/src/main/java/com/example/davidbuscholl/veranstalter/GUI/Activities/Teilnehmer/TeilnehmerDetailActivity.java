package com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.example.davidbuscholl.veranstalter.Entities.Refuse;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.LoginRegisterActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.MainActivity;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstaltungDetailActivity.context;

public class TeilnehmerDetailActivity extends AppCompatActivity {
    private static Context context;
    private static Context applicationContext;
    private static ProgressDialog progress;
    private static Event event;
    private static int position;

    private TextView title;
    private TextView location;
    private TextView total;
    private ListView list;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        applicationContext = getApplicationContext();
        progress = progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        Intent i = getIntent();
        position = i.getIntExtra("event", -1);
        if (position == -1 || position >= Event.size()) {
            ServerErrorDialog.show(applicationContext, "Fehlerhafter Aufruf");
            ((Activity) context).finish();
            return;
        }
        event = Event.get(position);

        loadEventExtras();
    }

    private void loadEventExtras() {

        progress.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/events/" + event.getId() + "/details";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setContentView(R.layout.activity_teilnehmer_detail);
                setTitle("Detailansicht");

                title = (TextView) findViewById(R.id.teDetTitle);
                location = (TextView) findViewById(R.id.teDetLocation);
                total = (TextView) findViewById(R.id.teDetTotal);
                list = (ListView) findViewById(R.id.teDetList);

                progress.dismiss();
                Log.i(this.toString(), response);
                JSONObject ob = null;
                try {
                    ob = new JSONObject(response);
                    if (ob.has("success")) {
                        if (ob.getBoolean("success")) {
                            event.detailFromJsonObject(ob);
                            EventDetail detail = event.getDetail();
                            title.setText(detail.getTitle());
                            location.setText(detail.getLocation());
                            total.setText(String.valueOf(detail.getParticipants().size()));
                            MeetingsListAdapter mla = new MeetingsListAdapter(context, detail);
                            list.setAdapter(mla);
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

    private static class MeetingsListAdapter extends BaseAdapter {
        private Context context;
        private EventDetail detail;

        public MeetingsListAdapter(Context context, EventDetail detail) {
            this.context = context;
            this.detail = detail;
        }

        @Override
        public int getCount() {
            return detail.getMeetings().size();
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
            View row = inf.inflate(R.layout.list_meetings_teilnehmer, parent, false);

            TextView date = (TextView) row.findViewById(R.id.teDetDate);
            TextView time = (TextView) row.findViewById(R.id.teDetTime);
            ImageView yesimg = (ImageView) row.findViewById(R.id.teDetYesImage);
            TextView yes = (TextView) row.findViewById(R.id.teDetYes);
            ImageView noimg = (ImageView) row.findViewById(R.id.teDetNoImage);
            TextView no = (TextView) row.findViewById(R.id.teDetNo);
            LinearLayout yeslayout = (LinearLayout) row.findViewById(R.id.teDetYesLayout);
            LinearLayout nolayout = (LinearLayout) row.findViewById(R.id.teDetNoLayout);

            Meeting m = detail.getMeetings().get(position);
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
            yes.setText(String.valueOf(detail.getParticipants().size() - m.getRefuses().size()));

            boolean refuse = false;
            for (Refuse r : m.getRefuses()) {
                if (r.getParticipant() == User.getCurrent().getId()) {
                    noimg.setColorFilter(Color.argb(255, 255, 0, 0));
                    yesimg.setColorFilter(Color.argb(255, 0, 0, 0));
                    refuse = true;
                }
            }
            if (!refuse) {
                yesimg.setColorFilter(Color.argb(255, 0, 255, 0));
            }

            yeslayout.setOnClickListener(new YesClickListener(row,detail,m.getId()));
            nolayout.setOnClickListener(new NoClickListener(row,detail,m.getId()));
            return row;
        }

        public static class YesClickListener implements View.OnClickListener {
            private View row;
            private EventDetail detail;
            private int meeting;

            public YesClickListener(View row, EventDetail detail, int id) {
                this.row = row;
                this.detail = detail;
                meeting = id;
            }

            @Override
            public void onClick(View v) {
                final ImageView yesimg = (ImageView) row.findViewById(R.id.teDetYesImage);
                final TextView yes = (TextView) row.findViewById(R.id.teDetYes);
                final ImageView noimg = (ImageView) row.findViewById(R.id.teDetNoImage);
                final TextView no = (TextView) row.findViewById(R.id.teDetNo);

                progress.show();
                RequestQueue queue = Volley.newRequestQueue(TeilnehmerDetailActivity.context);
                String url = "http://37.221.196.48/thesis/public/user/participating/accept/" + meeting + "?token=" + Token.get(TeilnehmerDetailActivity.context);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(this.toString(), response);
                        progress.dismiss();
                        JSONObject ob = null;
                        try {
                            ob = new JSONObject(response);
                            if (ob.has("success")) {
                                if (ob.getBoolean("success")) {
                                    yesimg.setColorFilter(Color.argb(255, 0, 255, 0));
                                    noimg.setColorFilter(Color.argb(255, 0, 0, 0));
                                    no.setText(String.valueOf(ob.getInt("refuses")));
                                    yes.setText(String.valueOf(detail.getParticipants().size() - ob.getInt("refuses")));
                                } else {
                                    ServerErrorDialog.show(TeilnehmerDetailActivity.context, ob.getString("error"));
                                }
                            } else {
                                ServerErrorDialog.show(TeilnehmerDetailActivity.context, ob.getString("error"));
                            }
                        } catch (Exception e) {
                            ServerErrorDialog.show(TeilnehmerDetailActivity.context);
                            e.printStackTrace();
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
        }

        public static class NoClickListener implements View.OnClickListener {
            private View row;
            private EventDetail detail;
            private int meeting;

            public NoClickListener(View row, EventDetail detail, int id) {
                this.row = row;
                this.detail = detail;
                meeting = id;
            }

            @Override
            public void onClick(View v) {
                final ImageView yesimg = (ImageView) row.findViewById(R.id.teDetYesImage);
                final TextView yes = (TextView) row.findViewById(R.id.teDetYes);
                final ImageView noimg = (ImageView) row.findViewById(R.id.teDetNoImage);
                final TextView no = (TextView) row.findViewById(R.id.teDetNo);

                progress.show();
                RequestQueue queue = Volley.newRequestQueue(TeilnehmerDetailActivity.context);
                String url = "http://37.221.196.48/thesis/public/user/participating/refuse/" + meeting + "?token=" + Token.get(TeilnehmerDetailActivity.context);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(this.toString(), response);
                        progress.dismiss();
                        JSONObject ob = null;
                        try {
                            ob = new JSONObject(response);
                            if (ob.has("success")) {
                                if (ob.getBoolean("success")) {
                                    yesimg.setColorFilter(Color.argb(255, 0, 0, 0));
                                    noimg.setColorFilter(Color.argb(255, 255, 0, 0));
                                    no.setText(String.valueOf(String.valueOf(ob.getInt("refuses"))));
                                    yes.setText(String.valueOf(detail.getParticipants().size() - ob.getInt("refuses")));
                                } else {
                                    ServerErrorDialog.show(TeilnehmerDetailActivity.context,ob.getString("error"));
                                }
                            } else {
                                ServerErrorDialog.show(TeilnehmerDetailActivity.context,ob.getString("error"));
                            }
                        } catch (Exception e) {
                            ServerErrorDialog.show(TeilnehmerDetailActivity.context);
                            e.printStackTrace();
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
        }
    }
}

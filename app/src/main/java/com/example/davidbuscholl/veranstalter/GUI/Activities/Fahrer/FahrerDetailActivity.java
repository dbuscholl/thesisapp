package com.example.davidbuscholl.veranstalter.GUI.Activities.Fahrer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.DriverEvent;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.Station;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.DriverEventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.StationListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstaltungDetailActivity;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FahrerDetailActivity extends AppCompatActivity {
    private static Context context;
    private ProgressDialog progress;
    private int position;
    private DriverEvent de;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahrer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        Intent i = getIntent();
        position = i.getIntExtra("event", -1);
        if (position == -1 || position >= DriverEvent.size()) {
            ServerErrorDialog.show(context, "Fehlerhafter Aufruf");
            finish();
            return;
        }
        de = DriverEvent.get(position);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Station.size() < 2)) {
                    String uri = "https://www.google.com/maps?";
                    for (int i = 0; i < Station.size(); i++) {
                        if (i == 0) {
                            uri += "saddr=" + String.valueOf(Station.get(i).getLatitude()) + "," + String.valueOf(Station.get(i).getLongitude());
                        }
                        if (i == 1) {
                            uri += "&daddr=" + String.valueOf(Station.get(i).getLatitude()) + "," + String.valueOf(Station.get(i).getLongitude());
                        }
                        if (i > 1) {
                            uri += "+to:" + String.valueOf(Station.get(i).getLatitude()) + "," + String.valueOf(Station.get(i).getLongitude());
                        }
                    }
                    Log.d("TAG",uri);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);

        TextView name = (TextView) findViewById(R.id.adetName);
        TextView date = (TextView) findViewById(R.id.adetDate);
        TextView time = (TextView) findViewById(R.id.adetTime);
        TextView stations = (TextView) findViewById(R.id.adetStations);
        TextView duration = (TextView) findViewById(R.id.adetDuration);
        list = (ListView) findViewById(R.id.adetDetails);
        try {
            name.setText(de.getAngebotName());
            stations.setText(String.valueOf(de.getStationen()));
            duration.setText(de.getDuration());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = df.parse(de.getDatumStart());
            Date end = df.parse(de.getDatumEnde());

            df = new SimpleDateFormat("dd. MM. yyyy");
            date.setText(df.format(start));

            df = new SimpleDateFormat("HH:mm");
            time.setText(df.format(start) + " - " + df.format(end));
        } catch (Exception e) {
            e.printStackTrace();
        }

        load();
    }

    private void load() {
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/user/driving/" + String.valueOf(de.getId()) + "?token=" + Token.get(this);

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
                            StationListAdapter sla = new StationListAdapter(context, ob.getJSONArray("data"));
                            list.setAdapter(sla);
                        } else {
                            if (ob.has("error") && ob.getString("error").equals("Treffen hat keine Route!")) {
                                new AlertDialog.Builder(context).setTitle("Routenproblem!").setMessage("Die Route hat sich geändert bzw. es ist noch keine vorhanden. \nWillst du jetzt eine Route berechnen lassen? Das dauert vielleicht ein bisschen...").setPositiveButton("Ja!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        makeRouteRequest();
                                    }
                                }).setNegativeButton("Nein!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                            }
                        }
                    } else {
                        ServerErrorDialog.show(context);
                        finish();
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(context);
                    e.printStackTrace();
                    finish();
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

    private void makeRouteRequest() {
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/driver/route/" + String.valueOf(de.getId()) + "?token=" + Token.get(this);

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
                            load();
                        } else {
                            ServerErrorDialog.show(context, ob.getString("error"));
                        }
                    } else {
                        ServerErrorDialog.show(context);
                        finish();
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(context);
                    e.printStackTrace();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(45000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}


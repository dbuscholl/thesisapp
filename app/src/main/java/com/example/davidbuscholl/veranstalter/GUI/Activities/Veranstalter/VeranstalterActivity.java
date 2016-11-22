package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.LoginRegisterActivity;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VeranstalterActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private SharedPreferences prefs;
    private ListView eventlist;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstalter);
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences("de.dbuscholl.veranstalter", Context.MODE_PRIVATE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VeranstalterActivity.this,VeranstaltungHinzufuegenActivity.class));
            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        eventlist = (ListView) findViewById(R.id.eventsListView);
        load(VeranstalterActivity.this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(User.roles().indexOf(2)==-1) {
            menu.findItem(R.id.action_divers).setVisible(false);
        }
        if(User.roles().indexOf(3)==-1) {
            menu.findItem(R.id.action_participants).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            progress.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://37.221.196.48/thesis/public/user/logout";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(this.toString(),response);
                    JSONObject ob = null;
                    try {
                        ob = new JSONObject(response);
                        if(ob.has("success")) {
                            if (ob.getBoolean("success")) {
                                startActivity(new Intent(getApplicationContext(), LoginRegisterActivity.class));
                                finish();
                            } else {
                                ServerErrorDialog.show(VeranstalterActivity.this);
                            }
                            progress.dismiss();
                        } else {
                            ServerErrorDialog.show(VeranstalterActivity.this);
                            finish();
                        }
                    } catch (Exception e) {
                        ServerErrorDialog.show(VeranstalterActivity.this);
                        e.printStackTrace();
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("token",prefs.getString("token","0"));
                    return map;
                }
            };
            queue.add(stringRequest);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void load(final Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/user/events?token=" + Token.get(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                Log.d(this.toString(),response);
                JSONObject ob = null;
                try {
                    ob = new JSONObject(response);
                    if(ob.has("success")) {
                        if (ob.getBoolean("success")) {
                            JSONArray events = ob.getJSONArray("events");
                            EventListAdapter ela = new EventListAdapter(context, events);
                            ListView eventlist = (ListView) ((Activity) context).findViewById(R.id.eventsListView);
                            eventlist.setAdapter(ela);
                            eventlist.setOnItemClickListener(new ClickListener());
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
        });
        queue.add(stringRequest);
    }

    public static Context getContext() {
        return context;
    }

    private static class ClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(context,VeranstaltungDetailActivity.class);
            i.putExtra("event",position);
            context.startActivity(i);
        }
    }
}

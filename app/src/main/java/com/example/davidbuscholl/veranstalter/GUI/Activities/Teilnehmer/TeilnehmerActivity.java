package com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import com.example.davidbuscholl.veranstalter.GUI.Activities.EventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstalterActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstaltungDetailActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstaltungTreffenActivity;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeilnehmerActivity extends AppCompatActivity {
    private static Context context;
    private static Context applicationContext;
    private static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teilnehmer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        final ListView list = (ListView) findViewById(R.id.tePartList);
        context = this;
        applicationContext = getApplicationContext();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/user/participating?token=" + Token.get(this);

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
                            EventListAdapter ela = new EventListAdapter(context, ob.getJSONArray("events"));
                            list.setAdapter(ela);
                            final JSONObject finalOb = ob;
                            list.setOnItemClickListener(new ClickListener());
                        } else {
                            ServerErrorDialog.show(getApplicationContext());
                        }
                    } else {
                        ServerErrorDialog.show(getApplicationContext());
                        finish();
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(getApplicationContext());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teilnehmer, menu);
        if(User.getCurrent().roles().indexOf(1)==-1) {
            menu.findItem(R.id.action_parti_organizer).setVisible(false);
        }
        if(User.getCurrent().roles().indexOf(2)==-1) {
            menu.findItem(R.id.action_parti_divers).setVisible(false);
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
        if (id == R.id.action_parti_logout) {
            User.getCurrent().logout(context);
            return true;
        }

        if(id == R.id.action_parti_organizer) {
            context.startActivity(new Intent(context,VeranstalterActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent i = new Intent(context, TeilnehmerDetailActivity.class);
            i.putExtra("event", position);
            context.startActivity(i);
        }
    }
}

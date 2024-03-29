package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.EventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Fahrer.FahrerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The mian acitivity holding the gui for the organizer from which he can manage all his events-
 */
public class VeranstalterActivity extends AppCompatActivity {
    private static EventListAdapter ela;
    private ProgressDialog progress;
    private SharedPreferences prefs;
    private static ListView eventlist;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstalter);
        setTitle("Eigene Veranstaltungen");
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences("de.dbuscholl.veranstalter", Context.MODE_PRIVATE);
        // open the activity from which the organizer can add new events
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
        registerForContextMenu(eventlist);
        load(VeranstalterActivity.this);

    }


    /**
     * action bar menu. Depending on the roles of the user some items have to be hidden
     * @param menu inherited from parent
     * @return inherited from parent
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_veranstalter, menu);
        if(User.getCurrent().roles().indexOf(2)==-1) {
            menu.findItem(R.id.action_organizer_divers).setVisible(false);
        }
        if(User.getCurrent().roles().indexOf(3)==-1) {
            menu.findItem(R.id.action_organizer_participants).setVisible(false);
        }
        return true;
    }

    /**
     * Function to be callend, when an item of the menu is being clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // logout was clicked
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_organizer_logout) {
            User.getCurrent().logout(context);
            return true;
        }

        // "switch to participants view" was clicked
        if(id == R.id.action_organizer_participants) {
            context.startActivity(new Intent(context,TeilnehmerActivity.class));
            finish();
        }

        // "switch to drivers view" was clicked
        if(id == R.id.action_organizer_divers) {
            context.startActivity(new Intent(context,FahrerActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * firing a request to the server where the data of the response is filled into the listview by its adapter
     * @param context
     */
    public static void load(final Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/events?token=" + Token.get(context);

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
                            ela = new EventListAdapter(context, events);
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

    /**
     * deletes the selected item
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listPosition = info.position;

        progress.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://37.221.196.48/thesis/public/events/delete";

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
                            Toast.makeText(VeranstalterActivity.this,"Löschen erfolgreich",Toast.LENGTH_SHORT).show();
                            load(VeranstalterActivity.this);
                        } else {
                            if(ob.has("error")) ServerErrorDialog.show(context,ob.getString("error"));
                        }
                        progress.dismiss();
                    } else {
                        Toast.makeText(VeranstalterActivity.this,"Unerwarteter Fehler",Toast.LENGTH_SHORT).show();
                        ((Activity) context).finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(VeranstalterActivity.this,"Unerwarteter Fehler",Toast.LENGTH_SHORT).show();
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
                map.put("id", String.valueOf(Event.get(listPosition).getId()));
                return map;
            }
        };
        queue.add(stringRequest);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("Löschen");
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

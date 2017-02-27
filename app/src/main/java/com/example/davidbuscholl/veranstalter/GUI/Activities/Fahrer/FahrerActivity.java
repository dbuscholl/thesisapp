package com.example.davidbuscholl.veranstalter.GUI.Activities.Fahrer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.DriverEvent;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.DriverEventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.EventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstalterActivity;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

/**
 * This is the Driver Activity containing main options for the driver such as adding Tracks to drive
 * and choose a meeting to get more information about.
 */
public class FahrerActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static Context context;
    private String m_Text;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahrer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (ListView) findViewById(R.id.driEventList);

        context = this;
        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);

        load();
        //make longtap-menu pop out
        registerForContextMenu(list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Fires a request where a driver can be registered for an event as standarddriver
             * @param view inherited from parent
             */
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Veranstaltungsnummer eingeben...");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if (m_Text.trim().length() == 0) {
                            ServerErrorDialog.show(context, "Keinen Text eingegeben.");
                            return;
                        }
                        progress.show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "http://37.221.196.48/thesis/public/events/" + m_Text.trim() + "/registerDriver?token=" + Token.get(context);

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
                                    }
                                } catch (Exception e) {
                                    ServerErrorDialog.show(context);
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
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    /**
     * Loading all meetings by firing a request where the driver is driving for. If positive result
     * the list will append every meeting.
     */
    public void load() {
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/user/driving?token=" + Token.get(this);

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
                            if(ob.has("data")) {
                                DriverEventListAdapter dla = new DriverEventListAdapter(context, ob.getJSONArray("data"));
                                list.setAdapter(dla);
                                list.setOnItemClickListener(new FahrerActivity.ClickListener());
                                if (User.getCurrent().getAdresse().equals("")) {
                                    TeilnehmerActivity.askAdress(context, "Gib die Adresse ein, von der aus du losfahren wirst:");
                                }
                            } else {
                                list.setAdapter(null);
                            }
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
        queue.add(stringRequest);
    }

    /**
     * opening longtap menu depending on item on which it was executed
     * @param menu inherited from parent
     * @param v inherited from parent
     * @param menuInfo inherited from parent
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.driEventList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_veranstaltungslist, menu);
        }
    }

    /**
     * Actions which should be done after clicking on a menu item of the longtap-menu. As there is only
     * one item, the only action available is to delete a meeting the driver drives for currently. Reloading
     * the list after a positive response was received from the server
     * @param item inherited from parent
     * @return inherited from parent
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.eventlistDelete:
                final int listPosition = info.position;
                int id = DriverEvent.get(listPosition).getId();
                progress.show();

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://37.221.196.48/thesis/public/events/" + id + "/unregisterDriver?token=" + Token.get(this);

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
                                    ServerErrorDialog.show(getApplicationContext(), ob.getString("error"));
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
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Method to be executed when the actionbar menu is being clicked. Depending on the roles some
     * items have to be hidden by the system (switch to organizer view / switch to participants view)
     * @param menu inherited from parent
     * @return inherited from parent
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver, menu);
        //noinspection SimplifiableIfStatement

        if (User.getCurrent().roles().indexOf(1) == -1) {
            menu.findItem(R.id.action_driver_organizer).setVisible(false);
        }
        if (User.getCurrent().roles().indexOf(3) == -1) {
            menu.findItem(R.id.action_driver_parti).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //logout user button
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_driver_logout) {
            User.getCurrent().logout(context);
            return true;
        }

        // button "switch to organizer view"
        if (id == R.id.action_driver_organizer) {
            context.startActivity(new Intent(context, VeranstalterActivity.class));
            finish();
        }

        // button "switch to participants view
        if (id == R.id.action_driver_parti) {
            context.startActivity(new Intent(context, TeilnehmerActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Click Listener for Meetings-ListItems. It opens the FahrerDetailActivity and passing the
     * position of the clicked item to it.
     */
    private static class ClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent i = new Intent(context, FahrerDetailActivity.class);
            i.putExtra("event", position);
            context.startActivity(i);
        }
    }
}

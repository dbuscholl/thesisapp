package com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer;

import android.app.Activity;
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
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstalterActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstaltungHinzufuegenActivity;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.AddressAutoComplete;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.AddressChosenInterface;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the main acitivity where the participant can make actions from.
 */
public class TeilnehmerActivity extends AppCompatActivity {
    private static Context context;
    private static Context applicationContext;
    private static ProgressDialog progress;
    private String m_Text = "";
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teilnehmer);
        setTitle("Angemeldete Veranstaltungen");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        list = (ListView) findViewById(R.id.tePartList);
        registerForContextMenu(list);

        context = this;
        applicationContext = getApplicationContext();

        // with the fab users can add events to their participating list by typing its id into the textfield
        // the app fires a request to the server containing information about the id of the event.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
                        String url = "http://37.221.196.48/thesis/public/events/" + m_Text.trim() + "/register?token=" + Token.get(context);

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
                                            list.setOnItemClickListener(new ClickListener());
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

        load();
    }

    /**
     * the menu appearing when the user longtaps an items
     * @param menu inherited from parent
     * @param v inherited from parent
     * @param menuInfo inherited from parent
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.tePartList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_veranstaltungslist, menu);
        }
    }

    /**
     * As there is only the possibility to delete an event from the participating list, the client sends a
     * request to the server containing the id of the event which should get removed from the list.
     * @param item inherited from parent
     * @return inherited from parent
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.eventlistDelete:
                final int listPosition = info.position;
                int id = Event.get(listPosition).getId();
                progress.show();

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://37.221.196.48/thesis/public/events/" + id + "/unregister?token=" + Token.get(this);

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
     * the actionbar menu. Depending on the users roles some items have to be hidden.
     * @param menu inherited from parent
     * @return inherited from parent
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teilnehmer, menu);
        if (User.getCurrent().roles().indexOf(1) == -1) {
            menu.findItem(R.id.action_parti_organizer).setVisible(false);
        }
        if (User.getCurrent().roles().indexOf(2) == -1) {
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

        // logging out the user
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_parti_logout) {
            User.getCurrent().logout(context);
            return true;
        }

        // if clicked on "switch to organizers view"
        if (id == R.id.action_parti_organizer) {
            context.startActivity(new Intent(context, VeranstalterActivity.class));
            finish();
        }

        // if clicked on "switch to drivers view"
        if (id == R.id.action_parti_divers) {
            context.startActivity(new Intent(context, FahrerActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * function to load the list of participating events for the user by sendind a request to the server
     */
    public void load() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/participating?token=" + Token.get(this);

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
                            list.setOnItemClickListener(new ClickListener());
                            if (User.getCurrent().getAdresse().equals("")) {
                                askAdress(context, "Keine Adresse hinterlegt! Gib sie hier ein:");
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
     * this is being called when the client has to ask the user for its address because it is not yet registered
     * in the servers database. The server adds a seperate object in the json response from which the client knows
     * when to show the dialog.
     * This method basically pops up a dialog where the user can enter some text which is validated by Google Maps Autocomplete
     * and the result of the autocomplete will be send back to the api server which adds it to its database. An interface is
     * used for the response from the google server where the programmer can decide what will happen after that. In this case
     * adding it to the users basic information.
     * @param context inherited from parent
     * @param title inherited from parent
     */
    public static void askAdress(final Context context, String title) {
        final String[] address = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                address[0] = input.getText().toString();
                if (address[0].trim().length() == 0) {
                    ServerErrorDialog.show(context, "Keinen Text eingegeben.");
                    return;
                }

                AddressAutoComplete.show(context, address[0], new AddressChosenInterface() {
                    @Override
                    public void onAddressChosen(final String chosen) {
                        progress.show();

                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "http://37.221.196.48/thesis/public/user/update";

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progress.dismiss();
                                Log.i(this.toString(), response);
                                JSONObject ob = null;
                                try {
                                    ob = new JSONObject(response);
                                    if (ob.has("success")) {
                                        if (ob.getBoolean("success")) {
                                            User.getCurrent().setAdresse(chosen);
                                            ServerErrorDialog.show(context,"Adresse wurde hinterlegt!");
                                        } else {
                                            ServerErrorDialog.show(context, ob.getString("error"));
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
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("adresse",chosen);
                                map.put("token", Token.get(context));
                                return map;
                            }
                        };
                        queue.add(stringRequest);
                    }
                });
            }
        });
        builder.show();
    }

    /**
     * The Click listener for the list view which opens the detail view for an event. It stores
     * the position of the list item into the intent for its activity.
     */
    private static class ClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent i = new Intent(context, TeilnehmerDetailActivity.class);
            i.putExtra("event", position);
            context.startActivity(i);
        }
    }

}

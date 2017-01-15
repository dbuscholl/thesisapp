package com.example.davidbuscholl.veranstalter.GUI.Activities.Fahrer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.Entities.User;
import com.example.davidbuscholl.veranstalter.GUI.Activities.EventListAdapter;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerDetailActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstalterActivity;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

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

        context = this;
        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);

        list = (ListView) findViewById(R.id.driEventList);
        registerForContextMenu(list);

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
                        String url = "http://37.221.196.48/thesis/public/event/" + m_Text.trim() + "/registerDriver?token=" + Token.get(context);

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


    public void load() {
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
                            EventListAdapter ela = new EventListAdapter(context, ob.getJSONArray("events"));
                            list.setAdapter(ela);
                            list.setOnItemClickListener(new FahrerActivity.ClickListener());
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.driEventList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_veranstaltungslist, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.eventlistDelete:
                final int listPosition = info.position;
                int id = Event.get(listPosition).getId();
                progress.show();

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://37.221.196.48/thesis/public/event/" + id + "/unregisterDriver?token=" + Token.get(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver, menu);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_driver_logout) {
            User.getCurrent().logout(context);
            return true;
        }

        if (id == R.id.action_driver_organizer) {
            context.startActivity(new Intent(context, VeranstalterActivity.class));
            finish();
        }

        if (id == R.id.action_driver_parti) {
            context.startActivity(new Intent(context, TeilnehmerActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Intent i = new Intent(context, FahrerDetailActivity.class);
            i.putExtra("event", position);
            context.startActivity(i);
        }
    }
}

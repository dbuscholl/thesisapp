package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.GUI.Activities.LoginRegisterActivity;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class VeranstaltungHinzufuegenActivity extends AppCompatActivity {
    private ProgressDialog progress;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstaltung_hinzufuegen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        final EditText namewidget = (EditText) findViewById(R.id.vaAddName);
        final EditText addresswidget = (EditText) findViewById(R.id.vaAddAddress);
        final Button add = (Button) findViewById(R.id.vaAddAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = namewidget.getText().toString();
                final String address = addresswidget.getText().toString();
                progress.show();

                RequestQueue queue = Volley.newRequestQueue(VeranstaltungHinzufuegenActivity.this);
                String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD_uzMvvN_g4A8f4BJoIxzu82Zu-S2kybQ&language=de&components=country:de&input=" + address;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.d(this.toString(),response);
                        JSONObject ob = null;
                        try {
                            ob = new JSONObject(response);
                            if(ob.has("status")) {
                                if (ob.getString("status").equals("OK")) {
                                    JSONArray arr = ob.getJSONArray("predictions");
                                    final String [] values = new String[arr.length()];
                                    for(int i = 0; i < arr.length(); i++) {
                                        JSONObject item = arr.getJSONObject(i);
                                        String desc = item.getString("description");
                                        values[i] = desc;
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(VeranstaltungHinzufuegenActivity.this);
                                    builder.setTitle("Adresse auswählen:").setIcon(R.drawable.ic_location_on_black_24dp)
                                            .setItems(values, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    addresswidget.setText(values[which]);
                                                    progress.show();
                                                    Map<String,String> map = new HashMap<String, String>();
                                                    map.put("name",name);
                                                    map.put("adresse",values[which]);
                                                    createEvent(map);
                                                }
                                            });
                                    Dialog d = builder.create();
                                    d.show();
                                } else {
                                    ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
                                }
                            } else {
                                ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
                                finish();
                            }
                        } catch (Exception e) {
                            ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
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
        });
    }

    private void createEvent(final Map<String, String> map) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/events";

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
                            VeranstalterActivity.load(VeranstalterActivity.getContext());
                            Toast.makeText(VeranstaltungHinzufuegenActivity.this,"Veranstaltung hinzugefügt!",Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            if(ob.has("error")) ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this, ob.getString("error"));
                            else ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
                        }
                    } else {
                        ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(VeranstaltungHinzufuegenActivity.this);
                    e.printStackTrace();
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
                map.put("token", Token.get(VeranstaltungHinzufuegenActivity.this));
                return map;
            }
        };
        queue.add(stringRequest);

    }
}

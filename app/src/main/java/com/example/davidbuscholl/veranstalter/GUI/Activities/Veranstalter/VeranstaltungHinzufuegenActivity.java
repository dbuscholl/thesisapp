package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.ProgressDialog;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.AddressAutoComplete;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.AddressChosenInterface;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * the activity holding the gui from which the organizer can add new events
 */
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

        // just two simple text fields and a button which submits the values
        final EditText namewidget = (EditText) findViewById(R.id.vaAddName);
        final EditText addresswidget = (EditText) findViewById(R.id.vaAddAddress);
        final Button add = (Button) findViewById(R.id.vaAddAdd);

        // address is sent to the google maps autocomplete service to get a good address for geocoding
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = namewidget.getText().toString();
                final String address = addresswidget.getText().toString();
                AddressAutoComplete aac = new AddressAutoComplete();
                aac.show(VeranstaltungHinzufuegenActivity.this, address, new AddressChosenInterface() {
                    @Override
                    public void onAddressChosen(String chosen) {
                        progress.show();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name",name);
                        map.put("adresse",chosen);
                        createEvent(map);
                    }
                });
                }
        });
    }

    /**
     * the acutal process of adding an event to the list of oragnizing events. It sends the event data to the server.
     * this doesnt create any meetings yet. This has to be done in a seperate view.
     * @param map
     */
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
                            Toast.makeText(VeranstaltungHinzufuegenActivity.this,"Veranstaltung hinzugefügt!",Toast.LENGTH_SHORT).show();
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

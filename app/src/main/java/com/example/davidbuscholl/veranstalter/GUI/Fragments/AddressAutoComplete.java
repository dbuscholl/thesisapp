package com.example.davidbuscholl.veranstalter.GUI.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by David Buscholl on 22.02.2017.
 * This class shows the dialog to enter the users address for correct functionality of the app and
 * gets best suggestions by the google maps autocorrection api
 */

public class AddressAutoComplete {
    private static String chosen;

    public static void show(final Context context, String input, final AddressChosenInterface callback) {

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false);

        progress.show();

        //sending entered stirng to the google maps autocomplete api
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD_uzMvvN_g4A8f4BJoIxzu82Zu-S2kybQ&language=de&components=country:de&input=" + input;

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

                            // building the dialog from which the user can select one of the suggested addresses
                            JSONArray arr = ob.getJSONArray("predictions");
                            final String [] values = new String[arr.length()];
                            for(int i = 0; i < arr.length(); i++) {
                                JSONObject item = arr.getJSONObject(i);
                                String desc = item.getString("description");
                                values[i] = desc;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Adresse auswählen:").setIcon(R.drawable.ic_location_on_black_24dp)
                                    .setItems(values, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            chosen = values[which];
                                            // calling the callback of the interface for post selection (e.g. updating user details)
                                            callback.onAddressChosen(chosen);
                                        }
                                    });
                            builder.show();
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
}

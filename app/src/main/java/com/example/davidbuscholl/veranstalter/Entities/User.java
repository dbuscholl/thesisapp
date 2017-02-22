package com.example.davidbuscholl.veranstalter.Entities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.GUI.Activities.LoginRegisterActivity;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Buscholl on 14.11.2016.
 */
public class User {
    private int id;
    private String username;
    private String vorname;
    private String nachname;
    private String email;
    private String adresse;
    private String joined;
    private ArrayList<Integer> roles = new ArrayList<>();
    public ArrayList<Integer> roles() {
        return roles;
    }
    private static User current;

    public User() {}

    public User(JSONObject ob) {
        try {
            id = ob.getInt("id");
            username = ob.getString("username");
            vorname = ob.getString("vorname");
            nachname = ob.getString("nachname");
            email = ob.getString("email");
            adresse = ob.getString("adresse");
            joined = ob.getString("joined");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User(int id, String username, String vorname, String nachname, String email, String adresse, String joined) {
        this.id = id;
        this.username = username;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.adresse = adresse;
        this.joined = joined;
    }

    public void logout(final Context context) {
        ProgressDialog  progress = new ProgressDialog(context);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(context);
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
                            context.startActivity(new Intent(context, LoginRegisterActivity.class));
                            ((Activity) context).finish();
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token", Token.get(context));
                return map;
            }
        };
        queue.add(stringRequest);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public ArrayList<Integer> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Integer> roles) {
        this.roles = roles;
    }

    public static User getCurrent() {
        return current;
    }

    public static void setCurrent(User current) {
        User.current = current;
    }
}

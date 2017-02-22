package com.example.davidbuscholl.veranstalter.GUI.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Fahrer.FahrerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Teilnehmer.TeilnehmerActivity;
import com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter.VeranstalterActivity;
import com.example.davidbuscholl.veranstalter.R;
import com.example.davidbuscholl.veranstalter.GUI.Fragments.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);
        progress.setTitle("Ladevorgang");
        progress.setMessage("Bitte warten...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        prefs = this.getSharedPreferences("de.dbuscholl.veranstalter", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "0");
        Log.i(this.toString(), token);
        if (token.equals("0")) {
            startActivityForResult(new Intent(this, LoginRegisterActivity.class), 1);
            progress.dismiss();
        } else {
            validateToken(token);
        }
    }

    private void validateToken(final String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.221.196.48/thesis/public/user/validateLogin";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(this.toString(), response);
                JSONObject ob = null;
                progress.dismiss();
                try {
                    ob = new JSONObject(response);
                    if (ob.has("success")) {
                        if (ob.getBoolean("success")) {
                            prefs.edit().putString("token", ob.getString("token")).apply();
                            postLogin(MainActivity.this,ob);
                        } else {
                            startActivityForResult(new Intent(MainActivity.this, LoginRegisterActivity.class), 1);
                        }
                    } else {
                        ServerErrorDialog.show(MainActivity.this);
                    }
                } catch (Exception e) {
                    ServerErrorDialog.show(MainActivity.this);
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
                Map<String, String> map = new HashMap<>();
                map.put("token", prefs.getString("token", "0"));
                return map;
            }
        };
        queue.add(stringRequest);
    }

    public static void postLogin(Context context, JSONObject ob) throws JSONException {
        JSONObject data = ob.getJSONObject("data");
        User.setCurrent(new User(data));
        JSONArray roles = ob.getJSONArray("roles");

        if(roles.length()>0) {
            for(int i = 0; i < roles.length(); i++ ){
                User.getCurrent().roles().add(Integer.parseInt(roles.getJSONObject(i).getString("rolleId")));
            }
            int role = User.getCurrent().roles().get(0);
            switch(role) {
                case 1:
                    context.startActivity(new Intent(context,VeranstalterActivity.class));
                    break;
                case 2:
                    context.startActivity(new Intent(context,FahrerActivity.class));
                    break;
                case 3:
                    context.startActivity(new Intent(context,TeilnehmerActivity.class));
                    break;
            }
        }
    }

}

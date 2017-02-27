package com.example.davidbuscholl.veranstalter.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.davidbuscholl.veranstalter.GUI.Activities.LoginRegisterActivity;

/**
 * Created by David Buscholl on 21.11.2016.
 * simple token class which helpf to easily get the token from the sharedpreferences
 */

public class Token {

    public static String get(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("de.dbuscholl.veranstalter", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "0");
        Log.i(context.toString(), token);
        if (token.equals("0")) {
            context.startActivity(new Intent(context, LoginRegisterActivity.class));
        } else {
            return token;
        }
        ((Activity) context).finish();
        return "0";
    }
}

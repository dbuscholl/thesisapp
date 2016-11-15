package com.example.davidbuscholl.veranstalter;

import java.util.HashMap;

/**
 * Created by David Buscholl on 10.11.2016.
 */

public class Rules {
    public static HashMap<String, HashMap<String, String>> get() {
        HashMap<String, HashMap<String, String>> rules = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> usernamerules = new HashMap<String, String>();
        usernamerules.put("required", "true");
        usernamerules.put("min", "3");
        usernamerules.put("max", "20");

        HashMap<String, String> passwordrules = new HashMap<String, String>();
        passwordrules.put("required", "true");
        passwordrules.put("min", "8");
        passwordrules.put("max", "20");

        HashMap<String, String> pwagainrules = new HashMap<String, String>();
        pwagainrules.put("required", "true");
        pwagainrules.put("matches", "password");

        HashMap<String, String> emailrules = new HashMap<String, String>();
        emailrules.put("required", "true");
        emailrules.put("max", "50");
        emailrules.put("type", "email");

        rules.put("username", usernamerules);
        rules.put("password", passwordrules);
        rules.put("password_again", pwagainrules);
        rules.put("email", emailrules);
        return rules;
    }
}

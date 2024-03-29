package com.example.davidbuscholl.veranstalter.Helpers;

import java.util.HashMap;

/**
 * Created by David Buscholl on 10.11.2016.
 * An example list of rules for user registration and meetings creation
 */

public class ValidationRules {
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

        HashMap<String,String> typecountrules = new HashMap<>();
        typecountrules.put("required","true");
        typecountrules.put("minval","1");

        rules.put("username", usernamerules);
        rules.put("password", passwordrules);
        rules.put("password_again", pwagainrules);
        rules.put("email", emailrules);
        rules.put("typecount",typecountrules);
        return rules;
    }

    public static HashMap<String, HashMap<String, String>> getMeetingRules() {
        HashMap<String, HashMap<String, String>> rules = new HashMap<>();
        HashMap<String,String> daterules = new HashMap<>();
        daterules.put("required","true");
        daterules.put("notempty", "true");
        rules.put("date", daterules);

        HashMap<String,String> timerules = new HashMap<>();
        timerules.put("required","true");
        timerules.put("notempty","true");
        rules.put("starttime",timerules);
        rules.put("endtime",timerules);

        return rules;
    }
}

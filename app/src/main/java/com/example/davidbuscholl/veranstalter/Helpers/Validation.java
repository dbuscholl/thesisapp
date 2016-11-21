package com.example.davidbuscholl.veranstalter.Helpers;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Buscholl on 10.11.2016.
 */

public class Validation {
    private boolean passed = false;
    private ArrayList<String> errors = new ArrayList<>();

    public Validation check(HashMap<String, String> source, HashMap<String, HashMap<String,String>> items) {
        for(Map.Entry<String, HashMap<String,String>> entry : items.entrySet()) {
            HashMap<String, String> rules = entry.getValue();
            for(Map.Entry<String,String> ruleentry : rules.entrySet()) {
                String rule = TextUtils.htmlEncode(ruleentry.getKey());
                String ruleValue = TextUtils.htmlEncode(ruleentry.getValue());
                String item = TextUtils.htmlEncode(entry.getKey());
                String itemText = beautify(item);
                String value = source.get(item).trim();

                if(rule.equals("required") && value.equals("")) {
                    errors.add(itemText + "ist ein Pflichtfeld");
                } else if (!value.equals("")) {
                    switch(rule) {
                        case "min":
                            if(value.length() < Integer.parseInt(ruleValue)) {
                                errors.add(itemText + " muss mindestens " + ruleValue + " Buchstaben enthalten");
                            }
                            break;
                        case "max":
                            if(value.length() > Integer.parseInt(ruleValue)) {
                                errors.add(itemText + " darf höchstens " + ruleValue + " Buchstaben enthalten");
                            }
                            break;
                        case "matches":
                            if(!value.equals(source.get(ruleValue))) {
                                errors.add(itemText + " muss den gleichen Wert haben wie " + beautify(source.get(ruleValue)));
                            }
                            break;
                        case "type":
                            if(ruleValue.equals("email")) {
                                if(!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
                                    errors.add(itemText + " im ungültigen Format");
                                }
                            }
                            break;
                        case "minval":
                            if(Integer.parseInt(value) < Integer.parseInt(ruleValue)) {
                                errors.add("Es muss mindestens eine Nutzerrolle ausgewählt sein");
                            }
                            break;
                    }
                }
            }
        }

        if(errors.size() == 0) {
            passed = true;
        }

        return this;
    }

    private String beautify(String item) {
        switch(item) {
            case "username":
                return "Benutzername";
            case "password":
                return "Passwort";
            case "password_again":
                return "Passwort wiederholen";
            case "email":
                return "E-Mail";
        }
        return "";
    }

    public boolean hasPassed(){
        return passed;
    }

    public String[] getErrors() {
        return errors.toArray(new String[errors.size()]);
    }
}

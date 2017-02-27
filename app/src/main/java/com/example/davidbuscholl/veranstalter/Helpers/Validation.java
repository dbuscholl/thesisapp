package com.example.davidbuscholl.veranstalter.Helpers;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Buscholl on 10.11.2016.
 * The more or less complex validation class which works pretty much the same as the one on the server side.
 * You can add rules like "min" or "matches" here to define things like minimum char-length or matching
 * another field in the array
 */

public class Validation {
    // indicating wether validation was successfully passed
    private boolean passed = false;
    // storing all occuring errors here
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * check the source for the rules in the items array
     * @param source holding the keys indicating the field type which should be validated like "password" or "username"
     * @param items the criteria for the source like "min" => 6, which means minimum charlength must be 6 characters
     * @return returns instance of itself
     */
    public Validation check(HashMap<String, String> source, HashMap<String, HashMap<String,String>> items) {
        // every field which has rules ("password" => {"min" => 6, "matches" => "password_again"}, "username"....)
        for(Map.Entry<String, HashMap<String,String>> entry : items.entrySet()) {
            HashMap<String, String> rules = entry.getValue();

            // every rule entry such as "min"
            for(Map.Entry<String,String> ruleentry : rules.entrySet()) {
                // conains the rules type like "min"
                String rule = TextUtils.htmlEncode(ruleentry.getKey());
                // contains the valuue of a rule like number of minimum charlength
                String ruleValue = TextUtils.htmlEncode(ruleentry.getValue());
                String item = TextUtils.htmlEncode(entry.getKey());
                String itemText = beautify(item);
                // getting corresponding source item
                String value = source.get(item).trim();

                // check if its required
                if(rule.equals("required") && value.equals("")) {
                    errors.add(itemText + " ist ein Pflichtfeld");
                } else if (!value.equals("")) {
                    switch(rule) {
                        // minimum charlength rule
                        case "min":
                            if(value.length() < Integer.parseInt(ruleValue)) {
                                errors.add(itemText + " muss mindestens " + ruleValue + " Buchstaben enthalten");
                            }
                            break;
                        // maximum charlength rule
                        case "max":
                            if(value.length() > Integer.parseInt(ruleValue)) {
                                errors.add(itemText + " darf höchstens " + ruleValue + " Buchstaben enthalten");
                            }
                            break;
                        // has to be equal to another field in the source array
                        case "matches":
                            if(!value.equals(source.get(ruleValue))) {
                                errors.add(itemText + " muss den gleichen Wert haben wie " + beautify(source.get(ruleValue)));
                            }
                            break;
                        // has to match a formatting specific type
                        case "type":
                            if(ruleValue.equals("email")) {
                                if(!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
                                    errors.add(itemText + " im ungültigen Format");
                                }
                            }
                            break;
                        // the value mus be greater the defined in rulevalue
                        case "minval":
                            if(Integer.parseInt(value) < Integer.parseInt(ruleValue)) {
                                errors.add("Es muss mindestens eine Nutzerrolle ausgewählt sein");
                            }
                            break;
                        // must have at least one char inside
                        case "notempty":
                            if(value.length()<1) {
                                errors.add("Das Feld " + itemText + " darf nicht leer sein!");
                            }
                    }
                }
            }
        }

        if(errors.size() == 0) {
            passed = true;
        }

        return this;
    }

    /**
     * beautifies the keys for usefull error messages!
     * @param item the item whichs text should be beautified
     * @return returns the beautified string
     */
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
            case "date":
                return "Datum";
            case "starttime":
                return "Beginn";
            case "endtime":
                return "Ende";
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

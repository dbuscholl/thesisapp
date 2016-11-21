package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.davidbuscholl.veranstalter.R;

/**
 * Created by David Buscholl on 21.11.2016.
 */
public class AddressChooseDialog {
    private String[] values;

    public AddressChooseDialog(String[] values) {
        this.values = values;
    }

    public void create(Activity activity) {

    }
}

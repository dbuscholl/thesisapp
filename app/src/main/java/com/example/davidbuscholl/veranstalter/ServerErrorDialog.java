package com.example.davidbuscholl.veranstalter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by David Buscholl on 11.11.2016.
 */
public class ServerErrorDialog {
    public static void show(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Fehler")
                .setMessage("Unerwarteter Fehler")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}

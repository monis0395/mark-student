package com.mark.mark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by monis.q on 08-10-2017.
 */

public class Util {

    static void alertDialog(String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    static void closeActivityAlert(String msg, final Context context){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    static void changeHost(final Context context) {
        final StringRes sr = new StringRes();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sr.setHOSTNAME(userInput.getText().toString());
                                Toast.makeText(context, "HOST set successfully!", Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}



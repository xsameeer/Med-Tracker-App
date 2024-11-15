package com.example.medtrackerapp.Medication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medtrackerapp.database.DatabaseHandler;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int medicationId = intent.getIntExtra("medicationId", -1);

        if (medicationId == -1) {
            Log.e("NotificationActionReceiver", "Invalid medication ID");
            return;
        }

        // Initialize the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(context);

        // Handle "Yes" action
        if ("YES_ACTION".equals(action)) {
            dbHandler.storeResponse(medicationId,true );
            Log.d("NotificationActionReceiver", "User took medication with ID: " + medicationId);
        }
        // Handle "No" action
        else if ("NO_ACTION".equals(action)) {
            dbHandler.storeResponse(medicationId, false);
            Log.d("NotificationActionReceiver", "User did not take medication with ID: " + medicationId);
        }
    }
}
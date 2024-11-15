package com.example.medtrackerapp.Medication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medtrackerapp.database.DatabaseHandler;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int medicationId = intent.getIntExtra("medicationId", 1);
        int notificationId = intent.getIntExtra("notificationId", 1);

        Log.d("NotificationActionReceiver", "Received action: " + action);
        Log.d("NotificationActionReceiver", "medicationId: " + medicationId + ", notificationId: " + notificationId);

        // Initialize the database handler
        DatabaseHandler dbHandler = new DatabaseHandler(context);

        // Handle "Yes" action
        if ("YES_ACTION".equals(action)) {
            dbHandler.storeResponse(medicationId, true); // Store "Yes" response
            Log.d("NotificationActionReceiver", "User took medication with ID: " + medicationId);
        }
        // Handle "No" action
        else if ("NO_ACTION".equals(action)) {
            dbHandler.storeResponse(medicationId, false); // Store "No" response
            Log.d("NotificationActionReceiver", "User did not take medication with ID: " + medicationId);
        }

        // Cancel the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId); // Cancel the notification by its ID
            Log.d("NotificationActionReceiver", "Notification with ID " + notificationId + " cancelled");
        }
    }
}
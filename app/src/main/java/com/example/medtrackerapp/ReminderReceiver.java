package com.example.medtrackerapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MedicationReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medicationName");

        // Create the custom notification layout
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_medication_reminder);
        notificationLayout.setTextViewText(R.id.tvMedicationDetails, "Time to take your medication: " + medicationName);

        // Create a Notification Channel (required for Android 8.0+)
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Medication Reminders", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification with the custom layout
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)  // Replace with your actual icon
                .setContent(notificationLayout)                   // Set custom layout
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Notify the user
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}

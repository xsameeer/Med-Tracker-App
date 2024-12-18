package com.example.medtrackerapp.Medication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.medtrackerapp.R;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MedicationReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medicationName");
        int medicationId = intent.getIntExtra("medicationId", -1); // Ensure medicationId is passed for unique identification

        // Log the receipt of the broadcast
        Log.d("ReminderReceiver", "Received alarm for medication: " + medicationName);

        // Unique notification ID
        int notificationId = medicationId; // Use medicationId as the unique notification ID

// Intent for the "Yes" action
        Intent yesIntent = new Intent(context, NotificationActionReceiver.class);
        yesIntent.setAction("YES_ACTION");
        yesIntent.putExtra("medicationId", medicationId); // Pass medication ID
        yesIntent.putExtra("notificationId", notificationId); // Pass notification ID
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(
                context,
                medicationId, // Unique request code for "Yes"
                yesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

// Intent for the "No" action
        Intent noIntent = new Intent(context, NotificationActionReceiver.class);
        noIntent.setAction("NO_ACTION");
        noIntent.putExtra("medicationId", medicationId); // Pass medication ID
        noIntent.putExtra("notificationId", notificationId); // Pass notification ID
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(
                context,
                medicationId + 1, // Unique request code for "No"
                noIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Check notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("ReminderReceiver", "Notification permission not granted. Cannot show notification.");
            return;
        }

        // Create a notification channel for Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Medication Reminder Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Channel for medication reminders");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification with Yes and No buttons
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app's actual notification icon
                .setContentTitle("Medication Reminder")
                .setContentText("Did you take your medication: " + medicationName + "?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(R.drawable.ic_launcher_background, "Yes", yesPendingIntent) // Yes button
                .addAction(R.drawable.ic_launcher_foreground, "No", noPendingIntent);  // No button

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification.build());
    }
}
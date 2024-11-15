package com.example.medtrackerapp.Medication;

import static android.app.AlarmManager.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.R;
import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.Medication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private TableLayout tableMedications;
    private DatabaseHandler dbHandler;
    private String userEmail = "user@example.com"; // Replace with the actual logged-in user's email
    private AlarmManager alarmManager;
    private EditText medicationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // Check and request notification permission
        checkAndRequestNotificationPermission();

        // Existing initialization code
        dbHandler = new DatabaseHandler(this);
        tableMedications = findViewById(R.id.tableMedications);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        loadMedications();

        Button btnAddMedication = findViewById(R.id.btnAddMedication);
        btnAddMedication.setOnClickListener(v -> showAddMedicationDialog());
    }

    private void loadMedications() {
        tableMedications.removeAllViews();

        List<Medication> medications = dbHandler.getMedicationsByUser(userEmail);
        for (Medication medication : medications) {
            addMedicationRow(medication);
        }
    }

    private void addMedicationRow(final Medication medication) {
        TableRow row = new TableRow(this);

        TextView tvName = new TextView(this);
        tvName.setText(medication.getName());

        TextView tvDosage = new TextView(this);
        tvDosage.setText(medication.getDosage() + " mg");

        TextView tvFrequency = new TextView(this);
        tvFrequency.setText(medication.getFrequency() + "x/day");

        TextView tvReminderTime = new TextView(this);
        tvReminderTime.setId(View.generateViewId());

        Button btnRemove = new Button(this);
        btnRemove.setText("Remove");
        btnRemove.setOnClickListener(v -> {
            dbHandler.deleteMedication(medication.getId());
            loadMedications();
            Toast.makeText(this, "Medication Removed", Toast.LENGTH_SHORT).show();
        });

        row.addView(tvName);
        row.addView(tvDosage);
        row.addView(tvFrequency);
        row.addView(btnRemove);
        row.addView(tvReminderTime);

        tableMedications.addView(row);
    }

    private void showAddMedicationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_medication, null);

        final EditText etMedicationName = dialogView.findViewById(R.id.etMedicationName);
        final EditText etDosage = dialogView.findViewById(R.id.etDosage);
        final EditText etFrequency = dialogView.findViewById(R.id.etFrequency);
        final Button btnSetTime = dialogView.findViewById(R.id.btnSetTime);
        final TextView tvSelectedTime = dialogView.findViewById(R.id.tvSelectedTime);

        final LinearLayout daysLayout = dialogView.findViewById(R.id.daysLayout);
        CheckBox[] dayCheckBoxes = new CheckBox[7];
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < days.length; i++) {
            dayCheckBoxes[i] = new CheckBox(this);
            dayCheckBoxes[i].setText(days[i]);
            daysLayout.addView(dayCheckBoxes[i]);
        }

        final Calendar calendar = Calendar.getInstance();

        // Set time picker
        btnSetTime.setOnClickListener(v -> new android.app.TimePickerDialog(MedicationActivity.this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            String timeText = String.format("Selected time: %02d:%02d", hourOfDay, minute);
            tvSelectedTime.setText(timeText);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show());

        new AlertDialog.Builder(this)
                .setTitle("Add Medication")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etMedicationName.getText().toString();
                    int dosage = Integer.parseInt(etDosage.getText().toString());
                    int frequency = Integer.parseInt(etFrequency.getText().toString());
                    if (dbHandler.checkMedication(etMedicationName.getText().toString())){
                        Toast.makeText(this, "Medication already exists", Toast.LENGTH_SHORT).show();
                        return;
                        //exit here
                    }
                    else{
                    // Get selected days
                    List<String> selectedDays = new ArrayList<>();
                    for (CheckBox checkBox : dayCheckBoxes) {
                        if (checkBox.isChecked()) {
                            selectedDays.add(checkBox.getText().toString());
                        }
                    }

                    String daysOfWeek = String.join(",", selectedDays);

                    // Add medication to database
                    Medication medication = new Medication(0, name, dosage, frequency, daysOfWeek, "", false);
                    dbHandler.addMedication(userEmail, name, dosage, frequency, daysOfWeek, "", false);

                    // Schedule alarms for each selected day
                    scheduleAlarmsForMedication(medication, calendar, selectedDays);

                    loadMedications();
                    Toast.makeText(this, "Medication Added", Toast.LENGTH_SHORT).show();
                        }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void scheduleAlarmsForMedication(Medication medication, Calendar calendar, List<String> selectedDays) {
        for (String day : selectedDays) {
            int dayOfWeek = getDayOfWeek(day);

            // Adjust calendar for the correct day of the week
            Calendar alarmCalendar = (Calendar) calendar.clone();
            alarmCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

            // If the alarm time has already passed for this week, set it for the next week
            if (alarmCalendar.before(Calendar.getInstance())) {
                alarmCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            // Schedule the alarm
            scheduleAlarm(medication, alarmCalendar);
        }
    }

    private void scheduleAlarm(Medication medication, Calendar calendar) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicationName", medication.getName());

        // Create a unique PendingIntent using medication ID and day of week
        int requestCode = medication.getId() + calendar.get(Calendar.DAY_OF_WEEK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule the alarm
        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Log.d("MedicationActivity", "Alarm set for: " + calendar.getTime() + " for medication: " + medication.getName());
        }
    }


    private List<String> getSelectedDays(String daysOfWeek) {
        List<String> selectedDays = new ArrayList<>();
        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            String[] daysArray = daysOfWeek.split(",");
            for (String day : daysArray) {
                selectedDays.add(day.trim());
            }
        }
        return selectedDays;
    }

    private int getDayOfWeek(String day) {
        switch (day) {
            case "Sun":
                return Calendar.SUNDAY;
            case "Mon":
                return Calendar.MONDAY;
            case "Tue":
                return Calendar.TUESDAY;
            case "Wed":
                return Calendar.WEDNESDAY;
            case "Thu":
                return Calendar.THURSDAY;
            case "Fri":
                return Calendar.FRIDAY;
            case "Sat":
                return Calendar.SATURDAY;
            default:
                return Calendar.SUNDAY;
        }
    }

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    // Check and request notification permission
    private void checkAndRequestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. You may miss reminders.", Toast.LENGTH_LONG).show();
                // Optionally guide users to enable permissions
                promptToOpenAppSettings();
            }
        }
    }

    // Optional: Prompt to open app settings
    private void promptToOpenAppSettings() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Notification permission is required to remind you about your medications. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

//adherence tracker

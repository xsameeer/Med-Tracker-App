package com.example.medtrackerapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.Medication;
import java.util.Calendar;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private TableLayout tableMedications;
    private DatabaseHandler dbHandler;
    private String userEmail = "user@example.com"; // Replace with the actual logged-in user's email
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // Initialize DatabaseHandler, TableLayout, and AlarmManager
        dbHandler = new DatabaseHandler(this);
        tableMedications = findViewById(R.id.tableMedications);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Load existing medications from the database for the specific user
        loadMedications();

        // Set up the Add Medication button to open a dialog for adding new medication
        Button btnAddMedication = findViewById(R.id.btnAddMedication);
        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicationDialog();
            }
        });
    }

    /**
     * Load medications from the database and display them in the TableLayout.
     */
    private void loadMedications() {
        tableMedications.removeAllViews(); // Clear the table before loading

        List<Medication> medications = dbHandler.getMedicationsByUser(userEmail); // Get medications for user
        for (Medication medication : medications) {
            addMedicationRow(medication); // Add each medication as a row in the table
        }
    }

    /**
     * Adds a row to the TableLayout for the specified medication, with reminder functionality.
     */
    private void addMedicationRow(final Medication medication) {
        TableRow row = new TableRow(this);

        // Create TextViews for each column in the row
        TextView tvName = new TextView(this);
        tvName.setText(medication.getName());

        TextView tvDosage = new TextView(this);
        tvDosage.setText(medication.getDosage() + " mg");

        TextView tvFrequency = new TextView(this);
        tvFrequency.setText(medication.getFrequency() + "x/day");

        // TextView to display reminder time
        TextView tvReminderTime = new TextView(this);
        tvReminderTime.setId(View.generateViewId());
        tvReminderTime.setText("Reminder: Not set");

        // Button to set the reminder
        Button btnSetReminder = new Button(this);
        btnSetReminder.setText("Set Reminder");
        btnSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(medication, tvReminderTime);
            }
        });

        // Create a Remove button for each row
        Button btnRemove = new Button(this);
        btnRemove.setText("Remove");
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteMedication(medication.getId()); // Delete medication from database
                loadMedications(); // Refresh the table
            }
        });

        // Add the TextViews and Buttons to the row
        row.addView(tvName);
        row.addView(tvDosage);
        row.addView(tvFrequency);
        row.addView(tvReminderTime);
        row.addView(btnSetReminder);
        row.addView(btnRemove);

        // Add the row to the table layout
        tableMedications.addView(row);
    }

    /**
     * Show a dialog to add a new medication and save it to the database.
     */
    private void showAddMedicationDialog() {
        // Inflate a custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_medication, null); // Assuming a custom dialog XML

        final EditText etMedicationName = dialogView.findViewById(R.id.etMedicationName);
        final EditText etDosage = dialogView.findViewById(R.id.etDosage);
        final EditText etFrequency = dialogView.findViewById(R.id.etFrequency);

        // Build and show the dialog
        new AlertDialog.Builder(this)
                .setTitle("Add Medication")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etMedicationName.getText().toString();
                        int dosage = Integer.parseInt(etDosage.getText().toString());
                        int frequency = Integer.parseInt(etFrequency.getText().toString());

                        // Add the new medication to the database
                        dbHandler.addMedication(userEmail, name, dosage, frequency);

                        // Reload the medications in the table
                        loadMedications();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Show a TimePicker dialog for setting medication reminders.
     */
    private void showTimePickerDialog(Medication medication, TextView tvReminderTime) {
        Calendar calendar = Calendar.getInstance();

        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setReminder(medication, calendar, tvReminderTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    /**
     * Set a reminder for the medication using AlarmManager.
     */
    private void setReminder(Medication medication, Calendar calendar, TextView tvReminderTime) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicationName", medication.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medication.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Update reminder display
        tvReminderTime.setText("Reminder set at: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }
}

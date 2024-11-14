package com.example.medtrackerapp.Medication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        dbHandler = new DatabaseHandler(this);
        tableMedications = findViewById(R.id.tableMedications);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        loadMedications();

        Button btnAddMedication = findViewById(R.id.btnAddMedication);
        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicationDialog();
            }
        });
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

        Button btnSetReminder = new Button(this);
        btnSetReminder.setText("Set Reminder");
        btnSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(medication, tvReminderTime);
            }
        });

        Button btnRemove = new Button(this);
        btnRemove.setText("Remove");
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteMedication(medication.getId());
                loadMedications();
            }
        });

        row.addView(tvName);
        row.addView(tvDosage);
        row.addView(tvFrequency);
        row.addView(btnRemove);
        row.addView(tvReminderTime);
        row.addView(btnSetReminder);

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

        // Add day of week checkboxes
        final LinearLayout daysLayout = dialogView.findViewById(R.id.daysLayout);
        CheckBox[] dayCheckBoxes = new CheckBox[7];
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < days.length; i++) {
            dayCheckBoxes[i] = new CheckBox(this);
            dayCheckBoxes[i].setText(days[i]);
            daysLayout.addView(dayCheckBoxes[i]);
        }

        // Calendar to hold the selected time
        final Calendar calendar = Calendar.getInstance();

        // Set up the time picker button
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open time picker dialog
                new android.app.TimePickerDialog(MedicationActivity.this, (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Display selected time
                    String timeText = String.format("Selected time: %02d:%02d", hourOfDay, minute);
                    tvSelectedTime.setText(timeText);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Add Medication")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etMedicationName.getText().toString();
                        int dosage = Integer.parseInt(etDosage.getText().toString());
                        int frequency = Integer.parseInt(etFrequency.getText().toString());

                        List<String> selectedDays = new ArrayList<>();
                        for (int i = 0; i < days.length; i++) {
                            if (dayCheckBoxes[i].isChecked()) {
                                selectedDays.add(days[i]);
                            }
                        }

                        String daysOfWeek = String.join(",", selectedDays);

                        // Add medication with selected time
                        dbHandler.addMedication(userEmail, name, dosage, frequency, daysOfWeek, "", false);

                        loadMedications();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        Toast.makeText(this, "Reminder Placed", Toast.LENGTH_SHORT).show();
    }

    private void showTimePickerDialog(Medication medication, TextView tvReminderTime) {
        Calendar calendar = Calendar.getInstance();

        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setReminder(medication, calendar, tvReminderTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void setReminder(Medication medication, Calendar calendar, TextView tvReminderTime) {
        List<String> selectedDays = getSelectedDays(medication.getDaysOfWeek());
        Calendar now = Calendar.getInstance();
        int currentDay = now.get(Calendar.DAY_OF_WEEK);

        for (String day : selectedDays) {
            int dayOfWeek = getDayOfWeek(day);
            if (dayOfWeek >= currentDay) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                scheduleAlarm(medication, calendar, tvReminderTime);
            }
        }
    }

    private void scheduleAlarm(Medication medication, Calendar calendar, TextView tvReminderTime) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicationName", medication.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medication.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        tvReminderTime.setText("Reminder set for: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
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
}

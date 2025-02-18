package com.example.medtrackerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.Medication.MedicationActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnMedication;
    private Button btnCalendar;
    private Button btnProfile;
    private Button btnReports;
    private Button btnSettings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnMedication = findViewById(R.id.btnMedication);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnProfile = findViewById(R.id.btnProfile);
        btnReports = findViewById(R.id.btnReports);
        btnSettings = findViewById(R.id.btnSettings);

        // Set click listeners for buttons
        btnMedication.setOnClickListener(v -> {
            // Handle Medication button click
            Intent intent = new Intent(HomeActivity.this, MedicationActivity.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(v -> {
            // Handle Calendar button click
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            // Handle profile button click
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        btnReports.setOnClickListener(v -> {
            // Handle Reports button click
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            // Handle Settings button click
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

    }
}





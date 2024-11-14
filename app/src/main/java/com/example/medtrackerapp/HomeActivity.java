package com.example.medtrackerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.*;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnMedication;
    private Button btnCalendar;
    private Button btnProfile;
    private Button btnReports;
    private Button btnSettings;
    private Button btnLogout;

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
        btnLogout = findViewById(R.id.btnLogout);

        // Set click listeners for buttons
        btnMedication.setOnClickListener(v -> {
            // Handle Medication button click
            Intent intent = new Intent(this, MedicationActivity.class);
        });

        btnCalendar.setOnClickListener(v -> {
            // Handle Calendar button click
            Intent intent = new Intent(this, CalendarActivity.class);
        });

        btnProfile.setOnClickListener(v -> {
            // Handle Profile button click
            Intent intent = new Intent(this, ProfileActivity.class);
        });

        btnReports.setOnClickListener(v -> {
            // Handle Reports button click
            Intent intent = new Intent(this, ReportsActivity.class);
        });

        btnSettings.setOnClickListener(v -> {
            // Handle Settings button click
            Intent intent = new Intent(this, SettingsActivity.class);
        });

        btnLogout.setOnClickListener(v -> {
            // Handle Logout button click
            Intent intent = new Intent(this, LoginActivity.class);
        });

    }
}





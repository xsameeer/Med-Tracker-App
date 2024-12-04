package com.example.medtrackerapp.Settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.R;

public class SettingsActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", "");
        String email = sharedPreferences.getString("Email", "");

        // Populate fields with existing data
        nameEditText.setText(name);
        emailEditText.setText(email);

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            // Get user input
            String updatedName = nameEditText.getText().toString().trim();
            String updatedEmail = emailEditText.getText().toString().trim();

            // Validate inputs
            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save updated data to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", updatedName);
            editor.putString("Email", updatedEmail);
            editor.apply();

            // Show success message
            Toast.makeText(this, "Account settings updated!", Toast.LENGTH_SHORT).show();
        });

    }
}
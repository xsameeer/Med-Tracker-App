package com.example.medtrackerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.User;
import com.example.medtrackerapp.RegistrationActivity;


public class ProfileActivity extends AppCompatActivity {
    private TextView nameTxtView;
    private TextView date_of_birthTxtView;
    private TextView genderTxtView;
    private TextView healthcareProviderTxtView;
    private ImageView profileImageView;
    private Button btnLogout;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTxtView = findViewById(R.id.Name);
        date_of_birthTxtView = findViewById(R.id.date_of_birth);
        genderTxtView = findViewById(R.id.Gender);
        healthcareProviderTxtView = findViewById(R.id.healthcare_provider);
        profileImageView = findViewById(R.id.profilePicture);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", "No name");
        String dateOfBirth = sharedPreferences.getString("DOB", "N/A");
        String gender = sharedPreferences.getString("Gender", "Unknown");
        String healthcareProvider = sharedPreferences.getString("Healthcare Provider", "N/A");

        nameTxtView.setText("Name: " + name);
        date_of_birthTxtView.setText("DOB: " + dateOfBirth);
        genderTxtView.setText("Gender: " + gender);
        healthcareProviderTxtView.setText("Healthcare Provider: " + healthcareProvider);

        btnLogout.setOnClickListener(v -> {
            // Handle Logout button click
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

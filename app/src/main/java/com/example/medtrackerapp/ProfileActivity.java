package com.example.medtrackerapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.User;
import com.example.medtrackerapp.RegistrationActivity;


public class ProfileActivity extends AppCompatActivity {
    private TextView nameTxtView;
    private TextView ageTxtView;
    private TextView genderTxtView;
    private TextView healthcareProviderTxtView;
    private ImageView profileImageView;
    private DatabaseHandler databaseHandler;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHandler = new DatabaseHandler(this);

        nameTxtView = findViewById(R.id.Name);
        ageTxtView = findViewById(R.id.Age);
        genderTxtView = findViewById(R.id.Gender);
        healthcareProviderTxtView = findViewById(R.id.healthcare_provider);
        profileImageView = findViewById(R.id.profilePicture);

        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", "No name");
        int age = sharedPreferences.getInt("Age", 0);
        String gender = sharedPreferences.getString("Gender", "Unknown");
        String healthcareProvider = sharedPreferences.getString("Healthcare Provider", "N/A");

        nameTxtView.setText("Name: " + name);
        ageTxtView.setText("Age: " + age);
        genderTxtView.setText("Gender: " + gender);
        healthcareProviderTxtView.setText("Healtcare Provider " + healthcareProvider);

        //Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
    }
}

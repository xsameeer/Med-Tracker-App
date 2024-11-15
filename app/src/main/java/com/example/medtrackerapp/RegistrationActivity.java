package com.example.medtrackerapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.User;

public class RegistrationActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtProviderEmail;
    private EditText txtDateOfBirth;
    private EditText txtName;
    private EditText txtGender;
    private Button btnRegister;
    private DatabaseHandler databasehandler;

    // RegistrationActivity.java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Initialize registration components here
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtProviderEmail = findViewById(R.id.txtProviderEmail);
        txtDateOfBirth = findViewById(R.id.txtDateOfBirth);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        btnRegister = findViewById(R.id.btnRegister);

        databasehandler = new DatabaseHandler(this);
        btnRegisterListener();
    }

public void btnRegisterListener() {
    btnRegister.setOnClickListener(v -> {
        if (!databasehandler.checkUser(txtUsername.getText().toString().trim())) {
            User user = new User();
            user.setEmail(txtUsername.getText().toString().trim());
            user.setPassword(txtPassword.getText().toString().trim());
            user.setProviderEmail(txtProviderEmail.getText().toString().trim());
            user.setDateOfBirth(txtDateOfBirth.getText().toString().trim());
            user.setName(txtName.getText().toString().trim());
            user.setGender(txtGender.getText().toString().trim());

            //Save user data to sharedpreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserProfile" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", user.getName());
            editor.putString("Email", user.getEmail());
            editor.putString("Gender", user.getGender());
            editor.putString("DOB", user.getDateOfBirth());
            editor.putString("Healthcare Provider", user.getProviderEmail());
            editor.apply(); //Apply changes

            databasehandler.addUser(user);
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);  // takes you back to login screen after registration
            Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
        }
        });
    };
}


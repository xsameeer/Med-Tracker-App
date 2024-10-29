package com.example.medtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.User;

public class LoginActivity extends AppCompatActivity {

    // DatabaseHelper instance for managing database operations
    private DatabaseHandler databasehandler;

    // Declare views
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtProviderEmail;
    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Sets the layout for the login screen

        // Initializes the DatabaseHelper with the current context
        databasehandler = new DatabaseHandler(this);

        // Initialize views after setContentView
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtProviderEmail = findViewById(R.id.txtProviderEmail);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Sets up the listeners for the Register and Login buttons
        btnRegisterListener();
        btnLoginListener();
    }

    private void btnRegisterListener() {
        btnRegister.setOnClickListener(v -> {
            if (!databasehandler.checkUser(txtUsername.getText().toString().trim())) {
                User user = new User();
                user.setEmail(txtUsername.getText().toString().trim());
                user.setPassword(txtPassword.getText().toString().trim());
                user.setProviderEmail(txtProviderEmail.getText().toString().trim());

                databasehandler.addUser(user);
                Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void btnLoginListener() {
        btnLogin.setOnClickListener(v -> {
            if (databasehandler.checkUser(
                    txtUsername.getText().toString().trim(),
                    txtPassword.getText().toString().trim())) {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username or password is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
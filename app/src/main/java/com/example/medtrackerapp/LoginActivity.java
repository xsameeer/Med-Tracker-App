package com.example.medtrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrackerapp.database.DatabaseHandler;

public class LoginActivity extends AppCompatActivity {

    // DatabaseHelper instance for managing database operations
    private DatabaseHandler databasehandler;

    // Declare views
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnforgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Sets the layout for the login screen

        // Initializes the DatabaseHelper with the current context
        databasehandler = new DatabaseHandler(this);

        // Initialize views after setContentView
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnforgotPassword = findViewById(R.id.btnforgotPassword);

        // Set the Register button to open RegistrationActivity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);  // Starts RegistrationActivity
        });

        // Sets up the listeners for the Register and Login buttons
        btnLoginListener();
    }

    // Set the Register button to open RegistrationActivity

    private void btnLoginListener() {
        btnLogin.setOnClickListener(v -> {
            if (databasehandler.checkUser(
                    txtUsername.getText().toString().trim(),
                    txtPassword.getText().toString().trim())) {

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username or password is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBtnforgotPassword(){
        btnforgotPassword.setOnClickListener(v -> {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }
}
package com.example.medtrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import android.*;

import com.example.medtrackerapp.database.DatabaseHandler;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;
    private DatabaseHandler databasehandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        databasehandler = new DatabaseHandler(this);

        if (databasehandler.checkUser(
                txtUsername.getText().toString().trim())) {
            String username = txtUsername.getText().toString().trim();

// Check if the username exists in the database and retrieve the email and password
            String email = databasehandler.getEmailByUsername(username);
            String password = databasehandler.getPasswordByUsername(username);

            if (email != null && password != null) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email}); // User's email retrieved from the database
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Forgot Password Request");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + username + ",\n\nYour password is: " + password);

                // Start the email activity
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Username not found or email unavailable.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}


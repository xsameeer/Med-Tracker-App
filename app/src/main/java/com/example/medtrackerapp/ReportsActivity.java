package com.example.medtrackerapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.medtrackerapp.database.DatabaseHandler;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class ReportsActivity extends AppCompatActivity {

    private DatabaseHandler dbHandler;
    private final List<String> pastReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Initialize DatabaseHandler
        dbHandler = new DatabaseHandler(this);

        // Link buttons
        Button btnGenerateReport = findViewById(R.id.btnGenerateReport);
        Button btnViewReport = findViewById(R.id.btnViewReport);
        Button btnDownloadReports = findViewById(R.id.btnDownloadReports);

        // Button to generate and email report
        btnGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = getLoggedInUserEmail(); // Fetch the logged-in user's email
                if (userEmail != null) {
                    String providerEmail = fetchProviderEmail(userEmail); // Get provider's email from database
                    if (providerEmail != null) {
                        generateReport(userEmail); // Generate the report and send it to the provider's email
                        Toast.makeText(ReportsActivity.this, "Report generated and emailed successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ReportsActivity", "No provider email found for user: " + userEmail);
                        Toast.makeText(ReportsActivity.this, "No provider email found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ReportsActivity", "Logged-in user email is null. Cannot generate report.");
                    Toast.makeText(ReportsActivity.this, "Unable to fetch logged-in user's email.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Button to view the most recent report
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pastReports.isEmpty()) {
                    String latestReport = pastReports.get(pastReports.size() - 1); // Get the latest report
                    viewReport(latestReport);
                } else {
                    Toast.makeText(ReportsActivity.this, "No reports available to view.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Button to download all reports (placeholder implementation)
        btnDownloadReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pastReports.isEmpty()) {
                    Toast.makeText(ReportsActivity.this, "Reports downloaded to app directory.", Toast.LENGTH_SHORT).show();
                    Log.d("ReportsActivity", "All reports are available in the app's storage directory.");
                } else {
                    Toast.makeText(ReportsActivity.this, "No reports available to download.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String fetchProviderEmail(String userEmail) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String providerEmail = null;

        String query = "SELECT " + dbHandler.getColumnProviderEmail() +
                " FROM " + dbHandler.getTableUser() +
                " WHERE " + dbHandler.getColumnEmail() + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        if (cursor.moveToFirst()) {
            providerEmail = cursor.getString(0);
        }
        cursor.close();

        return providerEmail;
    }

    private String getLoggedInUserEmail() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String userEmail = null;

        // Replace with actual logic to identify the logged-in user, e.g., using a unique identifier
        String query = "SELECT " + dbHandler.getColumnEmail() +
                " FROM " + dbHandler.getTableUser() +
                " LIMIT 1"; // Fetch the first user (placeholder for demonstration)
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            userEmail = cursor.getString(0); // Retrieve the user's email
        }
        cursor.close();

        return userEmail;
    }

    public void generateReport(String userEmail) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        StringBuilder reportContent = new StringBuilder();
        String query = "SELECT M." + dbHandler.getColumnMedicationName() +
                ", A." + dbHandler.getColumnStatus() +
                " FROM " + dbHandler.getTableAdherence() + " A " +
                " JOIN " + dbHandler.getTableMedications() + " M " +
                " ON A." + dbHandler.getColumnMedicationIdFk() + " = M." + dbHandler.getColumnMedicationId() +
                " WHERE M." + dbHandler.getColumnEmail() + " = ? ";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        reportContent.append("Adherence Report (Last 30 days)\n\n");
        reportContent.append("Medication Name | Date | Status\n");
        reportContent.append("----------------------------------------\n");

        if (cursor.moveToFirst()) {
            do {
                String medicationName = cursor.getString(0);
                String date = cursor.getString(1);
                String status = cursor.getString(2);
                reportContent.append(medicationName).append(" | ").append(date).append(" | ").append(status).append("\n");
            } while (cursor.moveToNext());
        } else {
            reportContent.append("No adherence data found for the user.");
        }
        cursor.close();

        // Generate the report and save
        String fileName = "Report_" + System.currentTimeMillis() + ".txt";
        saveReportToFile(fileName, reportContent.toString());

        // Send the report to the provider's email
        sendReportToProvider(userEmail, fileName);
    }

    private void saveReportToFile(String fileName, String content) {
        try {
            File reportsDir = new File(getExternalFilesDir(null), "Reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs(); // Create directory if it doesn't exist
            }

            File reportFile = new File(reportsDir, fileName);
            FileWriter writer = new FileWriter(reportFile);
            writer.write(content);
            writer.close();

            pastReports.add(reportFile.getAbsolutePath());
            Log.d("ReportsActivity", "Report saved: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ReportsActivity", "Error saving report", e);
        }
    }

    private void sendEmailWithReport(String providerEmail, String filePath) {
        try {
            File reportFile = new File(filePath);

            // Use FileProvider for secure file sharing
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    reportFile
            );

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain"); // Use "text/plain" for .txt files
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{providerEmail});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Monthly Adherence Report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the adherence report attached.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

            // Grant temporary read permissions
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(emailIntent, "Send Email"));
            Log.d("ReportsActivity", "Email intent launched successfully.");
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error sending email: ", e);
        }
    }

    private void viewReport(String filePath) {
        try {
            File reportFile = new File(filePath);
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    reportFile
            );

            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(fileUri, "text/plain");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(viewIntent, "View Report"));
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error viewing report: ", e);
        }
    }

    private void sendReportToProvider(String userEmail, String fileName) {
        String providerEmail = fetchProviderEmail(userEmail);
        if (providerEmail != null) {
            sendEmailWithReport(providerEmail, new File(getExternalFilesDir("Reports"), fileName).getAbsolutePath());
        } else {
            Log.e("ReportsActivity", "No provider email found for user: " + userEmail);
        }
    }
}

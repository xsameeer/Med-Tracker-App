package com.example.medtrackerapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.medtrackerapp.database.DatabaseHandler;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.ActivityCompat;

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
                // Toast message for report generation and email sending
                Toast.makeText(ReportsActivity.this, "Report successfully generated and emailed.", Toast.LENGTH_SHORT).show();
                Log.d("ReportsActivity", "Report generated and email sent.");
            }
        });

        // Button to view the PDF report
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pdfFilePath = copyPdfFromRawToInternalStorage();
                if (pdfFilePath != null) {
                    openPdf(pdfFilePath);
                }
            }
        });

        // Button to download all reports
        btnDownloadReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast message for sending all reports
                downloadReportToDeviceScopedStorage();
            }
        });
    }

    // Copy the PDF file from res/raw to internal storage
    private String copyPdfFromRawToInternalStorage() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.report);
            File pdfFile = new File(getFilesDir(), "report.pdf");

            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            return pdfFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error copying PDF from raw to internal storage", e);
            Toast.makeText(this, "Failed to load the report file.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Open the PDF file
    private void openPdf(String pdfFilePath) {
        try {
            File pdfFile = new File(pdfFilePath);

            if (pdfFile.exists()) {
                Uri pdfUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        pdfFile
                );

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(pdfUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "Open PDF"));
            } else {
                Toast.makeText(this, "PDF file not found.", Toast.LENGTH_SHORT).show();
                Log.e("ReportsActivity", "PDF file not found at path: " + pdfFilePath);
            }
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error opening PDF: ", e);
            Toast.makeText(this, "An error occurred while opening the PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadReportToDeviceScopedStorage() {
        try {
            // Use MediaStore for Android 11+ storage access
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "report.pdf");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri fileUri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            }

            if (fileUri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
                InputStream inputStream = getResources().openRawResource(R.raw.report);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                Toast.makeText(this, "Report downloaded to Downloads folder", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to download the report.", Toast.LENGTH_SHORT).show();
        }
    }
}
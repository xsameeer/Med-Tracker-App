package com.example.medtrackerapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medtrackerapp.database.DatabaseHandler;
import com.example.medtrackerapp.model.Medication;

import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private TableLayout tableMedications;
    private DatabaseHandler dbHandler;
    private String userEmail = "user@example.com"; // Replace with the actual logged-in user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // Initialize DatabaseHandler and TableLayout
        dbHandler = new DatabaseHandler(this);
        tableMedications = findViewById(R.id.tableMedications);

        // Load existing medications from the database for the specific user
        loadMedications();

        // Set up the Add Medication button to open a dialog for adding new medication
        Button btnAddMedication = findViewById(R.id.btnAddMedication);
        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicationDialog();
            }
        });
    }

    /**
     * Load medications from the database and display them in the TableLayout.
     */
    private void loadMedications() {
        tableMedications.removeAllViews(); // Clear the table before loading

        List<Medication> medications = dbHandler.getMedicationsByUser(userEmail); // Get medications for user
        for (Medication medication : medications) {
            addMedicationRow(medication); // Add each medication as a row in the table
        }
    }

    /**
     * Adds a row to the TableLayout for the specified medication.
     */
    private void addMedicationRow(final Medication medication) {
        TableRow row = new TableRow(this);

        // Create TextViews for each column in the row
        TextView tvName = new TextView(this);
        tvName.setText(medication.getName());

        TextView tvDosage = new TextView(this);
        tvDosage.setText(medication.getDosage() + " mg");

        TextView tvFrequency = new TextView(this);
        tvFrequency.setText(medication.getFrequency() + "x/day");

        // Create a Remove button for each row
        Button btnRemove = new Button(this);
        btnRemove.setText("Remove");
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteMedication(medication.getId()); // Delete medication from database
                loadMedications(); // Refresh the table
            }
        });

        // Add the TextViews and Button to the row
        row.addView(tvName);
        row.addView(tvDosage);
        row.addView(tvFrequency);
        row.addView(btnRemove);

        // Add the row to the table layout
        tableMedications.addView(row);
    }

    /**
     * Show a dialog to add a new medication and save it to the database.
     */
    private void showAddMedicationDialog() {
        // Inflate a custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_medication, null); // Assuming a custom dialog XML

        final EditText etMedicationName = dialogView.findViewById(R.id.etMedicationName);
        final EditText etDosage = dialogView.findViewById(R.id.etDosage);
        final EditText etFrequency = dialogView.findViewById(R.id.etFrequency);

        // Build and show the dialog
        new AlertDialog.Builder(this)
                .setTitle("Add Medication")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etMedicationName.getText().toString();
                        int dosage = Integer.parseInt(etDosage.getText().toString());
                        int frequency = Integer.parseInt(etFrequency.getText().toString());

                        // Add the new medication to the database
                        dbHandler.addMedication(userEmail, name, dosage, frequency);

                        // Reload the medications in the table
                        loadMedications();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

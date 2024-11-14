package com.example.medtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.medtrackerapp.model.User;
import com.example.medtrackerapp.model.Medication; // Import Medication model class
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database version and name constants
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "UserManager.db";

    // User table and column names
    private static final String TABLE_USER = "user";
    private static final String COLUMN_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_PROVIDER_EMAIL = "user_providerEmail";

    // Medications table and column names
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String COLUMN_MEDICATION_ID = "medication_id";
    private static final String COLUMN_MEDICATION_NAME = "medication_name";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_FREQUENCY = "frequency";

    // SQL statement to create the user table
    private static final String USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_EMAIL + " TEXT PRIMARY KEY,"  // Primary key: email
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_PROVIDER_EMAIL + " TEXT" + ")";    // Password column

    // SQL statement to create the medications table
    private static final String CREATE_MEDICATIONS_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS + "("
            + COLUMN_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMAIL + " TEXT,"  // Foreign key: user email
            + COLUMN_MEDICATION_NAME + " TEXT,"
            + COLUMN_DOSAGE + " INTEGER,"
            + COLUMN_FREQUENCY + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_EMAIL + ") REFERENCES " + TABLE_USER + "(" + COLUMN_EMAIL + "))";

    // SQL statement to drop the user and medications tables if they exist
    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private static final String DROP_MEDICATIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_MEDICATIONS;

    // Constructor to initialize the DatabaseHelper
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE);           // Create user table
        db.execSQL(CREATE_MEDICATIONS_TABLE);     // Create medications table
    }

    // Called when the database version changes, to update the database schema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);        // Drops the existing user table
        db.execSQL(DROP_MEDICATIONS_TABLE); // Drops the existing medications table
        onCreate(db);                       // Recreates the tables by calling onCreate
    }

    // User-related methods

    /**
     * Adds a new user to the database.
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_PROVIDER_EMAIL, user.getProviderEmail());

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * Checks if a user with a specific email exists in the database.
     */
    public boolean checkUser(String email) {
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    /**
     * Checks if a user with a specific email and password exists in the database.
     */
    public boolean checkUser(String email, String password) {
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    /**
     * Retrieves the provider email based on the user's email.
     */
    public String getProviderEmailByUserEmail(String email) {
        String providerEmail = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_PROVIDER_EMAIL},
                COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                providerEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVIDER_EMAIL));
            }
            cursor.close();
        }
        db.close();
        return providerEmail;
    }

    // Medication-related methods

    /**
     * Adds a new medication for a specific user.
     */
    public void addMedication(String userEmail, String name, int dosage, int frequency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, userEmail);
        values.put(COLUMN_MEDICATION_NAME, name);
        values.put(COLUMN_DOSAGE, dosage);
        values.put(COLUMN_FREQUENCY, frequency);

        db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
    }

    /**
     * Retrieves all medications for a specific user by email.
     */
    public List<Medication> getMedicationsByUser(String userEmail) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { userEmail };

        Cursor cursor = db.query(TABLE_MEDICATIONS,
                new String[]{COLUMN_MEDICATION_ID, COLUMN_MEDICATION_NAME, COLUMN_DOSAGE, COLUMN_FREQUENCY},
                selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Medication medication = new Medication(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY))
                );
                medications.add(medication);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return medications;
    }

    /**
     * Deletes a specific medication by its ID.
     */
    public void deleteMedication(int medicationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATIONS, COLUMN_MEDICATION_ID + "=?", new String[]{String.valueOf(medicationId)});
        db.close();
    }
}


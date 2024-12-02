package com.example.medtrackerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.medtrackerapp.model.User;
import com.example.medtrackerapp.model.Medication;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "UserManager.db";

    // User table and column names
    private static final String TABLE_USER = "user";
    private static final String COLUMN_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_PROVIDER_EMAIL = "user_providerEmail";
    private static final String COLUMN_NAME = "user_name";           // New: user name
    private static final String COLUMN_DATE_OF_BIRTH = "user_dob";   // New: date of birth
    private static final String COLUMN_GENDER = "user_gender";       // New: gender

    // Medications table and column names
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String COLUMN_MEDICATION_ID = "medication_id";
    private static final String COLUMN_MEDICATION_NAME = "medication_name";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_DAYS_OF_WEEK = "days_of_week";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_INDEFINITE = "indefinite";

    // Adherence table and column names
    private static final String TABLE_ADHERENCE = "adherence";
    private static final String COLUMN_ADHERENCE_ID = "adherence_id";
    private static final String COLUMN_MEDICATION_ID_FK = "medication_id_fk";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_STATUS = "status";

    public String getTableUser() {
        return TABLE_USER;
    }

    public String getTableMedications() {
        return TABLE_MEDICATIONS;
    }

    public String getTableAdherence() {
        return TABLE_ADHERENCE;
    }

    // Getters for User Table Columns
    public String getColumnEmail() {
        return COLUMN_EMAIL;
    }

    public String getColumnUserPassword() {
        return COLUMN_USER_PASSWORD;
    }

    public String getColumnProviderEmail() {
        return COLUMN_PROVIDER_EMAIL;
    }

    public String getColumnName() {
        return COLUMN_NAME;
    }

    public String getColumnDateOfBirth() {
        return COLUMN_DATE_OF_BIRTH;
    }

    public String getColumnGender() {
        return COLUMN_GENDER;
    }

    public String getColumnMedicationId() {
        return COLUMN_MEDICATION_ID;
    }

    public String getColumnMedicationName() {
        return COLUMN_MEDICATION_NAME;
    }

    public String getColumnDosage() {
        return COLUMN_DOSAGE;
    }

    public String getColumnFrequency() {
        return COLUMN_FREQUENCY;
    }

    public String getColumnDaysOfWeek() {
        return COLUMN_DAYS_OF_WEEK;
    }

    public String getColumnEndDate() {
        return COLUMN_END_DATE;
    }

    public String getColumnIndefinite() {
        return COLUMN_INDEFINITE;
    }

    // Getters for Adherence Table Columns
    public String getColumnAdherenceId() {
        return COLUMN_ADHERENCE_ID;
    }

    public String getColumnMedicationIdFk() {
        return COLUMN_MEDICATION_ID_FK;
    }

    public String getColumnDate() {
        return COLUMN_DATE;
    }

    public String getColumnStatus() {
        return COLUMN_STATUS;
    }


    // SQL statement to create the user table
    private static final String USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_PROVIDER_EMAIL + " TEXT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_DATE_OF_BIRTH + " TEXT,"
            + COLUMN_GENDER + " TEXT" + ")";

    // SQL statements for creating other tables
    private static final String CREATE_MEDICATIONS_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS + "("
            + COLUMN_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_MEDICATION_NAME + " TEXT,"
            + COLUMN_DOSAGE + " INTEGER,"
            + COLUMN_FREQUENCY + " INTEGER,"
            + COLUMN_DAYS_OF_WEEK + " TEXT,"
            + COLUMN_END_DATE + " TEXT,"
            + COLUMN_INDEFINITE + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_EMAIL + ") REFERENCES " + TABLE_USER + "(" + COLUMN_EMAIL + "))";

    private static final String CREATE_ADHERENCE_TABLE = "CREATE TABLE " + TABLE_ADHERENCE + "("
            + COLUMN_ADHERENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MEDICATION_ID_FK + " INTEGER,"
            + COLUMN_STATUS + " TEXT," // Updated: Use TEXT for "Yes"/"No" instead of INTEGER
            + "FOREIGN KEY(" + COLUMN_MEDICATION_ID_FK + ") REFERENCES " + TABLE_MEDICATIONS + "(" + COLUMN_MEDICATION_ID + "))";

    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private static final String DROP_MEDICATIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_MEDICATIONS;
    private static final String DROP_ADHERENCE_TABLE = "DROP TABLE IF EXISTS " + TABLE_ADHERENCE;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE);
        db.execSQL(CREATE_MEDICATIONS_TABLE);
        db.execSQL(CREATE_ADHERENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_MEDICATIONS_TABLE);
        db.execSQL(DROP_ADHERENCE_TABLE);
        onCreate(db);
    }

    // User-related methods

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_PROVIDER_EMAIL, user.getProviderEmail());
        values.put(COLUMN_NAME, user.getName());                  // New: name
        values.put(COLUMN_DATE_OF_BIRTH, user.getDateOfBirth());  // New: date of birth
        values.put(COLUMN_GENDER, user.getGender());              // New: gender
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public boolean checkUser(String email) {
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    public boolean checkUser(String email, String password) {
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // Medication-related methods

    public void addMedication(String userEmail, String name, int dosage, int frequency, String daysOfWeek, String endDate, boolean indefinite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, userEmail);
        values.put(COLUMN_MEDICATION_NAME, name);
        values.put(COLUMN_DOSAGE, dosage);
        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_DAYS_OF_WEEK, daysOfWeek);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_INDEFINITE, indefinite ? 1 : 0);
        db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
    }

    /**
     * Deletes a specific medication by its ID.
     */
    public void deleteMedication(int medicationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATIONS, COLUMN_MEDICATION_ID + "=?", new String[]{String.valueOf(medicationId)});
        db.close();
    }

    public List<Medication> getMedicationsByUser(String userEmail) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {userEmail};
        Cursor cursor = db.query(TABLE_MEDICATIONS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Medication medication = new Medication(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAYS_OF_WEEK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INDEFINITE)) == 1
                );
                medications.add(medication);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return medications;
    }

    public boolean checkMedication(String name) {
        String[] columns = {COLUMN_MEDICATION_NAME};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_MEDICATION_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(TABLE_MEDICATIONS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // Adherence-related methods

    public void storeResponse(int medicationId, boolean response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MEDICATION_ID_FK, medicationId);
        values.put(COLUMN_STATUS, response ? "Yes" : "No"); // Convert boolean to "Yes"/"No"

        long rowId = db.insert(TABLE_ADHERENCE, null, values); // Capture the result for debugging

        if (rowId != -1) {
            Log.d("DatabaseHandler", "Response stored successfully: Medication ID = " + medicationId +
                    ", Response = " + (response ? "Yes" : "No"));
        } else {
            Log.e("DatabaseHandler", "Failed to store response for Medication ID = " + medicationId);
        }

        db.close();
    }

    public void logAdherence(int medicationId, String date, boolean taken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICATION_ID_FK, medicationId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STATUS, taken ? 1 : 0);
        db.insert(TABLE_ADHERENCE, null, values);
        db.close();
    }

    public List<Boolean> getAdherenceForLast30Days(int medicationId) {
        List<Boolean> adherenceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_ADHERENCE +
                " WHERE " + COLUMN_MEDICATION_ID_FK + " = ? " +
                "ORDER BY " + COLUMN_DATE + " DESC LIMIT 30";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(medicationId)});

        if (cursor.moveToFirst()) {
            do {
                adherenceList.add(cursor.getInt(0) == 1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return adherenceList;
    }
}


package com.example.medtrackerapp.database;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.medtrackerapp.model.User;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database version and name constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserManager.db";

    // Table and column names
    private static final String TABLE_USER = "user";
    private static final String COLUMN_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_PROVIDER_EMAIL = "user_providerEmail";

    // SQL statement to create the user table
    private static final String USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_EMAIL + " TEXT PRIMARY KEY,"  // Primary key: email
            + COLUMN_USER_PASSWORD + " TEXT,"
            + COLUMN_PROVIDER_EMAIL+ " TEXT" + ")";    // Password column

    // SQL statement to drop the user table if it exists
    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // Constructor to initialize the DatabaseHelper
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE);  // Executes the SQL to create the user table
    }

    // Called when the database version changes, to update the database schema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);  // Drops the existing user table
        onCreate(db);                 // Recreates the table by calling onCreate
    }

    // Method to add a new user to the database
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();  // Gets a writable database instance
        ContentValues values = new ContentValues();      // Stores key-value pairs for a row
        values.put(COLUMN_EMAIL, user.getEmail());  // Adds username to ContentValues
        values.put(COLUMN_USER_PASSWORD, user.getPassword());  // Adds password to ContentValues
        values.put(COLUMN_PROVIDER_EMAIL, user.getProviderEmail());  // Adds provider email to ContentValues

        db.insert(TABLE_USER, null, values);  // Inserts the new row in the user table
        db.close();  // Closes the database connection
    }

    // Method to check if a user with a specific username exists in the database
    public boolean checkUser(String email) {
        // Define the columns to return
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();  // Gets a readable database instance

        // Define the query criteria
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };  // Where clause arguments

        // Query the user table
        Cursor cursor = db.query(
                TABLE_USER,        // Table name
                columns,           // Columns to return
                selection,         // Where clause
                selectionArgs,     // Where clause arguments
                null, null, null   // GroupBy, Having, and OrderBy
        );

        int cursorCount = cursor.getCount();  // Count the results
        cursor.close();                       // Close the cursor
        db.close();                           // Close the database connection

        return cursorCount > 0;  // Returns true if a match is found
    }

    // Method to check if a user with a specific username and password exists in the database
    public boolean checkUser(String username, String password) {
        String[] columns = {COLUMN_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query criteria for both username and password
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        // Query the user table
        Cursor cursor = db.query(
                TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null, null, null
        );

        int cursorCount = cursor.getCount();  // Count the results
        cursor.close();
        db.close();

        return cursorCount > 0;  // Returns true if a match is found
    }

        // Method to retrieve the email based on username
        public String getEmailByUsername(String username) {
            String email = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    "users", // Replace with your table name
                    new String[]{"email"}, // Column to retrieve
                    "username = ?", // WHERE clause
                    new String[]{username}, // Arguments for WHERE clause
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                }
                cursor.close();
            }
            db.close();
            return email;
        }

        // Method to retrieve the password based on username
        public String getPasswordByUsername(String username) {
            String password = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    "users", // Replace with your table name
                    new String[]{"password"}, // Column to retrieve
                    "username = ?", // WHERE clause
                    new String[]{username}, // Arguments for WHERE clause
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                }
                cursor.close();
            }
            db.close();
            return password;
        }
    }


package com.example.routinetrackerwonderfulapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoutineDatabaseHelper extends SQLiteOpenHelper {
    // Database name and version
    public static final String DATABASE_NAME = "routine_tracker.db";
    public static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_ROUTINES = "routines";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_TARGET_END_TIME = "target_end_time";
    public static final String COLUMN_FREQUENCY = "frequency";
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_STREAK_COUNT = "streak_count";
    public static final String COLUMN_CREATION_TIME = "creation_time";

    // SQL create table command
    public static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_ROUTINES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    "duration INTEGER, " +
                    "target_end_time TEXT, " +
                    "frequency TEXT, " +
                    "is_completed INTEGER, " +
                    "streak_count INTEGER, " +
                    COLUMN_CREATION_TIME + " INTEGER);";

    // Constructor
    public RoutineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table and create a new one if the database is upgraded
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINES);
        onCreate(db);
    }


    // Retrieve all the routines from the database
    public List<Habit> getAllHabits() {
        List<Habit> habitList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_ROUTINES, null, null, null, null, null, COLUMN_CREATION_TIME + " DESC")) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                    String targetEndTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET_END_TIME));
                    String frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY));
                    boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;
                    int streakCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_COUNT));
                    long creationTimeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATION_TIME));
                    Date creationTime = new Date(creationTimeInMillis);

                    Habit habit = new Habit(id, title, description, duration, targetEndTime, frequency, isCompleted, streakCount, creationTime);

                    habitList.add(habit);
                } while (cursor.moveToNext()); // Move the cursor to the next row until all rows are processed
            }

        }
        return habitList;
    }

    public long addHabit(Habit habit) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, habit.getTitle());
        values.put(COLUMN_DESCRIPTION, habit.getDescription());
        values.put("duration", habit.getDuration());
        values.put("target_end_time", habit.getTargetEndTime());
        values.put("frequency", habit.getFrequency());
        values.put("is_completed", habit.isCompleted() ? 1 : 0);
        values.put("streak_count", habit.getStreakCount());
        values.put(COLUMN_CREATION_TIME, habit.getCreationTime().getTime());

        long id = db.insert(TABLE_ROUTINES, null, values);
        db.close();
        return id;
    }

    public int updateHabit(Habit habit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, habit.getTitle());
        values.put(COLUMN_DESCRIPTION, habit.getDescription());
        values.put(COLUMN_DURATION, habit.getDuration());
        values.put(COLUMN_TARGET_END_TIME, habit.getTargetEndTime());
        values.put(COLUMN_FREQUENCY, habit.getFrequency());
        values.put(COLUMN_IS_COMPLETED, habit.isCompleted() ? 1 : 0);
        values.put(COLUMN_STREAK_COUNT, habit.getStreakCount());
        values.put(COLUMN_CREATION_TIME, habit.getCreationTime().getTime());

        // Update the habit row and return the number of rows affected
        return db.update(TABLE_ROUTINES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(habit.getId())});
    }

    // Method to delete a habit from the database using its ID
    public int deleteHabit(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        int rowsAffected = db.delete(TABLE_ROUTINES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d("RoutineDatabaseHelper", "Rows affected by delete: " + rowsAffected);
        db.close();

        return rowsAffected;
    }

    // Method to get a single habit by its ID
    public Habit getHabitById(int givenId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINES, null, COLUMN_ID + " = ?", new String[]{String.valueOf(givenId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
            String targetEndTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARGET_END_TIME));
            String frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY));
            boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;
            int streakCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK_COUNT));
            long creationTimeInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATION_TIME));
            Date creationTime = new Date(creationTimeInMillis);

            Habit habit = new Habit(id, title, description, duration, targetEndTime, frequency, isCompleted, streakCount, creationTime);

            cursor.close(); // Close the cursor when done

            return habit;
        }
        return null;
    }

}

package com.example.routinetrackerwonderfulapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class RoutineDatabaseHelperTest {
    private RoutineDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        dbHelper = new RoutineDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        clearDatabase();
    }

    @After
    public void tearDown() {
        clearDatabase();
        dbHelper.close();
        db = null;    // set the database object to null to avoid accidental reuse
    }

    /**
     * Helper method to clear the database by deleting all rows in the routines table.
     */
    private void clearDatabase() {
        // Only if the database is open before before attempting to delete
        if (db != null && db.isOpen()) {
            // Clear all rows from the routines table
            db.delete(RoutineDatabaseHelper.TABLE_ROUTINES, null, null);
        }
    }

    @Test
    public void testAddHabit() {
        Habit habit = new Habit("TB", "This is a test description.");
        long id = dbHelper.addHabit(habit);
        assertNotEquals("Habit ID should not be -1 after insertion", -1, id);
    }

    @Test
    public void testGetAllHabits() {
        // Add two test habits
        Habit habit1 = new Habit("Test Habit 1", "Description 1");
        Habit habit2 = new Habit("Test Habit 2", "Description 2");
        dbHelper.addHabit(habit1);
        dbHelper.addHabit(habit2);

        // Retrieve all habits
        List<Habit> habits = dbHelper.getAllHabits();

        assertNotNull(habits);

        assertEquals("There should be 2 habits in the list", 2, habits.size());

        // Check if the two habits match
        assertEquals("Test Habit 1", habits.get(0).getTitle());
        assertEquals("Description 1", habits.get(0).getDescription());

        // Check if the second habit matches
        assertEquals("Test Habit 2", habits.get(1).getTitle());
        assertEquals("Description 2", habits.get(1).getDescription());
    }

    @Test
    public void testGetHabitById() {
        // Add a habit and retrieve it by ID
        Habit habit = new Habit("Habit something", "Description something");
        long id = dbHelper.addHabit(habit);
        assertNotEquals("Habit ID should not be -1 after already added", -1, id);

        // Retrieve by ID
        Habit retrievedHabit = dbHelper.getHabitById((int) id);
        assertNotNull("Retrieved habit should not be null", retrievedHabit);
        assertEquals("Habit something", retrievedHabit.getTitle());
        assertEquals("Description something", retrievedHabit.getDescription());
    }

    @Test
    public void testUpdateHabit() {
        Habit habit = new Habit("Test habit", "Description");
        long id = dbHelper.addHabit(habit);
        assertNotEquals("Habit ID should not be -1 after insertion", -1, id);

        // Update the habit's details
        Habit updatedHabit = new Habit("Updated habit", "Updated description");
        updatedHabit.setId((int) id);
        int rowsAffected = dbHelper.updateHabit(updatedHabit);
        assertEquals("One row should be affected", 1, rowsAffected);

        // Verify the update
        Habit habitFromDb = dbHelper.getHabitById((int) id);
        assertNotNull("Updated habit should not be null", habitFromDb);
        assertEquals("Updated habit", habitFromDb.getTitle());
        assertEquals("Updated description", habitFromDb.getDescription());
    }

    @Test
    public void testDeleteHabit() {
        // Add a habit first
        // Add a habit first
        Habit habit = new Habit("Test Habit", "Description");
        long id = dbHelper.addHabit(habit);
        assertNotEquals("Habit ID should not be -1 after insertion", -1, id);

        // Delete the habit
        int rowDeleted = dbHelper.deleteHabit((int) id);
        assertEquals("One row should be deleted", 1, rowDeleted);

        // Verify the deletion
        Habit deletedHabit = dbHelper.getHabitById((int) id);
        assertNull("Habit should not be found after deletion", deletedHabit);

    }

}

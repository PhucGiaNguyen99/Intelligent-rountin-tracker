package com.example.routinetrackerwonderfulapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest=Config.NONE)  // Add this to ignore the manifest file warning
public class MainActivityTest {
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        // Initialize the activity using Robolectric
        mainActivity = new MainActivity();
        mainActivity.habitList = new ArrayList<>(); // Mock or initialize an empty list
    }

    @Test
    public void testIsDuplicateTitle_NoDuplicates() {
        // Setup: Add a habit
        Habit habit1 = new Habit("Exercise", "Morning exercise");
        mainActivity.habitList.add(habit1);

        // Test: Check for a different title
        assertFalse(mainActivity.isDuplicateTitle("Read")); // Should return false
    }

    @Test
    public void testIsDuplicateTitle_WithDuplicate() {
        // Setup: Add a habit
        Habit habit1 = new Habit("Exercise", "Morning exercise");
        mainActivity.habitList.add(habit1);

        // Test: Check for the same title
        assertTrue(mainActivity.isDuplicateTitle("Exercise")); // Should return true
    }
}

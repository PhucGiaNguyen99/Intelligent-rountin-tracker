package com.example.routinetrackerwonderfulapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.media.MediaRouter2;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HabitAdapter.HabitActionListener {
    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    List<Habit> habitList;
    private RoutineDatabaseHelper routineDatabaseHelper;
    private EditText habitTitleInput;
    private EditText habitdescriptionInput;
    private Button addHabitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        routineDatabaseHelper = new RoutineDatabaseHelper(this);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the habit list and adapter
        habitList = new ArrayList<>();

        // Load the habits from the database
        loadHabitsFromDatabase();

        habitAdapter = new HabitAdapter(habitList);
        recyclerView.setAdapter(habitAdapter);

        // Set the HabitActionListener for the habit adapter
        habitAdapter.setHabitActionListener(new HabitAdapter.HabitActionListener() {
            @Override
            public void onEditClick(Habit habit, int position) {
                openEditHabitDialog(habit, position);  // Call a function to handle editing
            }

            @Override
            public void onDeleteClick(int position) {
                confirmDeleteHabit(position);  // Call a function to handle deletion
            }

            @Override
            public void updateHabitInDatabase(Habit habit) {

            }

            @Override
            public void deleteHabitFromDatabase(int habitId) {

            }
        });

        // Initialize the FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.add_habit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddHabitDialog();
            }
        });
    }

    // Method to load habits from the database
    private void loadHabitsFromDatabase() {
        List<Habit> habitsFromDb = routineDatabaseHelper.getAllHabits();

        // Clear the current habit list to avoid duplicates
        habitList.clear();

        // Add all the retrieved habits to the local habit list
        if (habitsFromDb != null && !habitsFromDb.isEmpty()) {
            habitList.addAll(habitsFromDb);
        }

        // Notify the adapter that the data has been changed
        if (habitAdapter != null) {
            habitAdapter.notifyDataSetChanged();
        }
    }

    // Method to add a habit using user input
    private void addHabit() {
        String habitTitle = habitTitleInput.getText().toString().trim();
        String habitDescription = habitdescriptionInput.getText().toString().trim();

        if (!habitTitle.isEmpty() && !habitDescription.isEmpty()) {
            Habit newHabit = new Habit(habitTitle, habitDescription);

            // Save it to the database using the addHabit method from RoutineDatabaseHelper
            routineDatabaseHelper.addHabit(newHabit);

            // Clear the input fields after adding the habit
            habitTitleInput.setText("");
            habitdescriptionInput.setText("");

            // Update the UI or notify the adapter if having one
            habitList.add(newHabit);
        }
    }

    // Function to check for duplicate titles
    boolean isDuplicateTitle(String title) {
        for (Habit habit : habitList) {
            if (habit.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    private void openAddHabitDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add New Habit");

            // Inflate the custom layout for the dialog
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);
            builder.setView(dialogView);

            // Get references to the EditText fields
            final EditText habitTitleInput = dialogView.findViewById(R.id.habit_title_input);
            final EditText habitDescriptionInput = dialogView.findViewById(R.id.habit_description_input);

            Spinner hourSpinner = dialogView.findViewById(R.id.duration_hour_spinner);
            Spinner minuteSpinner = dialogView.findViewById(R.id.duration_minute_spinner);
            Spinner daySpinner = dialogView.findViewById(R.id.target_end_day_spinner);
            Spinner monthSpinner = dialogView.findViewById(R.id.target_end_month_spinner);
            Spinner yearSpinner = dialogView.findViewById(R.id.target_end_year_spinner);

            // Populate spinners
            ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(0, 23));
            hourSpinner.setAdapter(hourAdapter);
            ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(0, 59));
            minuteSpinner.setAdapter(minuteAdapter);
            ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 31));
            daySpinner.setAdapter(dayAdapter);
            ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 12));
            monthSpinner.setAdapter(monthAdapter);
            ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(getCurrentYear(), getCurrentYear() + 5));
            yearSpinner.setAdapter(yearAdapter);

            // Set up the dialog buttons
            builder.setPositiveButton("Add", null); // Prevent auto-dismiss
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();   // Close the dialog without adding a habit
                }
            });

            // Show the dialog
            final AlertDialog dialog = builder.create();
            dialog.show();

            // Overwrite the positive button click event
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String habitTitle = habitTitleInput.getText().toString();
                    String habitDescription = habitDescriptionInput.getText().toString();

                    // Check if the input title is not empty
                    if (habitTitle.isEmpty()) {
                        habitTitleInput.setError("Title cannot be empty");
                    }
                    // Check if the input title is duplicate
                    else if (isDuplicateTitle(habitTitle)) {
                        habitTitleInput.setError("A routine with this title already exists.");
                    } else {
                        int durationHours = Integer.parseInt(hourSpinner.getSelectedItem().toString());
                        int durationMinutes = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
                        int totalDurationMinutes = (durationHours * 60) + durationMinutes;

                        int day = Integer.parseInt(daySpinner.getSelectedItem().toString());
                        int month = Integer.parseInt(daySpinner.getSelectedItem().toString());
                        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

                        Calendar endDate = Calendar.getInstance();
                        endDate.set(year, month - 1, day);

                        Habit newHabit = new Habit(habitTitle, habitDescription);
                        newHabit.setDuration(totalDurationMinutes);
                        newHabit.setTargetEndTime(endDate.getTime().toString());
                        newHabit.setStreakCount(0);
                        newHabit.setCompleted(false);

                        habitList.add(newHabit);
                        Log.d("MainActivity", "Habit added: " + newHabit.getTitle() + " | List size: " + habitList.size());
                        habitAdapter.setHabitList(habitList);

                        // Save to the database
                        routineDatabaseHelper.addHabit(newHabit);

                        // Notify the adapter to refresh the RecyclerView
                        habitAdapter.notifyDataSetChanged();

                        // Dismiss the dialog after successful addition
                        dialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", "Error in openAddHabitDialog: " + e.getMessage());
        }
    }

    private void openEditHabitDialog(Habit habit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Habit");

        // Inflate the dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);
        builder.setView(dialogView);

        // Initialize EditText fields
        final EditText habitTitleInput = dialogView.findViewById(R.id.habit_title_input);
        final EditText habitDescriptionInput = dialogView.findViewById(R.id.habit_description_input);

        // Pre-fill current habit details
        habitTitleInput.setText(habit.getTitle());
        habitDescriptionInput.setText(habit.getDescription());

        Spinner hourSpinner = dialogView.findViewById(R.id.duration_hour_spinner);
        Spinner minuteSpinner = dialogView.findViewById(R.id.duration_minute_spinner);
        Spinner daySpinner = dialogView.findViewById(R.id.target_end_day_spinner);
        Spinner monthSpinner = dialogView.findViewById(R.id.target_end_month_spinner);
        Spinner yearSpinner = dialogView.findViewById(R.id.target_end_year_spinner);

        // Populate spinners
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(0, 23));
        hourSpinner.setAdapter(hourAdapter);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(0, 59));
        minuteSpinner.setAdapter(minuteAdapter);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 31));
        daySpinner.setAdapter(dayAdapter);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(1, 12));
        monthSpinner.setAdapter(monthAdapter);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, generateNumberList(getCurrentYear(), getCurrentYear() + 5));
        yearSpinner.setAdapter(yearAdapter);

        // Set up the dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = habitTitleInput.getText().toString();
            String newDescription = habitDescriptionInput.getText().toString();

            // Update habit and notify adapter
            habit.setTitle(newTitle);
            habit.setDescription(newDescription);
            habitAdapter.notifyItemChanged(position); // Update specific item
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();   // Close the dialog without making changes
            }
        });

        // Show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Overwrite the positive button click event to save changes
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTitle = habitTitleInput.getText().toString();
                String updatedDescription = habitDescriptionInput.getText().toString();

                // Basic validation
                if (updatedTitle.isEmpty()) {
                    habitTitleInput.setError("Title cannot be empty");
                } else if (isDuplicateTitle(updatedTitle) && !habit.getTitle().equalsIgnoreCase(updatedTitle)) {
                    habitTitleInput.setError("A habit with this title already exists.");
                } else {
                    int durationHours = Integer.parseInt(hourSpinner.getSelectedItem().toString());
                    int durationMinutes = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
                    int totalDurationMinutes = (durationHours * 60) + durationMinutes;

                    int day = Integer.parseInt(daySpinner.getSelectedItem().toString());
                    int month = Integer.parseInt(daySpinner.getSelectedItem().toString());
                    int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

                    Calendar endDate = Calendar.getInstance();
                    endDate.set(year, month - 1, day);

                    Habit newHabit = new Habit(updatedTitle, updatedDescription);
                    newHabit.setDuration(totalDurationMinutes);
                    newHabit.setTargetEndTime(endDate.getTime().toString());
                    newHabit.setStreakCount(0);
                    newHabit.setCompleted(false);

                    // Update the habit with new values
                    //habit.setTitle(updatedTitle);
                    //habit.setDescription(updatedDescription);

                    // Update the list and notify the adapter
                    habitList.set(position, habit);
                    habitAdapter.notifyItemChanged(position);

                    // Update the database
                    routineDatabaseHelper.updateHabit(habit);
                    dialog.dismiss();
                }
            }
        });
    }

    // Function to get the current year
    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    // Function to generate a list of numbers from `start` to `end`
    private List<String> generateNumberList(int start, int end) {
        List<String> numberList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            numberList.add(String.valueOf(i)); // Convert each number to string and add to list
        }
        return numberList;
    }


    private void confirmDeleteHabit(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Habit");
        builder.setMessage("Are you sure you want to delete this habit?");

        // Set up the dialog buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Habit habitToDelete = habitList.get(position);
                int habitId = habitToDelete.getId();

                // Remove the habit from the list and notify the adapter
                habitList.remove(position);
                habitAdapter.notifyItemRemoved(position);

                routineDatabaseHelper.deleteHabit(habitId);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cancel deletion
            }
        });

        // Show the dialog
        builder.create().show();
    }

    // Implement methods of HabitActionListener
    @Override
    public void onEditClick(Habit habit, int position) {
        openEditHabitDialog(habit, position);
    }

    @Override
    public void onDeleteClick(int position) {
        Habit habitToDelete = habitList.get(position);

        // Remove the habit from the list and notify the adapter
        habitList.remove(position);
        habitAdapter.notifyItemRemoved(position);

        routineDatabaseHelper.deleteHabit(habitToDelete.getId());
    }

    @Override
    public void updateHabitInDatabase(Habit habit) {

    }

    @Override
    public void deleteHabitFromDatabase(int habitId) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
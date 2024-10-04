package com.example.routinetrackerwonderfulapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    List<Habit> habitList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the habit list and adapter
        habitList = new ArrayList<>();
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

    // Function to check for duplicate titles
    boolean isDuplicateTitle(String title) {
        for (Habit habit: habitList){
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
                    }
                    else {
                        Habit newHabit = new Habit(habitTitle, habitDescription);
                        habitList.add(newHabit);
                        Log.d("MainActivity", "Habit added: " + newHabit.getTitle() + " | List size: " + habitList.size());
                        habitAdapter.setHabitList(habitList);

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
                    // Update the habit with new values
                    habit.setTitle(updatedTitle);
                    habit.setDescription(updatedDescription);

                    // Update the list and notify the adapter
                    habitList.set(position, habit);
                    habitAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                }
            }
        });
    }

    private void confirmDeleteHabit(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Habit");
        builder.setMessage("Are you sure you want to delete this habit?");

        // Set up the dialog buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the habit from the list and notify the adapter
                habitList.remove(position);
                habitAdapter.notifyItemRemoved(position);
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






}
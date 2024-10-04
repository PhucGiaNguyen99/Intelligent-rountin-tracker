package com.example.routinetrackerwonderfulapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private HabitActionListener listener;

    public HabitAdapter(List<Habit> habitList) {
        this.habitList = habitList;
    }

    // Inflate the item layout for each habit
    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    // Bind data to the views (where the data is assigned to the UI elements)
    @Override
    public void onBindViewHolder(@NonNull HabitAdapter.HabitViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();

        // Ensure that the current position is valid
        if (currentPosition != RecyclerView.NO_POSITION) {
            Habit habit = habitList.get(currentPosition);

            // Bind the habit data to the UI elements
            holder.habitTitle.setText(habit.getTitle());
            holder.habitDescription.setText(habit.getDescription());

            // Format and display the creation time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(habit.getCreationTime());
            holder.habitCreationTime.setText("Created: " + formattedDate);

            // Bind the click listeners for edit and delete actions
            holder.editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(habit, currentPosition);  // Use currentPosition instead of position
                }
            });

            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(currentPosition);  // Use currentPosition instead of position
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    // Define the ViewHolder class, which holds the views for each habit item
    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitTitle, habitDescription, habitCreationTime;
        Button editButton, deleteButton;
        // Constructor: Bind the views from habit_item.xml

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTitle = itemView.findViewById(R.id.habit_title);
            habitDescription = itemView.findViewById(R.id.habit_description);
            habitCreationTime = itemView.findViewById(R.id.habit_creation_time);
            editButton= itemView.findViewById(R.id.edit_button);
            deleteButton= itemView.findViewById(R.id.delete_button);
        }
    }

    public void setHabitList(List<Habit> updatedHabitList) {
        this.habitList = updatedHabitList;  // Update the list reference
        notifyDataSetChanged();  // Notify the adapter of data changes
    }

    public void setHabitActionListener(HabitActionListener listener) {
        this.listener = listener;
    }

    public interface HabitActionListener {
        void onEditClick(Habit habit, int position);
        void onDeleteClick(int position);
    }

}

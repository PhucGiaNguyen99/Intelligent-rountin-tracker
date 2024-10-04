package com.example.routinetrackerwonderfulapp;

import java.util.Date;

public class Habit {
    private String title;
    private String description;
    private int duration;
    private String targetEndTime;
    private String frequency;
    private boolean isCompleted;    // Check if the habit is completed for today
    private int streakCount;    // Later
    private Date creationTime;


    public Habit(String title, String description) {
        this.title = title;
        this.description = description;
        this.duration = 0;
        this.targetEndTime = "";
        this.frequency = "";
        this.isCompleted = false;
        this.streakCount = 0;
        this.creationTime = new Date(); // Automatically set the current date and time
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTargetEndTime() {
        return targetEndTime;
    }

    public void setTargetEndTime(String targetEndTime) {
        this.targetEndTime = targetEndTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}

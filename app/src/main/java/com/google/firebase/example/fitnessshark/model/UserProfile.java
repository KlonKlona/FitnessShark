package com.google.firebase.example.fitnessshark.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * UserProfile POJO.
 */
@IgnoreExtraProperties
public class UserProfile {
    private String userId;
    private Goal goal;
    private DifficultyLevel experience;
    private int prefDuration;
    private int prefDaysWeek;
    private Workout currentWorkout;

    private int deadliftMax;
    private int squatMax;
    private int benchMax;
    private int pressMax;

    public UserProfile(String userId, Goal goal, DifficultyLevel experience, int prefDuration,
                       int prefDaysWeek, int deadliftMax,
                       int squatMax, int benchMax, int pressMax) {
        this.userId = userId;
        this.goal = goal;
        this.experience = experience;
        this.prefDuration = prefDuration;
        this.prefDaysWeek = prefDaysWeek;
        this.currentWorkout = null;
        this.deadliftMax = deadliftMax;
        this.squatMax = squatMax;
        this.benchMax = benchMax;
        this.pressMax = pressMax;
    }

    public int getDeadliftMax() {
        return deadliftMax;
    }

    public void setDeadliftMax(int deadliftMax) {
        this.deadliftMax = deadliftMax;
    }

    public int getSquatMax() {
        return squatMax;
    }

    public void setSquatMax(int squatMax) {
        this.squatMax = squatMax;
    }

    public int getBenchMax() {
        return benchMax;
    }

    public void setBenchMax(int benchMax) {
        this.benchMax = benchMax;
    }

    public int getPressMax() {
        return pressMax;
    }

    public void setPressMax(int pressMax) {
        this.pressMax = pressMax;
    }

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public DifficultyLevel getExperience() {
        return experience;
    }

    public void setExperience(DifficultyLevel experience) {
        this.experience = experience;
    }

    public int getPrefDuration() {
        return prefDuration;
    }

    public void setPrefDuration(int prefDuration) {
        this.prefDuration = prefDuration;
    }

    public int getPrefDaysWeek() {
        return prefDaysWeek;
    }

    public void setPrefDaysWeek(int prefDaysWeek) {
        this.prefDaysWeek = prefDaysWeek;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Workout getCurrentWorkout() {
        return currentWorkout;
    }

    public void setCurrentWorkout(Workout currentWorkout) {
        this.currentWorkout = currentWorkout;
    }
}

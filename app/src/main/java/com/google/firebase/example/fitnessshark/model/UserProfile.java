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
    private WorkoutPlan currentWorkoutPlan;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserProfile(String userId, Goal goal, DifficultyLevel experience, int prefDuration,
                       int prefDaysWeek) {
        this.userId = userId;
        this.goal = goal;
        this.experience = experience;
        this.prefDuration = prefDuration;
        this.prefDaysWeek = prefDaysWeek;
        this.currentWorkoutPlan = null;
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

    public WorkoutPlan getCurrentWorkoutPlan() {
        return currentWorkoutPlan;
    }

    public void setCurrentWorkoutPlan(WorkoutPlan currentWorkoutPlan) {
        this.currentWorkoutPlan = currentWorkoutPlan;
    }
}

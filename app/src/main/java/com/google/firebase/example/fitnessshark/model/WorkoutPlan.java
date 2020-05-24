package com.google.firebase.example.fitnessshark.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * WorkoutPlan POJO.
 */
@IgnoreExtraProperties
public class WorkoutPlan {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_DAYS_WEEK = "daysWeek";
    public static final String FIELD_WORKOUTS = "workouts";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    private String name;
    private DifficultyLevel difficulty;
    private WorkoutCategory category;
    private int duration;
    private int daysWeek;
    private int numRatings;
    private double avgRating;
    private String photo;
    private ArrayList<Workout> workouts;

    public WorkoutPlan() {}

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public WorkoutCategory getCategory() {
        return category;
    }

    public void setCategory(WorkoutCategory category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDaysWeek() {
        return daysWeek;
    }

    public void setDaysWeek(int daysWeek) {
        this.daysWeek = daysWeek;
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    public void addWorkout(Workout workout) {
        this.workouts.add(workout);
    }

    public WorkoutPlan(String name, DifficultyLevel difficulty, WorkoutCategory category,
                       int duration, int daysWeek, ArrayList<Workout> workouts) {
        this.name = name;
        this.difficulty = difficulty;
        this.category = category;
        this.duration = duration;
        this.daysWeek = daysWeek;
        this.workouts = workouts;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
        this.workouts = null;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}

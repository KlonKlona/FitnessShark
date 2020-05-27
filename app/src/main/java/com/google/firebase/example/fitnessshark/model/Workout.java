package com.google.firebase.example.fitnessshark.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.type.DayOfWeek;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Workout {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_EXERCISES = "exercises";

    public Workout(String name, ArrayList<Exercise> exercises,
                   ArrayList<DayOfWeek> daysOfWeek, DifficultyLevel difficulty, WorkoutCategory category,
                   int sets, int numExercises, int duration, String photo) {
        this.name = name;
        this.exercises = exercises;
        this.daysOfWeek = daysOfWeek;
        this.difficulty = difficulty;
        this.category = category;
        this.sets = sets;
        this.numExercises = numExercises;
        this.duration = duration;
        this.photo = photo;
    }

    public Workout() {}

    public Workout(String name) {
        this.name = name;
        this.exercises = new ArrayList<Exercise>();
        this.daysOfWeek = new ArrayList<DayOfWeek>();
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exerciseArrayList) {
        this.exercises = exercises;
    }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public ArrayList<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(ArrayList<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

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

    public int getNumExercises() {
        return numExercises;
    }

    public void setNumExercises(int numExercises) {
        this.numExercises = numExercises;
    }

    private String name;
    private ArrayList<Exercise> exercises;
    private ArrayList<DayOfWeek> daysOfWeek;
    private DifficultyLevel difficulty;
    private WorkoutCategory category;
    private int sets;
    private int numExercises;
    private int duration;
    private String photo;
//    private Impression impression;
}

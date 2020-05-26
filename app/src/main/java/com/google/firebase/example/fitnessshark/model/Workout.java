package com.google.firebase.example.fitnessshark.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.type.DayOfWeek;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Map;

@IgnoreExtraProperties
public class Workout {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_DAYS_WEEK = "daysWeek";
    public static final String FIELD_WORKOUTS = "workouts";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    public Workout(String name, Map<Exercise, Triplet<Integer, Integer, Double>> exercises,
                   DayOfWeek dayOfWeek, DifficultyLevel difficulty, WorkoutCategory category,
                   int sets, int numExercises, int duration, String photo) {
        this.name = name;
        this.exercises = exercises;
        this.dayOfWeek = dayOfWeek;
        this.difficulty = difficulty;
        this.category = category;
        this.sets = sets;
        this.numExercises = numExercises;
        this.duration = duration;
        this.photo = photo;
    }

    public Workout() {}

    public Map<Exercise, Triplet<Integer, Integer, Double>> getExercises() {
        return exercises;
    }

    public void setExercises(Map<Exercise, Triplet<Integer, Integer, Double>> exercises) {
        this.exercises = exercises;
    }

    public void addExercise(Map<Exercise, Triplet<Integer, Integer, Double>> exerciseTripletMap) {
        this.exercises = exerciseTripletMap;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public int getNumExercises() {
        return numExercises;
    }

    public void setNumExercises(int numExercises) {
        this.numExercises = numExercises;
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


    private String name;
    private Map<Exercise, Triplet<Integer, Integer, Double>> exercises;
    private DayOfWeek dayOfWeek;
    private DifficultyLevel difficulty;
    private WorkoutCategory category;
    private int sets;
    private int numExercises;
    private int duration;
    private String photo;
//    private Impression impression;
}

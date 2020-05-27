package com.google.firebase.example.fitnessshark.util;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.example.fitnessshark.model.Category;
import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.Equipment;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.Goal;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.example.fitnessshark.model.WorkoutCategory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.DayOfWeek;

import org.javatuples.Triplet;


public class WorkoutBuilder {
    private static final String TAG = "WorkoutBuilder";
    private static ArrayList<Exercise> fetchedExercises;

    private String name;
    private ArrayList<Exercise> exercises;
    private ArrayList<DayOfWeek> daysOfWeek;

    public static final HashMap<DifficultyLevel, Double> difficultyMultiplier = new HashMap<>();
    static {
        difficultyMultiplier.put(DifficultyLevel.ELITE, 1.0);
        difficultyMultiplier.put(DifficultyLevel.ADVANCED, 0.95);
        difficultyMultiplier.put(DifficultyLevel.INTERMEDIATE, 0.9);
        difficultyMultiplier.put(DifficultyLevel.AMATEUR, 0.85);
        difficultyMultiplier.put(DifficultyLevel.BEGINNER, 0.8);
    }

    public static final HashMap<Goal, Integer> goalRepetitionsRange = new HashMap<>();
    static {
        goalRepetitionsRange.put(Goal.ENDURANCE, 20);
        goalRepetitionsRange.put(Goal.BUILD, 12);
        goalRepetitionsRange.put(Goal.STRENGTH, 4);
        goalRepetitionsRange.put(Goal.LOSE, 8);
        goalRepetitionsRange.put(Goal.MAINTAIN, 10);
    }

    public static final HashMap<Integer, Integer> durationSets = new HashMap<>();
    static {
        durationSets.put(30, 1);
        durationSets.put(45, 2);
        durationSets.put(60, 3);
        durationSets.put(75, 4);
        durationSets.put(90, 5);
        durationSets.put(120, 6);
    }

    public static final HashMap<Integer, Integer> durationTotalExercises = new HashMap<>();
    static {
        durationSets.put(30, 3);
        durationSets.put(45, 4);
        durationSets.put(60, 4);
        durationSets.put(75, 5);
        durationSets.put(90, 5);
        durationSets.put(120, 6);
    }

    public static final HashMap<Goal, Double> goalMultiplier = new HashMap<>();
    static {
        goalMultiplier.put(Goal.ENDURANCE, 0.6);
        goalMultiplier.put(Goal.BUILD, 0.75);
        goalMultiplier.put(Goal.MAINTAIN, 0.7);
        goalMultiplier.put(Goal.STRENGTH, 0.85);
        goalMultiplier.put(Goal.LOSE, 0.8);
    }

    public WorkoutBuilder name(String name) {
        this.name = name;
        return this;
    }

    public WorkoutBuilder daysOfWeek(ArrayList<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
        return this;
    }

    public WorkoutBuilder exercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
        return this;
    }

    public Workout build() {
        if (name.isEmpty()) {
            throw new IllegalStateException("Name cannot be empty");
        }

        Workout workout = new Workout();
        workout.setName(this.name);
        workout.setExercises(this.exercises);
        workout.setDaysOfWeek(this.daysOfWeek);
        return workout;
    }

    public static WorkoutBuilder builder() {
        return new WorkoutBuilder();
    }

    public static void addExercises(FirebaseFirestore firestore, Category category) {
        firestore.collection("exercises")
                .whereEqualTo("category", category)
                .whereArrayContains("equipment", Equipment.BARBELL)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No matching exercises!");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                fetchedExercises.add(document.toObject(Exercise.class));
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

}
package com.google.firebase.example.fitnessshark.model;

public enum WorkoutCategory {
    POWER (4),
    STRENGTH (3),
    HYPERTROPHY (2),
    ENDURANCE (1)
    ;

    private final int workoutCategoryCode;

    private WorkoutCategory(int _workoutCategoryCode) {
        this.workoutCategoryCode = _workoutCategoryCode;
    }
}

package com.google.firebase.example.fitnessshark.model;

public enum Goal {
    BUILD   (5),
    LOSE    (4),
    MAINTAIN    (3),
    STRENGTH (2),
    ENDURANCE (1)
    ;

    private final int goalCode;

    private Goal(int _goalCode) {
        this.goalCode = _goalCode;
    }
}

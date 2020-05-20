package com.google.firebase.example.fitnessshark.model;

public enum DifficultyLevel {
    ELITE   (5),
    ADVANCED    (4),
    INTERMEDIATE    (3),
    AMATEUR (2),
    BEGINNER (1)
    ;

    private final int difficultyLevelCode;

    private DifficultyLevel(int _difficultyLevelCode) {
        this.difficultyLevelCode = _difficultyLevelCode;
    }
}

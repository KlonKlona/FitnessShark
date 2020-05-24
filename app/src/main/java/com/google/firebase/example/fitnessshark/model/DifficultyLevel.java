package com.google.firebase.example.fitnessshark.model;

import java.util.HashMap;
import java.util.Map;

public enum DifficultyLevel {
    ELITE   (1),
    ADVANCED    (2),
    INTERMEDIATE    (3),
    AMATEUR (4),
    BEGINNER (5)
    ;

    private final int difficultyLevelCode;
    private static Map map = new HashMap<>();

    private DifficultyLevel(int _difficultyLevelCode) {
        this.difficultyLevelCode = _difficultyLevelCode;
    }

    static {
        for (DifficultyLevel difficultyLevelCode : DifficultyLevel.values()) {
            map.put(difficultyLevelCode.difficultyLevelCode, difficultyLevelCode);
        }
    }

    public static DifficultyLevel valueOf(int difficultyLevelCode) {
        return (DifficultyLevel) map.get(difficultyLevelCode);
    }

    public int getDifficultyLevelCode() {
        return difficultyLevelCode;
    }
}

package com.google.firebase.example.fitnessshark.model;

import java.util.HashMap;
import java.util.Map;

public enum Goal {
    BUILD   (1),
    LOSE    (2),
    MAINTAIN    (3),
    STRENGTH (4),
    ENDURANCE (5)
    ;

    private final int goalCode;
    private static Map map = new HashMap<>();

    private Goal(int _goalCode) {
        this.goalCode = _goalCode;
    }

    static {
        for (Goal goalCode : Goal.values()) {
            map.put(goalCode.goalCode, goalCode);
        }
    }

    public static Goal valueOf(int goalCode) {
        return (Goal) map.get(goalCode);
    }

    public int getGoalCode() {
        return goalCode;
    }
}

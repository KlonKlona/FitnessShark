package com.google.firebase.example.fitnessshark.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public enum Equipment {
    @SerializedName("1") BARBELL   (1),
    @SerializedName("3") DUMBBELL    (3),
    @SerializedName("6") PULLUPBAR    (6),
    @SerializedName("7") NONE (7),
    @SerializedName("8") BENCH (8),
    @SerializedName("9") INCLINEBENCH (9),
    @SerializedName("10") KETTLEBELL (10)
    ;

    private final int equipmentCode;
    private static Map map = new HashMap<>();

    private Equipment(int _equipmentCode) {
        this.equipmentCode = _equipmentCode;
    }

    static {
        for (Equipment equipmentCode : Equipment.values()) {
            map.put(equipmentCode.equipmentCode, equipmentCode);
        }
    }

    public static Equipment valueOf(int equipmentCode) {
        return (Equipment) map.get(equipmentCode);
    }

    public int getEquipmentCode() {
        return equipmentCode;
    }
}

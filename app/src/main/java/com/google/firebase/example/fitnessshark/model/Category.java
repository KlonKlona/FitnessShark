package com.google.firebase.example.fitnessshark.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public enum Category {
    @SerializedName("8") ARMS   (8),
    @SerializedName("9") LEGS    (9),
    @SerializedName("10") ABS    (10),
    @SerializedName("11") CHEST (11),
    @SerializedName("12") BACK (12),
    @SerializedName("13") SHOULDERS(13)
    ;

    private final int categoryCode;
    private static Map map = new HashMap<>();

    private Category(int _categoryCode) {
        this.categoryCode = _categoryCode;
    }

    static {
        for (Category categoryCode : Category.values()) {
            map.put(categoryCode.categoryCode, categoryCode);
        }
    }

    public static Category fromCode(int code) {
        for(Category type : Category.values()) {
            if(type.getCategoryCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static Category valueOf(int categoryCode) {
        return (Category) map.get(categoryCode);
    }

    public int getCategoryCode() {
        return this.categoryCode;
    }
}

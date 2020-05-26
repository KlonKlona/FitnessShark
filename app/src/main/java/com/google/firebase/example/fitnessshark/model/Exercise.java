package com.google.firebase.example.fitnessshark.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

/**
 * Exercise POJO.
 */
@IgnoreExtraProperties
public class Exercise {
    private String name;
    private Category category;
    private List<Equipment> equipment;
    private String description;

    public Exercise(Category category, List<Equipment> equipment, String name, String description) {
        this.category = category;
        this.equipment = equipment;
        this.name = name;
        this.description = description;
    }

    public Exercise() {
        // Default constructor required for calls to DataSnapshot.getValue(Exercise.class)
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

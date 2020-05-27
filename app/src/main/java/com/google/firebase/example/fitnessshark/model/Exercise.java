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
    private int sets;
    private int reps;
    private double weight;

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Exercise(String name, String description, Category category, List<Equipment> equipment,
                    int sets, int reps, double weight) {
        this.category = category;
        this.equipment = equipment;
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
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

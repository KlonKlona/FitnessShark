/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.google.firebase.example.fitnessshark.util;

import android.content.Context;

import com.google.firebase.example.fitnessshark.model.Category;
import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.Goal;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.example.fitnessshark.model.WorkoutCategory;
import com.google.firebase.example.fitnessshark.model.WorkoutPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Workout Plans.
 */
public class WorkoutUtil {

    private static final String TAG = "WorkoutUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static final HashMap<DifficultyLevel, Double> difficultyMultiplier = new HashMap<>();
    static {
        difficultyMultiplier.put(DifficultyLevel.ELITE, 1.0);
        difficultyMultiplier.put(DifficultyLevel.ADVANCED, 0.95);
        difficultyMultiplier.put(DifficultyLevel.INTERMEDIATE, 0.9);
        difficultyMultiplier.put(DifficultyLevel.AMATEUR, 0.85);
        difficultyMultiplier.put(DifficultyLevel.BEGINNER, 0.8);
    }

    public static final HashMap<WorkoutCategory, Integer> categoryRepetitionsRange = new HashMap<>();
    static {
        categoryRepetitionsRange.put(WorkoutCategory.ENDURANCE, 20);
        categoryRepetitionsRange.put(WorkoutCategory.HYPERTROPHY, 12);
        categoryRepetitionsRange.put(WorkoutCategory.STRENGTH, 5);
        categoryRepetitionsRange.put(WorkoutCategory.POWER, 2);
    }

    public static final HashMap<WorkoutCategory, Double> categoryMultiplier = new HashMap<>();
    static {
        categoryMultiplier.put(WorkoutCategory.ENDURANCE, 0.6);
        categoryMultiplier.put(WorkoutCategory.HYPERTROPHY, 0.7);
        categoryMultiplier.put(WorkoutCategory.STRENGTH, 0.8);
        categoryMultiplier.put(WorkoutCategory.POWER, 0.9);
    }

    public static final HashMap<Goal, Double> goalMultiplier = new HashMap<>();
    static {
        goalMultiplier.put(Goal.ENDURANCE, 0.6);
        goalMultiplier.put(Goal.BUILD, 0.75);
        goalMultiplier.put(Goal.MAINTAIN, 0.7);
        goalMultiplier.put(Goal.STRENGTH, 0.85);
        goalMultiplier.put(Goal.LOSE, 0.8);
    }

    private static final HashMap<Integer, Integer> durationSets = new HashMap<>();
    static {
        durationSets.put(30, 9);
        durationSets.put(45, 13);
        durationSets.put(60, 18);
        durationSets.put(75, 22);
        durationSets.put(90, 27);
        durationSets.put(120, 36);
    }

    /**
     * Create a random Workout POJO.
     */
    public static Workout getRandom(WorkoutPlan workoutPlan) {
        Workout workout = new Workout();
        Random random = new Random();

        DifficultyLevel difficultyLevel = workoutPlan.getDifficulty();
        WorkoutCategory workoutCategory = workoutPlan.getCategory();
        int days = workoutPlan.getDaysWeek();
        int duration = workoutPlan.getDuration();



        return workout;
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static DifficultyLevel getRandomDifficulty(DifficultyLevel[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static WorkoutCategory getRandomCategory(WorkoutCategory[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }
}

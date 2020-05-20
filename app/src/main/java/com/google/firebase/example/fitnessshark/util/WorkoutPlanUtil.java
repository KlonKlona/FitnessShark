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

import com.google.firebase.example.fitnessshark.R;
import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.WorkoutCategory;
import com.google.firebase.example.fitnessshark.model.WorkoutPlan;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Restaurants.
 */
public class WorkoutPlanUtil {

    private static final String TAG = "WorkoutUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String WORKOUT_PLAN_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAMES = {
            "Full Body Workout",
            "Push / Pull / Legs",
            "Push / Pull",
            "Upper / Lower",
            "nSuns 5-3-1",
            "BBB 5-3-1",
            "Starting Strength"
    };

    /**
     * Create a random Restaurant POJO.
     */
    public static WorkoutPlan getRandom(Context context) {
        WorkoutPlan workoutPlan = new WorkoutPlan();
        Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        WorkoutCategory[] workoutCategories = new WorkoutCategory[]{WorkoutCategory.STRENGTH,
                WorkoutCategory.POWER, WorkoutCategory.HYPERTROPHY, WorkoutCategory.ENDURANCE};

        DifficultyLevel[] difficultyLevels = new DifficultyLevel[]{DifficultyLevel.ELITE,
                DifficultyLevel.ADVANCED, DifficultyLevel.INTERMEDIATE, DifficultyLevel.AMATEUR,
                DifficultyLevel.BEGINNER};

        int[] days = new int[]{1, 2, 3, 4, 5, 6};

        int[] duration = new int[]{15, 30, 45, 60, 75, 90, 105, 120};

        workoutPlan.setName(getRandomName(random));
        workoutPlan.setDifficulty(getRandomDifficulty(difficultyLevels, random));
        workoutPlan.setCategory(getRandomCategory(workoutCategories, random));
        workoutPlan.setDuration(getRandomInt(duration, random));
        workoutPlan.setDaysWeek(getRandomInt(days, random));
        workoutPlan.setPhoto(getRandomImageUrl(random));
        workoutPlan.setAvgRating(getRandomRating(random));
        workoutPlan.setNumRatings(random.nextInt(20));

        return workoutPlan;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), WORKOUT_PLAN_URL_FMT, id);
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static String getRandomName(Random random) {
        return getRandomString(random);
    }

    private static String getRandomString(Random random) {
        int ind = random.nextInt(WorkoutPlanUtil.NAMES.length);
        return WorkoutPlanUtil.NAMES[ind];
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

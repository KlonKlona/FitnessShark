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
 package com.google.firebase.example.fitnessshark;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.WorkoutCategory;
import com.google.firebase.example.fitnessshark.model.WorkoutPlan;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {

    private WorkoutCategory category = null;
    private DifficultyLevel difficulty = null;
    private int duration = -1;
    private int daysWeek = -1;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(WorkoutPlan.FIELD_AVG_RATING);
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasDifficulty() {
        return difficulty != null;
    }

    public boolean hasDuration() {
        return (duration > 0);
    }

    public boolean hasDaysWeek() {
        return (daysWeek > 0);
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getCategory() {
        return category.toString();
    }

    public void setCategory(String category) {
        this.category = WorkoutCategory.valueOf(category);
    }

    public String getDifficulty() {
        return difficulty.toString();
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = DifficultyLevel.valueOf(difficulty); }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDaysWeek() {
        return daysWeek;
    }

    public void setDaysWeek(int daysWeek) {
        this.daysWeek = daysWeek;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (category == null && difficulty == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_workout_plans));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }

        if (category != null && difficulty != null) {
            desc.append(" in ");
        }

        if (difficulty != null) {
            desc.append("<b>");
            desc.append(difficulty);
            desc.append("</b>");
        }

        if (duration > 0) {
            desc.append(" for ");
            desc.append("<b>");
            desc.append(duration);
            desc.append("</b>");
        }

        if (daysWeek > 0) {
            desc.append(" for ");
            desc.append("<b>");
            desc.append(daysWeek);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (WorkoutPlan.FIELD_DIFFICULTY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_difficulty);
        } else if (WorkoutPlan.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_popularity);
        } else {
            return context.getString(R.string.sorted_by_rating);
        }
    }
}

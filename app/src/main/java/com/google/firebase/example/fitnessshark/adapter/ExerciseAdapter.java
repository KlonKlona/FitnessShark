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
 package com.google.firebase.example.fitnessshark.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.example.fitnessshark.R;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.Rating;
import com.google.firebase.firestore.Query;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
public class ExerciseAdapter extends FirestoreAdapter<ExerciseAdapter.ViewHolder> {

    public ExerciseAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Exercise.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView descriptionView;
        TextView setsView;
        TextView repsView;
        TextView weightView;
        TextView categoryView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.exercise_item_name);
            setsView = itemView.findViewById(R.id.exercise_item_sets);
            repsView = itemView.findViewById(R.id.exercise_item_reps);
            weightView = itemView.findViewById(R.id.exercise_item_weight);
            categoryView = itemView.findViewById(R.id.exercise_item_category);
            descriptionView = itemView.findViewById(R.id.exercise_item_description);
        }

        public void bind(Exercise exercise) {
            nameView.setText(exercise.getName());
            descriptionView.setText(exercise.getDescription());
            setsView.setText(exercise.getSets() + " Sets of ");
            repsView.setText(exercise.getReps() + " Repetitions with ");
            weightView.setText(exercise.getWeight() + " kg");
            categoryView.setText(exercise.getCategory().toString());
        }
    }

}

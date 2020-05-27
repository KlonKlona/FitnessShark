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

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fitnessshark.R;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.Restaurant;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.example.fitnessshark.util.RestaurantUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Workouts.
 */
public class WorkoutAdapter extends FirestoreAdapter<WorkoutAdapter.ViewHolder> {

    public interface OnWorkoutSelectedListener {

        void onWorkoutSelected(DocumentSnapshot workout);

    }

    private OnWorkoutSelectedListener mListener;

    public WorkoutAdapter(Query query, OnWorkoutSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView numExercisesView;
        TextView numSetsView;
        TextView categoryView;
        TextView difficultyView;
        TextView durationView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.workout_item_image);
            nameView = itemView.findViewById(R.id.workout_item_name);
            numExercisesView = itemView.findViewById(R.id.workout_item_exercises);
            numSetsView = itemView.findViewById(R.id.workout_item_sets);
            categoryView = itemView.findViewById(R.id.workout_item_category);
            difficultyView = itemView.findViewById(R.id.workout_item_difficulty);
            durationView = itemView.findViewById(R.id.workout_item_duration);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnWorkoutSelectedListener listener) {

            Workout workout = snapshot.toObject(Workout.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
                    .load(workout.getPhoto())
                    .into(imageView);

            nameView.setText(workout.getName());
            numExercisesView.setText(workout.getNumExercises() + " Exercises");
            numSetsView.setText("Total " + workout.getSets() + " sets");
            categoryView.setText(workout.getCategory().toString());
            difficultyView.setText(workout.getDifficulty().toString());
            durationView.setText(workout.getDuration() + " min");

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onWorkoutSelected(snapshot);
                }
            });
        }

    }
}

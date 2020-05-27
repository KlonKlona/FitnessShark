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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fitnessshark.adapter.ExerciseAdapter;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.core.OrderBy;

public class WorkoutDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        ExerciseDialogFragment.ExerciseListener {

    private static final String TAG = "WorkoutDetail";

    public static final String KEY_WORKOUT_PLAN_ID = "key_workout_id";

    private ImageView mImageView;
    private TextView mNameView;
    private TextView mDifficultyView;
    private TextView mCategoryView;
    private TextView mDurationView;
    private TextView mDaysWeekView;
    private TextView mExercisesView;
    private TextView mSetsView;
    private ViewGroup mEmptyView;
    private RecyclerView mExerciseRecycler;

    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference mWorkoutRef;
    private ListenerRegistration mWorkoutRegistration;

    private ExerciseDialogFragment mExerciseDialog;

    private ExerciseAdapter mExerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_detail);
        
        mImageView = findViewById(R.id.workout_plan_image);
        mNameView = findViewById(R.id.workout_plan_name);
        mDifficultyView = findViewById(R.id.workout_plan_difficulty);
        mCategoryView = findViewById(R.id.workout_plan_category);
        mDurationView = findViewById(R.id.workout_plan_duration);
        mSetsView = findViewById(R.id.workout_plan_sets);
        mExercisesView = findViewById(R.id.workout_plan_exercises);
        mDaysWeekView = findViewById(R.id.workout_plan_days_week);
        mEmptyView = findViewById(R.id.view_empty_exercises);
        mExerciseRecycler = findViewById(R.id.recycler_exercises);

        findViewById(R.id.workout_plan_button_back).setOnClickListener(this);
        findViewById(R.id.fab_show_exercise_dialog).setOnClickListener(this);

        // Get workout ID from extras
        String workoutId = getIntent().getExtras().getString(KEY_WORKOUT_PLAN_ID);
        if (workoutId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_WORKOUT_PLAN_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get reference to the restaurant
        mWorkoutRef = mFirestore.collection("workouts").document(workoutId);

        // Get exercises
        Query exercisesQuery = mWorkoutRef
                .collection("exercises")
                .orderBy("weight", Query.Direction.DESCENDING);

        // RecyclerView
        mExerciseAdapter = new ExerciseAdapter(exercisesQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mExerciseRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mExerciseRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mExerciseRecycler.setLayoutManager(new LinearLayoutManager(this));
        mExerciseRecycler.setAdapter(mExerciseAdapter);

        mExerciseDialog = new ExerciseDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mExerciseAdapter.startListening();
        mWorkoutRegistration = mWorkoutRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mExerciseAdapter.stopListening();

        if (mWorkoutRegistration != null) {
            mWorkoutRegistration.remove();
            mWorkoutRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.workout_plan_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_show_exercise_dialog:
                onAddExerciseClicked(v);
                break;
        }
    }

    private Task<Void> addExercise(final DocumentReference workoutRef, final Exercise exercise) {
            // Create reference for new rating, for use inside the transaction
            final DocumentReference exerciseRef = workoutRef.collection("exercises")
                    .document();

            // In a transaction, add the new rating and update the aggregate totals
            return mFirestore.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(@NonNull Transaction transaction)
                        throws FirebaseFirestoreException {

                    Workout workout = transaction.get(workoutRef)
                            .toObject(Workout.class);

                    // Compute new number of sets
                    assert workout != null;
                    int newNumSets = workout.getSets() + exercise.getSets();

                    // Compute new number of exercises
                    int newNumExercises = workout.getNumExercises() + 1;

                    // Set new restaurant info
                    workout.setSets(newNumSets);
                    workout.setNumExercises(newNumExercises);

                    // Commit to Firestore
                    transaction.set(workoutRef, workout);
                    transaction.set(exerciseRef, exercise);

                    return null;
                }
            });        // return Tasks.forException(new Exception("not yet implemented"));
    }

    /**
     * Listener for the Workout document ({@link #mWorkoutRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "workout:onEvent", e);
            return;
        }

        onWorkoutLoaded(snapshot.toObject(Workout.class));
    }

    private void onWorkoutLoaded(Workout workout) {

        mNameView.setText(workout.getName());
        mDifficultyView.setText(workout.getDifficulty().toString());
        mCategoryView.setText(workout.getCategory().toString());
        mDurationView.setText(workout.getDuration() + " min");
        mDaysWeekView.setText(workout.getDaysOfWeek().size() + " Days / Week");
        mExercisesView.setText(workout.getNumExercises() + " Exercises");
        mSetsView.setText("(Total " + workout.getSets() + " Sets)");

        // Background image
        Glide.with(mImageView.getContext())
                .load(workout.getPhoto())
                .into(mImageView);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddExerciseClicked(View view) {
        mExerciseDialog.show(getSupportFragmentManager(), ExerciseDialogFragment.TAG);
    }

    public void onExercise(Exercise exercise) {
        // In a transaction, add the new rating and update the aggregate totals
        addExercise(mWorkoutRef, exercise)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Workout added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mExerciseRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add workout failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

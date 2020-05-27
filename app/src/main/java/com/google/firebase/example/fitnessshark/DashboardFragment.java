package com.google.firebase.example.fitnessshark;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fitnessshark.adapter.ExerciseAdapter;
import com.google.firebase.example.fitnessshark.adapter.WorkoutAdapter;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import com.google.firebase.example.fitnessshark.viewmodel.DashboardViewModel;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardFragment extends Fragment implements
            View.OnClickListener {

    private static final String TAG = "DashboardFragment";

    private static final int LIMIT = 50;

    private FirebaseUser mUser;
    private DocumentReference mUserRef;
    private DocumentReference mUserWorkout;
    private UserProfile mUserProfile;

    private AppCompatButton mBackButton;
    private AppCompatButton mOpenButton;

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
    private QueryDocumentSnapshot mUserWorkoutDocument;
    private ExerciseDialogFragment mExerciseDialog;
    private ExerciseAdapter mExerciseAdapter;
    private ListenerRegistration mWorkoutRegistration;

    private Workout mWorkout;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private DashboardViewModel mViewModel;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initFirestore();
        getWorkoutRef();


        View v = inflater.inflate(R.layout.activity_workout_plan_detail, container, false);

        mImageView = v.findViewById(R.id.workout_plan_image);
        mNameView = v.findViewById(R.id.workout_plan_name);
        mDifficultyView = v.findViewById(R.id.workout_plan_difficulty);
        mCategoryView = v.findViewById(R.id.workout_plan_category);
        mDurationView = v.findViewById(R.id.workout_plan_duration);
        mSetsView = v.findViewById(R.id.workout_plan_sets);
        mExercisesView = v.findViewById(R.id.workout_plan_exercises);
        mDaysWeekView = v.findViewById(R.id.workout_plan_days_week);
        mEmptyView = v.findViewById(R.id.view_empty_exercises);
        mExerciseRecycler = v.findViewById(R.id.recycler_exercises);

        v.findViewById(R.id.workout_plan_button_back).setOnClickListener(this);
        v.findViewById(R.id.fab_show_exercise_dialog).setOnClickListener(this);

        mExerciseRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mExerciseRecycler.setAdapter(mExerciseAdapter);

        mExerciseDialog = new ExerciseDialogFragment();

        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();

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
                break;
            case R.id.fab_show_exercise_dialog:
                onWorkoutSelected(mUserWorkoutDocument);
                break;
        }
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

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        checkIfProfileExist();
    }

    private void getWorkoutRef() {
        mFirestore.collection("workouts")
                .whereEqualTo("name", mUser.getUid() + "workout")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No Workout in Database!");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mUserWorkout = document.getReference();
                                mUserWorkoutDocument = document;
                                mWorkout = mUserWorkoutDocument.toObject(Workout.class);
                            }
                            // Get exercises
                            Query exercisesQuery = mUserWorkout
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

                            mExerciseAdapter.startListening();
//                            mWorkoutRegistration = mUserWorkout.addSnapshotListener((EventListener<DocumentSnapshot>) mUserWorkoutDocument);

                            onWorkoutLoaded(mWorkout);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void onWorkoutSelected(DocumentSnapshot workout) {
        // Go to the details page for the selected workout
        Intent intent = new Intent(this.getContext(), WorkoutDetailActivity.class);
        intent.putExtra(WorkoutDetailActivity.KEY_WORKOUT_PLAN_ID, workout.getId());

        startActivity(intent);
    }

    private void checkIfProfileExist() {
        mFirestore.collection("users")
                .whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No Profile in Database!");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mUserProfile = document.toObject(UserProfile.class);
                                mUserRef = document.getReference();
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}

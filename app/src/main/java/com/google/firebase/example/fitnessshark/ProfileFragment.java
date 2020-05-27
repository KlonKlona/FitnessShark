package com.google.firebase.example.fitnessshark;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.example.fitnessshark.model.Exercise;
import com.google.firebase.example.fitnessshark.model.WorkoutCategory;
import com.google.firebase.example.fitnessshark.util.WorkoutBuilder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fitnessshark.model.Category;
import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.Equipment;
import com.google.firebase.example.fitnessshark.model.Goal;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.example.fitnessshark.model.Workout;
import com.google.firebase.example.fitnessshark.util.WorkoutBuilder;
import com.google.firebase.example.fitnessshark.viewmodel.ProfileViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DayOfWeek;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ProfileFragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "ProfileFragment";
    private static final int MAX_IMAGE_NUM = 20;
    private static final String WORKOUT_PLAN_URL_FMT =
            "http://clipart-library.com/images_k/workout-silhouette-png/workout-silhouette-png-%d.png";

    private ProfileViewModel mViewModel;
    private FragmentActivity myContext;
    private boolean hasAbs;
    private boolean hasArms;
    private boolean hasBack;
    private boolean hasLegs;
    private boolean hasChest;
    private boolean hasShoulders;
    private boolean hasExercises;

    private AppCompatButton mSaveButton;
    private AppCompatButton mGenerateButton;
    private Toolbar mToolbar;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FirebaseUser mUser;
    private DocumentReference mUserRef;
    private DocumentReference mUserWorkout;
    private UserProfile mUserProfile;

    private Spinner mGoalSpinner;
    private Spinner mExperienceSpinner;
    private Spinner mDurationSpinner;
    private Spinner mDaysWeekSpinner;

    private EditText mDeadliftMax;
    private EditText mSquatMax;
    private EditText mBenchMax;
    private EditText mPressMax;
    private ProgressDialog dialog;

    private ArrayList<Exercise> fetchedExercises;
    private Workout mWorkout;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        dialog = new ProgressDialog(this.getContext());

        FragmentManager fragManager = myContext.getFragmentManager();
        fragManager
                .beginTransaction()
                .commit();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mWorkout = new Workout("default");
        fetchedExercises = new ArrayList<>();
        hasAbs = false;
        hasArms = false;
        hasBack = false;
        hasLegs = false;
        hasChest = false;
        hasShoulders = false;
        hasExercises = false;

        // View model
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        mToolbar = v.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mSaveButton = v.findViewById(R.id.ButtonSubmit);
        mSaveButton.setOnClickListener(this);

        mGenerateButton = v.findViewById(R.id.ButtonGenerate);
        mGenerateButton.setOnClickListener(this);

        mGoalSpinner = v.findViewById(R.id.SpinnerGoal);
        mExperienceSpinner = v.findViewById(R.id.SpinnerExperience);
        mDurationSpinner = v.findViewById(R.id.SpinnerDuration);
        mDaysWeekSpinner = v.findViewById(R.id.SpinnerDaysWeek);

        mDeadliftMax = v.findViewById(R.id.EditMaxDeadlift);
        mSquatMax = v.findViewById(R.id.EditMaxSquat);
        mBenchMax = v.findViewById(R.id.EditMaxBench);
        mPressMax = v.findViewById(R.id.EditMaxPress);

        initFirestore();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

//        mViewModel.getUserProfile().observe(getViewLifecycleOwner(), users -> {
//            // TODO: Consider implementing with ViewModel
//        });
    }

    @Override
    public void onStart() {
        super.onStart();

        checkIfProfileExistAndUpdateSpinners();
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonSubmit:
                submit();
                break;
            case R.id.ButtonGenerate:
                generatePlan();
                break;
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        mQuery = mFirestore.collection("users");
    }

    private boolean profileExists() {
        return (mViewModel.getDoesProfileExists());
    }

    private void checkIfProfileExistAndUpdateSpinners() {
        mFirestore.collection("users")
                .whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No Profile in Database!");
                            mViewModel.setDoesProfileExists(false);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mViewModel.setDoesProfileExists(true);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mUserProfile = document.toObject(UserProfile.class);
                                mUserRef = document.getReference();
                                updateSpinners();
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        mViewModel.setDoesProfileExists(false);
                    }
                });
    }

    private void updateSpinners() {
        Goal goalValue = mUserProfile.getGoal();
        mGoalSpinner.setSelection(getIndex(mGoalSpinner, goalValue.toString()));

        DifficultyLevel difficultyLevelValue = mUserProfile.getExperience();
        mExperienceSpinner.setSelection(getIndex(mExperienceSpinner, difficultyLevelValue.toString()));

        int durationValue = mUserProfile.getPrefDuration();
        mDurationSpinner.setSelection(getIndex(mDurationSpinner, durationValue));

        int daysWeekValue = mUserProfile.getPrefDaysWeek();
        mDaysWeekSpinner.setSelection(getIndex(mDaysWeekSpinner, daysWeekValue));

        mDeadliftMax.setText(String.valueOf(mUserProfile.getDeadliftMax()));
        mSquatMax.setText(String.valueOf(mUserProfile.getSquatMax()));
        mBenchMax.setText(String.valueOf(mUserProfile.getBenchMax()));
        mPressMax.setText(String.valueOf(mUserProfile.getPressMax()));
    }

    public void submit() {
        final String userId = mUser.getUid();
        mFirestore.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No Profile in Database!");
                            addUser(userId);
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                    DocumentReference userRef = document.getDocumentReference();
//                                    updateUser(userId, userRef);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateUser(String userId, DocumentReference userRef) {
//        CollectionReference users = mFirestore.collection("users");
//
//        final Spinner goalField = getActivity().findViewById(R.id.SpinnerGoal);
//        Goal goal = Goal.valueOf(goalField.getSelectedItem().toString()
//                .substring(0, goalField.getSelectedItem().toString().indexOf(" ")).toUpperCase());
//
//        final Spinner experienceField = getActivity().findViewById(R.id.SpinnerExperience);
//        DifficultyLevel experience = DifficultyLevel.valueOf(experienceField.getSelectedItem().toString()
//                .substring(0, experienceField.getSelectedItem().toString().indexOf(" ")).toUpperCase());
//
//        final Spinner durationField = getActivity().findViewById(R.id.SpinnerDuration);
//        int duration = Integer.parseInt(durationField.getSelectedItem().toString()
//                .substring(0, durationField.getSelectedItem().toString().indexOf(" ")));
//
//        final Spinner daysWeekField = getActivity().findViewById(R.id.SpinnerDaysWeek);
//        int daysWeek = Integer.parseInt(daysWeekField.getSelectedItem().toString()
//                .substring(0, daysWeekField.getSelectedItem().toString().indexOf(" ")));
//
//        UserProfile userProfile = new UserProfile(userId, goal, experience, duration, daysWeek);
//        userRef.update(userProfile);
    }

    private void addUser(String userId) {
        mUserRef = mFirestore.collection("users").document();

        Goal goal = Goal.valueOf(mGoalSpinner.getSelectedItem().toString()
                .substring(0, mGoalSpinner.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        DifficultyLevel experience = DifficultyLevel.valueOf(mExperienceSpinner.getSelectedItem().toString()
                .substring(0, mExperienceSpinner.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        int duration = Integer.parseInt(mDurationSpinner.getSelectedItem().toString()
                .substring(0, mDurationSpinner.getSelectedItem().toString().indexOf(" ")));

        int daysWeek = Integer.parseInt(mDaysWeekSpinner.getSelectedItem().toString()
                .substring(0, mDaysWeekSpinner.getSelectedItem().toString().indexOf(" ")));

        int deadliftMax = Integer.parseInt(mDeadliftMax.getText().toString());

        int squatMax = Integer.parseInt(mSquatMax.getText().toString());

        int benchMax = Integer.parseInt(mBenchMax.getText().toString());

        int pressMax = Integer.parseInt(mPressMax.getText().toString());

        UserProfile userProfile = new UserProfile(userId, goal, experience, duration, daysWeek,
                deadliftMax, squatMax, benchMax, pressMax);
        mUserRef.set(userProfile);

        mUserProfile = userProfile;
        mViewModel.setDoesProfileExists(true);
    }

    private void generatePlan() {
        if (profileExists()) {
            Log.d(TAG, "Generating new plan...");
            generateWorkout();
        } else {
            dialog.setMessage("You have to update your profile in order to generate workout");
        }
    }

    private boolean workoutIsGenerated() {
        return mViewModel.getIsWorkoutGenerated();
    }

    public void generateWorkout() {
        Random random = new Random();
        ArrayList<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
        fetchedExercises = new ArrayList<Exercise>();
        mUserWorkout = mFirestore.collection("workouts").document();

        mWorkout = new Workout("default");
        hasAbs = false;
        hasArms = false;
        hasBack = false;
        hasLegs = false;
        hasShoulders = false;
        hasChest = false;

        mWorkout.setName(mUserProfile.getUserId() + "workout");

        Log.d(TAG, "Getting exercise list and setting sets, reps and weights");
        for (Category category : Category.values()) {
            addExercises(category);
        }

        switch (mUserProfile.getGoal()) {
            case ENDURANCE:
                mWorkout.setCategory(WorkoutCategory.ENDURANCE);
                break;
            case STRENGTH:
                mWorkout.setCategory(WorkoutCategory.POWER);
            case LOSE:
                mWorkout.setCategory(WorkoutCategory.STRENGTH);
                break;
            case BUILD:
            case MAINTAIN:
                mWorkout.setCategory(WorkoutCategory.HYPERTROPHY);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mUserProfile.getGoal());
        }
        
        switch (mUserProfile.getExperience()) {
            case BEGINNER:
                mWorkout.setDifficulty(DifficultyLevel.BEGINNER);
                break;
            case AMATEUR:
                mWorkout.setDifficulty(DifficultyLevel.AMATEUR);
                break;
            case INTERMEDIATE:
                mWorkout.setDifficulty(DifficultyLevel.INTERMEDIATE);
                break;
            case ADVANCED:
                mWorkout.setDifficulty(DifficultyLevel.ADVANCED);
                break;
            case ELITE:
                mWorkout.setDifficulty(DifficultyLevel.ELITE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mUserProfile.getExperience());
        }

        switch (mUserProfile.getPrefDaysWeek()) {
            case 2:
                daysOfWeek.add(DayOfWeek.MONDAY);
                daysOfWeek.add(DayOfWeek.THURSDAY);
                mWorkout.setDaysOfWeek(daysOfWeek);
                break;
            case 3:
                daysOfWeek.add(DayOfWeek.MONDAY);
                daysOfWeek.add(DayOfWeek.WEDNESDAY);
                daysOfWeek.add(DayOfWeek.FRIDAY);
                mWorkout.setDaysOfWeek(daysOfWeek);
                break;
            case 4:
                daysOfWeek.add(DayOfWeek.MONDAY);
                daysOfWeek.add(DayOfWeek.TUESDAY);
                daysOfWeek.add(DayOfWeek.THURSDAY);
                daysOfWeek.add(DayOfWeek.FRIDAY);
                mWorkout.setDaysOfWeek(daysOfWeek);
                break;
            case 5:
                daysOfWeek.add(DayOfWeek.MONDAY);
                daysOfWeek.add(DayOfWeek.TUESDAY);
                daysOfWeek.add(DayOfWeek.WEDNESDAY);
                daysOfWeek.add(DayOfWeek.THURSDAY);
                daysOfWeek.add(DayOfWeek.FRIDAY);
                mWorkout.setDaysOfWeek(daysOfWeek);
                break;
            case 6:
                daysOfWeek.add(DayOfWeek.MONDAY);
                daysOfWeek.add(DayOfWeek.TUESDAY);
                daysOfWeek.add(DayOfWeek.WEDNESDAY);
                daysOfWeek.add(DayOfWeek.THURSDAY);
                daysOfWeek.add(DayOfWeek.FRIDAY);
                daysOfWeek.add(DayOfWeek.SATURDAY);
                mWorkout.setDaysOfWeek(daysOfWeek);
                break;
        }

        mWorkout.setDuration(mUserProfile.getPrefDuration());
        mWorkout.setPhoto(getRandomImageUrl(random));
    }

    private void pickExercises(Workout workout) {
        Collections.shuffle(fetchedExercises);
        for (Exercise exercise : fetchedExercises) {
            switch (exercise.getCategory()) {
                case ABS:
                    if (!hasAbs) {
                        workout.addExercise(exercise);
                        hasAbs = true;
                    }
                    break;
                case ARMS:
                    if (!hasArms) {
                        workout.addExercise(exercise);
                        hasArms = true;
                    }
                    break;
                case BACK:
                    if (!hasBack) {
                        workout.addExercise(exercise);
                        hasBack = true;
                    }
                    break;
                case LEGS:
                    if (!hasLegs) {
                        workout.addExercise(exercise);
                        hasLegs = true;
                    }
                    break;
                case CHEST:
                    if (!hasChest) {
                        workout.addExercise(exercise);
                        hasChest = true;
                    }
                    break;
                case SHOULDERS:
                    if (!hasShoulders) {
                        workout.addExercise(exercise);
                        hasShoulders = true;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + exercise.getCategory());
            }
        }
    }

    public void addExercises(Category category) {
        mFirestore.collection("exercisesApi")
                .whereEqualTo("category", category)
                .whereArrayContains("equipment", Equipment.BARBELL)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No matching exercises!");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Exercise exercise = document.toObject(Exercise.class);
                                Log.d(TAG, "Exercise: " + exercise.getName());
                                addSetsRepsAndWeight(exercise);
                                fetchedExercises.add(exercise);
                            }

                            pickExercises(mWorkout);
                            mWorkout.setNumExercises(mWorkout.getExercises().size());
                            mWorkout.setSets(0);
                            int numSets = 0;
                            for (Exercise exercise : mWorkout.getExercises()) {
                                numSets += exercise.getSets();
                            }
                            mWorkout.setSets(numSets);

                            mUserRef.update("currentWorkout", mWorkout)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                            mUserWorkout.set(mWorkout);

                            if (hasChest == hasAbs == hasArms == hasBack == hasLegs == hasShoulders &&
                                    !hasExercises && mWorkout.getNumExercises() == 6) {
                                for (Exercise exercise : mWorkout.getExercises()) {
                                    mUserWorkout.collection("exercises").add(exercise);
                                }
                                hasExercises = true;
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents in addExercises: ", task.getException());
                    }
                });
    }

    public void addSetsRepsAndWeight(Exercise exercise) {
        switch (exercise.getCategory()) {
            case LEGS:
                exercise.setWeight(mUserProfile.getSquatMax() *
                        (Double) WorkoutBuilder.difficultyMultiplier.get(mUserProfile.getExperience()) *
                        (Double) WorkoutBuilder.goalMultiplier.get(mUserProfile.getGoal()));
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            case BACK:
                exercise.setWeight(mUserProfile.getDeadliftMax() / 2 *
                        (Double) WorkoutBuilder.difficultyMultiplier.get(mUserProfile.getExperience()) *
                        (Double) WorkoutBuilder.goalMultiplier.get(mUserProfile.getGoal()));
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            case CHEST:
                exercise.setWeight(mUserProfile.getBenchMax() *
                        (Double) WorkoutBuilder.difficultyMultiplier.get(mUserProfile.getExperience()) *
                        (Double) WorkoutBuilder.goalMultiplier.get(mUserProfile.getGoal()));
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            case SHOULDERS:
                exercise.setWeight(mUserProfile.getPressMax() *
                        (Double) WorkoutBuilder.difficultyMultiplier.get(mUserProfile.getExperience()) *
                        (Double) WorkoutBuilder.goalMultiplier.get(mUserProfile.getGoal()));
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            case ARMS:
                exercise.setWeight(mUserProfile.getPressMax() / 2 *
                        (Double) WorkoutBuilder.difficultyMultiplier.get(mUserProfile.getExperience()) *
                        (Double) WorkoutBuilder.goalMultiplier.get(mUserProfile.getGoal()));
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            case ABS:
                exercise.setWeight(0);
                exercise.setSets(WorkoutBuilder.durationSets.get(mUserProfile.getPrefDuration()));
                exercise.setReps(WorkoutBuilder.goalRepetitionsRange.get(mUserProfile.getGoal()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + exercise.getCategory());
        }
    }

    private int getIndex(Spinner spinner, int myInteger){
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString()
                    .substring(0, spinner.getItemAtPosition(i).toString().indexOf(" "))
                    .equalsIgnoreCase(String.valueOf(myInteger))) {
                return i;
            }
        }
        return 0;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString()
                    .substring(0, spinner.getItemAtPosition(i).toString().indexOf(" "))
                    .equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), WORKOUT_PLAN_URL_FMT, id);
    }
}

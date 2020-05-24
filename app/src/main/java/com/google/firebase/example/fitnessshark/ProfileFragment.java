package com.google.firebase.example.fitnessshark;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.Goal;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.example.fitnessshark.viewmodel.ProfileViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;
    private FragmentActivity myContext;

    private AppCompatButton mSaveButton;
    private AppCompatButton mGenerateButton;
    private Toolbar mToolbar;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FirebaseUser mUser;
    private DocumentReference mUserRef;
    private UserProfile mUserProfile;

    private Spinner mGoalSpinner;
    private Spinner mExperienceSpinner;
    private Spinner mDurationSpinner;
    private Spinner mDaysWeekSpinner;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        FragmentManager fragManager = myContext.getFragmentManager();
        fragManager
                .beginTransaction()
                .commit();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        initFirestore();

        checkIfProfileExistAndUpdateSpinners();

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
        CollectionReference users = mFirestore.collection("users");

        final Spinner goalField = getActivity().findViewById(R.id.SpinnerGoal);
        Goal goal = Goal.valueOf(goalField.getSelectedItem().toString()
                .substring(0, goalField.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        final Spinner experienceField = getActivity().findViewById(R.id.SpinnerExperience);
        DifficultyLevel experience = DifficultyLevel.valueOf(experienceField.getSelectedItem().toString()
                .substring(0, experienceField.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        final Spinner durationField = getActivity().findViewById(R.id.SpinnerDuration);
        int duration = Integer.parseInt(durationField.getSelectedItem().toString()
                .substring(0, durationField.getSelectedItem().toString().indexOf(" ")));

        final Spinner daysWeekField = getActivity().findViewById(R.id.SpinnerDaysWeek);
        int daysWeek = Integer.parseInt(daysWeekField.getSelectedItem().toString()
                .substring(0, daysWeekField.getSelectedItem().toString().indexOf(" ")));

        UserProfile userProfile = new UserProfile(userId, goal, experience, duration, daysWeek);
        users.add(userProfile);

        mViewModel.setDoesProfileExists(true);
    }

    private void generatePlan() {
        Log.d(TAG, "Generating new plan...");

    }

    private int getIndex(Spinner spinner, int myString){
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString()
                    .substring(0, spinner.getItemAtPosition(i).toString().indexOf(" "))
                    .equalsIgnoreCase(String.valueOf(myString))) {
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
}

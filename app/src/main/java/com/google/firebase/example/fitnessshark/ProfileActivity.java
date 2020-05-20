package com.google.firebase.example.fitnessshark;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.example.fitnessshark.model.DifficultyLevel;
import com.google.firebase.example.fitnessshark.model.Goal;
import com.google.firebase.example.fitnessshark.model.UserProfile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private Toolbar mToolbar;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(mToolbar);

        initFirestore();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        // mQuery = mFirestore.collection("users").;
    }

    public void submit(View button) {
        CollectionReference users = mFirestore.collection("users");

        String userId = getIntent().getStringExtra("USER_ID");

        final Spinner goalField = findViewById(R.id.SpinnerGoal);
        Goal goal = Goal.valueOf(goalField.getSelectedItem().toString()
                .substring(0, goalField.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        final Spinner experienceField = findViewById(R.id.SpinnerExperience);
        DifficultyLevel experience = DifficultyLevel.valueOf(experienceField.getSelectedItem().toString()
                .substring(0, experienceField.getSelectedItem().toString().indexOf(" ")).toUpperCase());

        final Spinner durationField = findViewById(R.id.SpinnerDuration);
        int duration = Integer.parseInt(durationField.getSelectedItem().toString()
                .substring(0, durationField.getSelectedItem().toString().indexOf(" ")));

        final Spinner daysWeekField = findViewById(R.id.SpinnerDaysWeek);
        int daysWeek = Integer.parseInt(daysWeekField.getSelectedItem().toString()
                .substring(0, daysWeekField.getSelectedItem().toString().indexOf(" ")));

        UserProfile userProfile = new UserProfile(userId, goal, experience, duration, daysWeek);
        users.add(userProfile);
    }
}